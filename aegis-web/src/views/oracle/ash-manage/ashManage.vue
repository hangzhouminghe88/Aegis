<style lang="less" scoped>
  @import "../../../styles/table-common.less";
  @import "./ashManage.less";
</style>
<template>
  <div class="search">
    <historyManage v-if="currView=='his'" :param="hisParam" @close="currView='index'"></historyManage>
    <Card v-show="currView=='index'">
      <div class="form">
        <a-form-item v-model="searchForm.name" label="事件名称" placeholder="请输入事件名称"></a-form-item>
        <a-form-item type="slot">
          <slot>
            <Button @click="handleSearch" type="primary" icon="ios-search" class="margin-right-10">搜索</Button>
            <Button @click="handleReset">重置</Button>
          </slot>
        </a-form-item>
      </div>

      <Row class="operation">
        <div class="oss-operation">
          <div>
            <Button @click="refresh" icon="md-refresh">刷新</Button>
            <Button type="primary" icon="md-arrow-round-down" @click.stop="handleExports">导出top event数据</Button>
          </div>
          <div>
            <!-- <RadioGroup v-model="tabService" @on-change="changeService" type="button" style="margin-right:25px">
              <Radio v-for="item in hostList" :label="item.service" :key="item.value">{{item.service}}</Radio>
            </RadioGroup> -->
            <Select v-model="selectService" @on-change="changeService">
              <Option v-for="item in serviceList" :value="item.name" :key="item.name">{{item.name}}</Option>
            </Select>
          </div>
        </div>
      </Row>
      <Row>
        <Table
            :loading="loading"
            border
            :columns="columns"
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
  </div>
</template>

<script>
  import {
    getSysAshWaitList,
    getAllHostList,
    getAllAshList
  } from "@/api/index";
  import historyManage from "./historyManage.vue";
  import AFromItem from '@views/my-components/form/FormItem'
  import util from "@/libs/util.js";
  import {exportData} from '@/libs/date';
  import { mapGetters, mapState, mapActions} from 'vuex'

  export default {
    name: "ash-manage",
    components: {
      historyManage,
      "a-form-item": AFromItem
    },
    computed: {
      selectService: {
      get() {
        return this.$store.state.service.selectService;
      },
      set(value) {
        this.$store.state.service.selectService = value;
      },
    },
    ...mapGetters({
      serviceList: "getServiceList",
    }),
    },
    data() {
      return {
        currView: "index",
        openTip: false,
        openLevel: "0",
        loading: true,
        searchKey: "",
        sortColumn: "total_waits",
        sortType: "desc",
        modalType: 0,
        modalTitle: "",
        modalVisible: false,
        hostList: [],
        searchForm: {name: ''},
        form: {},
        formValidate: {},
        tabService: '',
        service: '',
        submitLoading: false,
        selectList: [],
        selectCount: 0,
        hisParam: {},
        columns: [
          {
            title: "所有者",
            key: "owner",
            sortable: false
          },
          {
            title: "对象名称",
            key: "objectName",
            sortable: false,
            render: (h, param) => {
              return h("a", {
                on: {
                  click: () => {
                    this.hisParam = param.row;
                    this.currView = 'his'
                  }
                }
              }, param.row.objectName)
            }
          },
          {
            title: "对象类型",
            key: "objectType",
            sortable: false
          },
          {
            title: "等待分类",
            key: "waitClass",
            sortable: false
          },
          {
            title: "事件名称",
            key: "event",
            sortable: false
          },
          {
            title: "总等待次数",
            key: "total_waits",
            sortable: false,
            render: (h, params) => {
              return h("span", params.row.totalWaits)
            }
          },
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
      //初始化请求
      init() {
        if(this.selectService) {
          this.getAllService();//获得所有实例
        }
      },
      //翻页
      changePage(v) {
        this.pageNumber = v;
        this.getSysAshWaitList();
        this.clearSelectAll();
      },
      //改变页码
      changePageSize(v) {
        this.pageSize = v;
        this.getSysAshWaitList();
      },
      //排序
      changeSort(e) {
        this.sortColumn = e.key;
        this.sortType = e.order;
        if (e.order == "normal") {
          this.sortType = "";
        }
        this.getSysAshWaitList();
      },
      //搜索防抖形式
      handleSearch: util.debounce(function() {
        this.pageNumber = 1;
        this.pageSize = 10;
        this.getSysAshWaitList();
      }, 500),
      //重置防抖形式
      handleReset: util.debounce(function() {
        this.searchForm.name = "";
        this.pageNumber = 1;
        this.pageSize = 10;
        // 重新加载数据
        this.getSysAshWaitList();
      }, 500),
      //获得所有oracle实例
      getAllService() {
        this.hostList = this.serviceList
        this.tabService = this.selectService;
        this.getSysAshWaitList();
      },
      //切换实例
      changeService(e) {
        this.pageNumber = 1;
        this.$store.commit('SETSELECTSERVICE', e);
        this.getSysAshWaitList();
      },
      //查询ashwait
      getSysAshWaitList() {
        this.loading = true;
        let params = {
          pageNumber: this.pageNumber,
          pageSize: this.pageSize,
          sort: this.sortColumn,
          order: this.sortType,
          name: this.searchForm.name.trim(),
          service: this.selectService
        };

        getSysAshWaitList(params).then(res => {
          this.loading = false;
          if (res.success) {
            this.data = res.result.records;
            this.total = res.result.total;
          }
        });
      },
      //刷新
      refresh: util.debounce(function(){
        this.getSysAshWaitList()
      }, 500),
      //隐藏modalVisible
      cancel() {
        this.modalVisible = false;
      },
      //清除全选
      clearSelectAll() {
        this.$refs.table.selectAll(false);
      },
      changeSelect(e) {
        this.selectList = e;
        this.selectCount = e.length;
      },
      //导出top event数据
      handleExports() {
        const params = {
          sortColumn: this.sortColumn,
          sortType: this.sortType,
          name: this.searchForm.name,
          service: this.selectService
        };
        getAllAshList(params).then((res) => {
          if (res.success) {
            let str = '所有者,对象名称,对象类型,等待分类,时间名称,总等待次数\n';
            res.result.forEach(item => {
              str += `${item.owner ? item.owner : ''},${item.objectName ? item.objectName : ''},${item.objectType ? item.objectType : ''},${item.waitClass ? item.waitClass : ''},${item.event ? item.event : ''},${item.totalWaits ? item.totalWaits : ''}\n`;
            })
            exportData('text/xls', str, this.selectService + '活动会话历史' + new Date().getTime() + '.csv');
          }
        })

      }
    },
    mounted() {
      this.init();
    },
    watch: {
    '$store.state.service.serviceList': function(newVal, oldVal) {
      if(newVal !== oldVal) {
         this.init();
      }
    }
   }
  };
</script>
