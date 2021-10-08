<style lang="less" scoped>
@import "../../../styles/table-common.less";
@import "./alertManage.less";
</style>
<template>
  <div class="search">
    <Card v-if="currview === 'index'">
      <div class="form">
        <a-form-item v-model="searchForm.name" label="主机名称" placeholder="请输入主机名称"></a-form-item>
        <a-form-item type="slot">
          <slot>
             <label name="timeRange" class="mh-input__label margin-right-10">请选择日期范围</label>
             <DatePicker type="datetimerange" v-model="timeRange" placeholder="请选择日期范围" style="width: 385px"></DatePicker>
          </slot>
        </a-form-item>
        <a-form-item type="slot">
          <slot>
            <Button @click="handleSearch" type="primary" icon="ios-search" class="margin-right-10">搜索</Button>
            <Button @click="handleReset">重置</Button>
          </slot>
        </a-form-item>
      </div>

      <Row class="operation" type="flex" justify="space-between">
        <Row>
          <Button @click="delAll" icon="md-trash" v-permission="['ROLE_ADMIN', 'ROLE_TEST']">批量删除</Button>
          <Button @click="refresh" icon="md-refresh">刷新</Button>
        </Row>
        <Row>
          <RadioGroup v-model="action" @on-change="changeService" type="button" style="margin-right:25px">
           <Radio v-for="item in actionList" :label="item.value" :key="item.value">{{item.label}}</Radio>
          </RadioGroup>
           <setting-table-column 
              :columns="action === 'alert' ? columns : columnsType"
              v-show="['alert',  'type'].includes(action)"
              @setting-change="handleSettingChange"></setting-table-column>
        </Row>
      </Row>
      <Row>
        <Table v-if="action === 'alert'"
          :loading="loading"
          border
          :columns="columnsSettings"
          :data="data"
          ref="table"
          sortable="custom"
          @on-sort-change="changeSort"
          @on-selection-change="changeSelect"
        ></Table>
        <Table v-if="action === 'service'"
          :loading="loading"
          border
          :columns="columnsService"
          :data="data"
          ref="table"
          sortable="custom"
          @on-sort-change="changeSort"
          @on-selection-change="changeSelect"
        ></Table>
        <Table v-if="action === 'type'"
          :loading="loading"
          border
          :columns="columnsTypeSettings"
          :data="data"
          ref="table"
          sortable="custom"
          @on-sort-change="changeSort"
          @on-selection-change="changeSelect"
        ></Table>
      </Row>
      <Row type="flex" justify="end" class="page">
        <Page
          :current="pageNumber"
          :total="total"
          :page-size="pageSize"
          @on-change="changePage"
          @on-page-size-change="changePageSize"
          :page-size-opts="[10,20,50]"
          size="small"
          show-total
          show-elevator
          show-sizer
        ></Page>
      </Row>
    </Card>
    <template>
      <alerts-results :param="resultProps" v-if="currview === 'result'" @close="closeAlertsResult"></alerts-results>
    </template>
    <Modal  v-model="visiable"
            :mask-closable="false"
            title="注册新故障">
      <Form label-position="left" :label-width="80" ref="form" class="fixed-add" :model="form" :rules="rules">
        <FormItem label="大类" prop="category">
          <Input v-model="form.category"/>
        </FormItem>
        <FormItem label="小类" prop="subCategory">
          <Input v-model="form.subCategory"/>
        </FormItem>
        <FormItem label="描述" prop="description">
          <Input type="textarea" v-model="form.description"/>
        </FormItem>
      </Form >
      <template slot="footer">
        <Button @click="visiable = false;">取消</Button>
        <Button type="primary" @click="confirm">确定</Button>
      </template>
    </Modal>
  </div>
</template>

