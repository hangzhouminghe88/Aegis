<template>
  <Card>
    <a @click="$emit('close')" class="back-title">
      <Icon type="ios-arrow-back" style="margin: 0 0 2px 0" />返回
    </a>
    <Tabs :active-key="currTab" @on-click="handleTabChange" :animated="true">
      <Tab-pane label="SQL执行计划" key="sqlPlan"></Tab-pane>
      <Tab-pane label="SQL明细数据" key="sqlDetail"></Tab-pane>
    </Tabs>
    <div v-show="currTab === 'sqlPlan'">
      <div class="form">
        <a-form-item v-model="searchForm.name" label="SQL全文搜索" placeholder="请输入关键字"></a-form-item>
        <a-form-item type="slot">
          <slot>
            <Button
                @click="handleSearch"
                type="primary"
                icon="ios-search"
                class="margin-right-10"
            >搜索</Button>
            <Button @click="handleReset" class="margin-right-10">重置</Button>
            <Button @click="copySQL" icon="md-copy" type="primary">拷贝SQL</Button>
          </slot>
        </a-form-item>
      </div>
      <Row type="flex" class="page">
        <div class="sqlplan_title">sql_id:{{param.sqlId}}</div>
        <div class="sqlplan_content">
          <div
              v-for="(item, index) in sqlTextData"
              :key="index"
              class="sqlplan_row"
          >{{item.sqlText}}</div>
        </div>
      </Row>
      <Row>
        <Table :columns="columns" :data="data" border ref="table"></Table>
      </Row>
    </div>
    <div v-show="currTab === 'sqlDetail'">
      <div class="form">
        <a-form-item v-model="searchForm.name" label="sql_id搜索" placeholder="请输入关键字"></a-form-item>
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
      <Row>
        <Table :columns="detailColumns" :data="detailData" border ref="table" @on-row-click="handleExpand"></Table>
      </Row>
      <Row type="flex" justify="end" class="page">
        <Page
            :current="pageNumber"
            :total="detailTotal"
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
    </div>
  </Card>
</template>

