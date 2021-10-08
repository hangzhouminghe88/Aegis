<style lang="less" scoped>
@import "../../../styles/table-common.less";
@import "./sqlBindManage.less";
</style>
<template>
  <div class="search">
    <historyManage v-if="currView=='his'" :service="service" @close="currView='index'"></historyManage>
    <Card v-show="currView=='index'">
      <div class="form">
        <a-form-item v-model="searchForm.name" label="sql_text" placeholder="请输入sql_text"></a-form-item>
        <a-form-item type="slot">
          <slot>
            <Button @click="handleSearch" type="primary" icon="ios-search" class="margin-right-10">搜索</Button>
            <Button @click="handleReset">重置</Button>
          </slot>
        </a-form-item>
      </div>

      <Row class="operation">
        <Button @click="refresh" icon="md-refresh">刷新</Button>
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
  getSqlBindList
} from "@/api/index";
import historyManage from "./historyManage.vue";
import AFromItem from '@views/my-components/form/FormItem';
import util from "@/libs/util.js";
export default {
  name: "alert-sql-bind-manage",
  components: {
      historyManage,
      "a-form-item": AFromItem
  },
  data() {
    return {
      currView:"index",
      openTip: false,
      openLevel: "0",
      loading: true,
      searchKey: "",
      sortColumn: "gmt_create",
      sortType: "desc",
      modalType: 0,
      modalTitle: "",
      modalVisible:false,
      searchForm:{name:''},
      form: {

      },
      formValidate: {
      },
      service:'',
      submitLoading: false,
      selectList: [],
      selectCount: 0,
      columns: [
        {
          title: "实例名",
          key: "service",
          sortable: false
        },
          {
              title: "SQL",
              key: "sqlText",
              sortable: false
          },
          {
              title: "sql_cnt",
              key: "sqlCnt",
              sortable: false
          },
          {
              title: "sql_delta",
              key: "sqlDelta",
              sortable: false
          },
          {
              title: "更新日期",
              key: "gmtCreate",
              sortable: false
          },
        {
          title: "操作",
          key: "action",
          align: "center",
          width: 250,
          render: (h, params) => {
            return h("div", [
              h(
                "Button",
                {
                  props: {
                    type: "primary",
                    size: "small",
                    icon: 'ios-eye-outline'
                  },
                  style:{
                      marginRight:'15px'
                  },
                  on: {
                    click: () => {
                      this.service = params.row.service;
                      this.currView='his'
                    }
                  }
                },
                "查看历史"
              )
            ]);
          }
        }
      ],
      data: [],
      pageNumber: 1,
      pageSize: 10,
      total: 0,
      selectAllFlag: false,
      dataType: 0,
      groupList:[]
    };
  },
  methods: {
    //初始化请求sql绑定列表
    init() {
      this.getSqlBindList();
    },
    //改变当前页
    changePage(v) {
      this.pageNumber = v;
      this.getSqlBindList();
      this.clearSelectAll();
    },
    //改变当前页码
    changePageSize(v) {
      this.pageSize = v;
      this.getSqlBindList();
    },
    changeSort(e) {
      this.sortColumn = e.key;
      this.sortType = e.order;
      if (e.order == "normal") {
        this.sortType = "";
      }
      this.getSqlBindList();
    },
    //处理搜索
    handleSearch: util.debounce(function() {
        this.pageNumber = 1;
        this.pageSize = 10;
        this.getSqlBindList();
    }, 500),
    //处理重置
    handleReset: util.debounce(function() {
        this.searchForm.name="";
        this.pageNumber = 1;
        this.pageSize = 10;

        // 重新加载数据
        this.getSqlBindList();
    }, 500),
    //处理刷新
    refresh: util.debounce(function() {
      this.init();
    }, 500),
    getSqlBindList() {
      this.loading = true;
      let params = {
        pageNumber: this.pageNumber,
        pageSize: this.pageSize,
        sort: this.sortColumn,
        order: this.sort,
        name:this.searchForm.name.trim()
      };

      getSqlBindList(params).then(res => {
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
    clearSelectAll() {
      this.$refs.table.selectAll(false);
    },
    changeSelect(e) {
      this.selectList = e;
      this.selectCount = e.length;
    },
  },
  mounted() {
    this.init();
  }
};
</script>