<script>
import { getAlertList, deleteAlert, registerError } from "@/api/index";
import util from "@/libs/util.js";
import AFromItem from '@views/my-components/form/FormItem';
import { formatDate } from '@/libs/date';
import AlertsResults from "@views/alert/alert-manage/AlertsResults";
import SettingTableColumn from "@views/my-components/settingTableColumn/SettingTableColumn";
export default {
  name: "alert-manage",
  components: {
    "a-form-item": AFromItem,
    AlertsResults,
   'setting-table-column': SettingTableColumn
  },
  computed: {
    columnsSettings() {
      return this.columns ? this.columns.filter(item => item.isSetting) : []
    },
    columnsTypeSettings() {
       return this.columnsType ? this.columnsType.filter(item => item.isSetting) : []
    }
  },
  data() {
    return {
      openTip: false,
      openLevel: "0",
      loading: true,
      searchKey: "",
      sortColumn: "gmt_create",
      sortType: "desc",
      modalType: 0,
      modalTitle: "",
      modalVisible: false,
      detailModalTitle: "",
      detailModalVisible: false,
      currentAlert: null,
      form: {
        category: '',
        subCategory: '',
        description: ''
      },
      rules: {
        category: [
          {required: true, message: '阈值最少需要4个字符', trigger: 'blur', min:  4}
        ],
        subCategory: [
          {required: true, message: '阈值最少需要4个字符', trigger: 'blur', min:  4}
        ]
      },
      formValidate: {},
      searchForm: {
        name: ""
      },
      timeRange: [new Date(new Date().getTime() - 604800000), new Date()],
      submitLoading: false,
      selectList: [],
      selectCount: 0,
      action: 'alert',
      visiable: false,
      selectRow: {},
      resultProps: {},
      currview: 'index',
      actionList: [
        {
          label: '按告警明细统计',
          value: 'alert'
        },
        {
          label: '按实例统计',
          value: 'service'
        },
        {
          label: '按告警类型统计',
          value: 'type'
        }
      ],
      columns: [
        {
          type: "selection",
          width: 60,
          align: "center",
          title: '请选择',
          isSetting: true
        },
        {
          title: "主机名称",
          key: "hostName",
          sortable: false,
          width: 120,
          tooltip: true,
          ellipsis: true,
          isSetting: true
        },
        {
          title: "服务名称",
          key: "service",
          sortable: false,
          width: 120,
          tooltip: true,
          ellipsis: true,
          isSetting: true
        },
        {
          title: "告警信息",
          key: "errMsg",
          sortable: false,
          ellipsis: true,
          tooltip: true,
          width: 150,
          tooltip: true,
          ellipsis: true,
          isSetting: true
        },
        {
          title: "类型",
          key: "type",
          sortable: false,
          width: 120,
          tooltip: true,
          ellipsis: true,
          isSetting: true
        },
        {
          title: "子类型",
          key: "errType",
          sortable: false,
          width: 120,
          tooltip: true,
          ellipsis: true,
          isSetting: true
        },
        {
          title: "级别",
          key: "errLevel",
          sortable: false,
          width: 120,
          tooltip: true,
          ellipsis: true,
          isSetting: true
        },
        {
          title: "角色组",
          key: "sendGroup",
          sortable: false,
          width: 120,
          tooltip: true,
          ellipsis: true,
          isSetting: document.documentElement.clientWidth >=1300 ? true : false
        },
        {
          title: "负责人",
          key: "fixedBy",
          sortable: false,
          width: 120,
          tooltip: true,
          ellipsis: true,
          isSetting: document.documentElement.clientWidth >=1300 ? true : false
        },
        {
          title: "操作",
          key: "action",
          align: "center",
          width: 150,
          isSetting: true,
          render: (h, params) => {
            return h("div", [
              params.row.fixedBy ? h(
                "Button",
                {
                  props: {
                    type: "primary",
                    size: "small",
                    icon: 'ios-eye-outline'
                  },
                  on: {
                    click: () => {
                      this.resultProps = params.row;
                      this.currview = 'result';
                    }
                  }
                },
                "已处理"
                )
              :
              h(
                "Button",
                {
                  props: {
                    type: "primary",
                    size: "small",
                    icon: 'ios-eye-outline'
                  },
                  on: {
                    click: () => {
                      this.handle(params.row);
                      this.selectRow = params.row;
                    }
                  }
                },
                "待处理"
              )
            ]);
          }
        }
      ],
      columnsService: [
        {
          title:'最早告警时间',
          key: 'minCreate'
        },
        {
          title: '实例',
          key: 'service'
        },
        {
          title: '告警总数',
          key: 'cnt'
        },
        {
          title:'最近告警时间',
          key: 'gmtCreate'
        }
      ],
      columnsType: [
        {
          title: '最早告警时间',
          key: 'minCreate',
          width: 200,
          tooltip: true,
          ellipsis: true,
          isSetting: true
        },
        {
          title: '主机名',
          key: 'name',
          width: 120,
          tooltip: true,
          ellipsis: true,
          isSetting: true
        },
        {
          title: '实例',
          key: 'service',
          width: 120,
          tooltip: true,
          ellipsis: true,
          isSetting: true
        },
        {
          title: '类型',
          key: 'errType',
          width: 120,
          tooltip: true,
          ellipsis: true,
          isSetting: true
        },
        {
          title: '子类型',
          key: 'type',
          width: 120,
          tooltip: true,
          ellipsis: true,
          isSetting: true
        },
        {
          title: '告警项目',
          key: 'hostName',
          width: 120,
          tooltip: true,
          ellipsis: true,
          isSetting: true
        },
        {
          title: '告警次数',
          key: 'cnt',
          width: 120,
          tooltip: true,
          ellipsis: true,
          isSetting: document.documentElement.clientWidth >=1300 ? true : false
        },
        {
          title: '最近告警时间',
          key: 'gmtCreate',
          width: 200,
          tooltip: true,
          ellipsis: true,
          isSetting: true
        }
      ],
      data: [],
      pageNumber: 1,
      pageSize: 10,
      total: 0,
      selectAllFlag: false,
      dataType: 0,
      groupList: []
    };
  },
  methods: {
    //初始化
    init() {
      this.getAlertList();
    },
    //改变当前页
    changePage(v) {
      this.pageNumber = v;
      this.getAlertList();
      this.clearSelectAll();
    },
    //改变页码
    changePageSize(v) {
      this.pageSize = v;
      this.getAlertList();
    },
    changeSort(e) {
      this.sortColumn = e.key;
      this.sortType = e.order;
      if (e.order == "normal") {
        this.sortType = "";
      }
      this.getAlertList();
    },
    getAlertList() {
      this.loading = true;
      let params = {
        pageNumber: this.pageNumber,
        pageSize: this.pageSize,
        sort: this.sortColumn,
        order: this.sort,
        startTime: formatDate(this.timeRange[0], "yyyy-MM-dd hh:mm:ss"),
        endTime: formatDate(this.timeRange[1], "yyyy-MM-dd hh:mm:ss"),
        hostName: this.searchForm.name.trim(),
      };
      getAlertList(params, this.action).then(res => {
        this.loading = false;
        if (res.success) {
          this.data = res.result.records;
          this.total = res.result.total;
        }
      });
    },
    cancel() {
      this.modalVisible = false;
    },
    handle(v) {
      const _this = this;
      _this.$refs.form.resetFields();
      _this.visiable = true;
    },

    clearSelectAll() {
      this.$refs.table.selectAll(false);
    },
    changeSelect(e) {
      this.selectList = e;
      this.selectCount = e.length;
    },
    //删除告警
    delAll() {
      if (this.selectCount <= 0) {
        this.$Message.warning("您还未选择要删除的数据");
        return;
      }
      //确认提示框
      this.$Modal.confirm({
        title: "确认删除",
        content: "您确认要删除所选的 " + this.selectCount + " 条数据?",
        loading: true,
        onOk: () => {
          let ids = "";
          this.selectList.forEach(function(e) {
            ids += e.id + ",";
          });
          ids = ids.substring(0, ids.length - 1);
          deleteAlert(ids).then(res => {
            this.$Modal.remove();
            if (res.success) {
              this.$Message.success("删除成功");
              this.clearSelectAll();
              this.getAlertList();
            }
          });
        }
      });
    },
    //搜索防抖处理
    handleSearch: util.debounce(function() {
      const _this = this;
      _this.pageNumber = 1;
      _this.clearSelectAll();
      _this.getAlertList();
    }, 500),
    //重置防抖处理
    handleReset: util.debounce(function() {
      const _this = this;
      _this.pageNumber = 1;
      _this.searchForm.name = '';
      _this.clearSelectAll();
      _this.getAlertList();
    }, 500),
    //刷新防抖处理
    refresh: util.debounce(function() {
      this.init();
    }, 500),
    changeService(v) {
      const _this = this;
      _this.action = v;
      _this.pageNumber = 1;
      _this.getAlertList();
    },
    confirm() {
      const _this = this;
      _this.$refs.form.validate(validate => {
         if(validate) {
           registerError({
             ..._this.selectRow,
             subKind: _this.form.subCategory,
             kind: _this.form.category,
             fixedInfo: _this.form.description,
             fixedBy: JSON.parse(localStorage.getItem("userInfo")).username ? JSON.parse(localStorage.getItem("userInfo")).username : ''
           }).then(() => {
             _this.visiable = false;
             _this.$Message.warning("添加成功!");
             _this.getAlertList();
           });
         }
      })
    },
    closeAlertsResult() {
      const _this = this;
      _this.getAlertList();
      _this.currview='index';
    },
     handleSettingChange(columns) {
       if(this.action === 'alert'){
          this.columns.map(item => {
          if (item.title === columns.title) {
            item.isSetting = !columns.isSetting;
          }
         })
       }else{
         this.columnsType.map(item => {
          if (item.title === columns.title) {
            item.isSetting = !columns.isSetting;
          }
         })
       }
      }
  },
  mounted() {
    this.init();
  }
};
</script>