<script>
import {
  getSqlPlanList,
  getSqlSummaryDetailList,
  getSqlTextList
} from "@api/index";
import { formatDate, copyText } from "@/libs/date.js";
import AFromItem from "@views/my-components/form/FormItem";
import expandRow from "@views/oracle/sql-text-index-manage/ExpandRow";
export default {
  name: "SqlPlan",
  components: {
    "a-form-item": AFromItem,
    expandRow
  },
  //属性
  props: {
    param: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  data() {
    let _this = this;
    return {
      searchForm: {
        name: ""
      },
      data: [],
      loading: false,
      total: 0,
      currTab: "sqlPlan",
      detailData: [],
      detailTotal: 0,
      pageNumber: 1,
      pageSize: 10,
      sqlTextData: [],
      columns: [
        {
          title: "plan_hash",
          key: "planHashValue",
          sortable: true
        },
        {
          title: "id",
          key: "planId",
          sortable: true
        },
        {
          title: "options",
          key: "operation",
          sortable: true
        },
        {
          title: "object_name",
          key: "objectName",
          sortable: true
        },
        {
          title: "rows",
          key: "cardinality",
          sortable: true
        },
        {
          title: "bytes",
          key: "bytes",
          sortable: true
        },
        {
          title: "cost",
          key: "cost",
          sortable: true
        },
        {
          title: "temp",
          key: "plan_hash",
          sortable: true
        }
      ],
      detailColumns: [
        {
          type: 'expand',
          width: 100,
          render: (h, param) => {
            return h(expandRow, {
              props: {
                row: param.row,
                type:'detail'
              }
            })
          }
        },
        {
          title: "更新时间",
          key: "sqlId",
          align: "center",
          width: 150,
          sortable: false,
          render: (h, params) => {
            return h(
              "a",
              {
                on: {
                  click: () => _this.$emit("close")
                }
              },
              params.row.gmtCreate
            );
          }
        },
        {
          title: "所有者",
          key: "sql_text",
          align: "center",
          className: "col-left",
          width: 150,
          sortable: true,
          render: (h, params) => {
            return h("span", params.row.schemaName);
          }
        },
        {
          title: "fsc",
          key: "scan_type",
          align: "center",
          sortable: true,
          width: 150,
          render: (h, params) => {
            return h("span", params.row.scanType);
          }
        },
        {
          title: "exe",
          key: "execDelta",
          align: "center",
          sortable: true,
          width: 150,
          render: (h, params) => {
            return h("span", params.row.executions);
          }
        },
        {
          title: "ex%",
          key: "execPer",
          align: "center",
          sortable: true,
          width: 150,
          render: (h, params) => {
            return h("span", params.row.execPer);
          }
        },
        {
          title: "buf",
          key: "bufferGets",
          align: "center",
          sortable: true,
          width: 150,
          render: (h, params) => {
            return h("span", params.row.bufferGets);
          }
        },
        {
          title: "bu%",
          key: "buffPer",
          align: "center",
          sortable: true,
          width: 150,
          render: (h, params) => {
            return h("span", params.row.buffPer);
          }
        },
        {
          title: "dsk",
          key: "diskReads",
          align: "center",
          sortable: true,
          width: 150,
          render: (h, params) => {
            return h("span", params.row.diskReads);
          }
        },
        {
          title: "di%",
          key: "diskPer",
          align: "center",
          sortable: true,
          width: 150,
          render: (h, params) => {
            return h("span", params.row.diskPer);
          }
        },
        {
          title: "ela",
          key: "elapsedTime",
          align: "center",
          sortable: true,
          width: 150,
          render: (h, params) => {
            return h("span", params.row.elapsedTime);
          }
        }
      ]
    };
  },
  mounted() {
    this.getSqlTextList();
    this.getSqlPlanList();
  },
  methods: {
    //搜索
    handleSearch() {
      this.getSqlPlanList();
    },
    //重置
    handleReset() {
      this.searchForm.name = "";
      this.getSqlPlanList();
    },
    getSqlPlanList() {
      let params = {
        sort: this.sortColumn,
        order: this.sortType,
        name: this.searchForm.name.trim(),
        sqlId: this.param.sqlId,
        service: this.param.service
      };
      getSqlPlanList(params).then(res => {
        this.loading = false;
        if (res.success) {
          this.data = res.result.records;
          this.total = res.result.total;
        }
      });
    },
    //获得sqlText
    getSqlTextList() {
      let params = {
        service: this.param.service
      };
      getSqlTextList(this.param.sqlId, params).then(res => {
        this.loading = false;
        if (res.success) {
          this.sqlTextData = res.result;
        }
      });
    },
    //切换tab回调
    handleTabChange(tab) {
      this.currTab = tab === 0 ? "sqlPlan" : "sqlDetail";
      switch (this.currTab) {
        case "sqlPlan":
          this.getSqlPlanList();
          break;
        case "sqlDetail":
          this.getSqlSummaryDetailList();
      }
    },
    getSqlSummaryDetailList() {
      this.loading = true;
      let params = {
        pageNumber: this.pageNumber,
        pageSize: this.pageSize,
        sort: this.sortColumn,
        order: this.sortType,
        service: this.param.service,
        sqlId: this.param.sqlId,
        gmtCreate: formatDate(
          new Date(new Date().getTime() - 30 * 24 * 60 * 60 * 1000),
          "yyyy-MM-dd"
        )
      };

      getSqlSummaryDetailList(params).then(res => {
        this.loading = false;
        if (res.success) {
          this.detailData = res.result.records;
          this.detailTotal = res.result.total;
        }
      });
    },
    //分页
    changePage(v) {
      this.pageNumber = v;
      this.getSqlSummaryDetailList();
    },
    changePageSize(v) {
      this.pageSize = v;
      this.pageNumber = 1;
      this.getSqlSummaryDetailList();
    },
    copySQL() {
      const _this = this;
      let dom = document.querySelector(".sqlplan_content");
      copyText(dom.innerText);
    },
     //处理展开
    handleExpand(row, index) {
      this.detailData.forEach((item, i) => {
        //这个循环是为了每次只能展开一个，其他自动收起，不需要可以去掉
        i !== index ? (this.detailData[i]._expanded = false) : "";
      });
      this.detailData[index]._expanded = !this.detailData[index]._expanded;
      this.detailData.sort();
    }
  }
};
</script>
<style lang="less" scoped>
@import "../../../styles/table-common.less";
@import "~@views/oracle/sql-text-index-manage/sqlTextIndexManage.less";
</style>
