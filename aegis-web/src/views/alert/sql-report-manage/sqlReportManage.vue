<style lang="less" scoped>
@import "../../../styles/table-common.less";
@import "./sqlReportManage.less";
</style>
<template>
  <div class="search">
    <historyManage v-if="currView=='his'" :service="service" @close="currView='index'"></historyManage>
    <sqlManage v-if="currView=='sql'" :service="service" @close="currView='index'"></sqlManage>
    <Card v-show="currView=='index'">
      <div class="form">
        <a-form-item v-model="searchForm.name" label="实例名" placeholder="请输入实例名"></a-form-item>
        <a-form-item type="slot">
          <slot>
            <Button
              @click="handleSearch"
              type="primary"
              icon="ios-search"
              class="margin-right-10"
            >搜索</Button>
            <Button @click="handleReset">重置</Button>
          </slot>
        </a-form-item>
      </div>

      <Row class="operation" type="flex" justify="space-between">
        <Button @click="init" icon="md-refresh">刷新</Button>
        <setting-table-column :columns="columns" @setting-change="handleSettingChange"></setting-table-column>
      </Row>
      <Row>
        <Table
          :loading="loading"
          border
          :columns="columnsSettings"
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
import { getSqlSummaryReportList } from "@/api/index";
import historyManage from "./historyManage.vue";
import sqlManage from "./sqlSummaryManage.vue";
import AFromItem from "@views/my-components/form/FormItem";
import SettingTableColumn from "@views/my-components/settingTableColumn/SettingTableColumn";
import util from "@/libs/util.js";
export default {
  name: "alert-sql-report-manage",
  components: {
    historyManage,
    sqlManage,
    "a-form-item": AFromItem,
    "setting-table-column": SettingTableColumn
  },
  computed: {
    columnsSettings() {
      return this.columns ? this.columns.filter(item => item.isSetting) : [];
    }
  },
  data() {
    return {
      currView: "index",
      openTip: false,
      openLevel: "0",
      loading: true,
      searchKey: "",
      sortColumn: "createTime",
      sortType: "desc",
      modalType: 0,
      modalTitle: "",
      modalVisible: false,
      searchForm: { name: "" },
      form: {},
      formValidate: {},
      service: "",
      submitLoading: false,
      selectList: [],
      selectCount: 0,
      columns: [
        {
          title: "实例名",
          key: "service",
          sortable: false,
          width: 120,
          tooltip: true,
          ellipsis: true,
          isSetting: true
        },
        {
          title: "total",
          key: "total",
          sortable: false,
          width: 120,
          tooltip: true,
          ellipsis: true,
          isSetting: document.documentElement.clientWidth >=1300 ? true : false
        },
        {
          title: "fsc",
          key: "fscan",
          sortable: false,
          width: 120,
          tooltip: true,
          ellipsis: true,
          isSetting: document.documentElement.clientWidth >=1300 ? true : false
        },
        {
          title: "exec",
          key: "execs",
          sortable: false,
          width: 120,
          tooltip: true,
          ellipsis: true,
          isSetting: document.documentElement.clientWidth >=1300 ? true : false
        },
        {
          title: "buff",
          key: "lgrds",
          sortable: false,
          width: 120,
          tooltip: true,
          ellipsis: true,
          isSetting: document.documentElement.clientWidth >=1300 ? true : false
        },
        {
          title: "disk",
          key: "phrds",
          sortable: false,
          width: 120,
          tooltip: true,
          ellipsis: true,
          isSetting: true
        },
        {
          title: "rows",
          key: "rows",
          sortable: false,
          width: 120,
          tooltip: true,
          ellipsis: true,
          isSetting: true
        },
        {
          title: "elap",
          key: "elapsed",
          sortable: false,
          width: 120,
          tooltip: true,
          ellipsis: true,
          isSetting: true
        },
        {
          title: "sort",
          key: "sorts",
          sortable: false,
          width: 120,
          tooltip: true,
          ellipsis: true,
          isSetting: true
        },
        {
          title: "更新日期",
          key: "gmtCreate",
          sortable: false,
          width: 150,
          tooltip: true,
          ellipsis: true,
          isSetting: true,
          render: (h, param) => {
            return h(
              "a",
              {
                on: {
                  click: () => {
                    this.$router.push({ path: `/oracle/sql-text-index-manage/${param.row.service}`});
                  }
                }
              },
              param.row.gmtCreate
            );
          }
        },
        {
          title: "操作",
          key: "action",
          align: "center",
          width: 150,
          isSetting: true,
          render: (h, params) => {
            return h("div", [
              h(
                "Button",
                {
                  props: {
                    type: "primary",
                    size: "small",
                    icon: "ios-eye-outline"
                  },
                  on: {
                    click: () => {
                      this.tablespace = params.row.name;
                      this.service = params.row.service;
                      this.currView = "his";
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
      groupList: []
    };
  },
  methods: {
    //请求sql区段报表
    init() {
      this.getSqlSummaryReportList();
    },
    //改变当前页
    changePage(v) {
      this.pageNumber = v;
      this.getSqlSummaryReportList();
      this.clearSelectAll();
    },
    //改变当前页码
    changePageSize(v) {
      this.pageSize = v;
      this.getSqlSummaryReportList();
    },
    changeSort(e) {
      this.sortColumn = e.key;
      this.sortType = e.order;
      if (e.order == "normal") {
        this.sortType = "";
      }
      this.getSqlSummaryReportList();
    },
    handleSearch() {
      this.pageNumber = 1;
      this.pageSize = 10;
      this.getSqlSummaryReportList();
    },
    handleReset() {
      this.searchForm.name = "";
      this.pageNumber = 1;
      this.pageSize = 10;

      // 重新加载数据
      this.getSqlSummaryReportList();
    },
    getSqlSummaryReportList() {
      this.loading = true;
      let params = {
        pageNumber: this.pageNumber,
        pageSize: this.pageSize,
        sort: this.sortColumn,
        order: this.sort,
        name: this.searchForm.name.trim()
      };

      getSqlSummaryReportList(params).then(res => {
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
    handleSettingChange(columns) {
      this.columns.map(item => {
        if (item.title === columns.title) {
          item.isSetting = !columns.isSetting;
        }
      });
    }
  },
  mounted() {
    this.init();
  }
};
</script>
