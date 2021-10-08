<style lang="less" scoped>
@import "../../../styles/table-common.less";
@import "./sqlReportManage.less";
.sqltab .ivu-table-cell{padding:5px}
.col-left .ivu-table-cell{
  text-align: left;
  overflow : hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;      /* 可以显示的行数，超出部分用...表示*/
  -webkit-box-orient: vertical;
}
</style>
<template>
  <div>
    <Card>
      <div slot="title">
        <a @click="close" class="back-title">
          <Icon type="ios-arrow-back" style="margin: 0 0 2px 0"/>返回
        </a>
      </div>
      
      <Form ref="searchForm" :model="searchForm" inline :label-width="150" >

        <Form-item label="SQL全文搜索" prop="name">
          <Input
                  type="text"
                  v-model="searchForm.name"
                  clearable
                  placeholder="请输入关键字"
                  style="width: 200px"
          />
        </Form-item>
        <Form-item style="margin-left:-35px;" class="br">
          <Button @click="handleSearch" type="primary" icon="ios-search">搜索</Button>
          <Button @click="handleReset">重置</Button>
        </Form-item>

        <Form-item label="注意：" prop="name">
          <Alert banner type="warning">每天采集1次,execs是sql在1天内的执行次数</Alert>
        </Form-item>

      </Form>
      <Row class="sqltab">
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
  getSqlSummaryList
} from "@/api/index";
import util from "@/libs/util.js";
export default {
  name: "history-sqlsummary-manage",
  props: {
      service: String
  },
  components: {
  },
  data() {
    return {
      openTip: false,
      openLevel: "0",
      loading: true,
      searchKey: "",
      sortColumn: "create_time",
      sortType: "desc",
      modalType: 0,
      modalTitle: "",
      modalVisible:false,
      submitLoading: false,
      selectList: [],
      selectCount: 0,
      searchForm:{name:''},
      columns: [
          {
              title: "sql ID",
              key: "sqlId",
              align:'center',
              width:150,
              sortable: false
          },
          {
              title: "sql语句",
              key: "sql_text",
              align:'center',
              className:'col-left',
              width:300,
              sortable: true,
              render: (h, params) => {
                  return h("span",params.row.sqlText);
              }
          },
          {
              title: "fsc",
              key: "scan_type",
              align:'center',
              sortable: true,
              render: (h, params) => {
                  return h("span",params.row.scanType);
              }
          },
          {
              title: "exe",
              key: "exec_delta",
              align:'center',
              sortable: true,
              render: (h, params) => {
                  return h("span",params.row.execDelta);
              }
          },
          {
              title: "ex%",
              key: "exec_per",
              align:'center',
              sortable: true,
              render: (h, params) => {
                  return h("span",params.row.execPer);
              }
          },
          {
              title: "buf",
              key: "buffer_gets",
              align:'center',
              sortable: true,
              render: (h, params) => {
                  return h("span",params.row.bufferGets);
              }
          },
          {
              title: "bu%",
              key: "buff_per",
              align:'center',
              sortable: true,
              render: (h, params) => {
                  return h("span",params.row.buffPer);
              }
          },
          {
              title: "dsk",
              key: "disk_reads",
              align:'center',
              sortable: true,
              render: (h, params) => {
                  return h("span",params.row.diskReads);
              }
          },
          {
              title: "di%",
              key: "disk_per",
              align:'center',
              sortable: true,
              render: (h, params) => {
                  return h("span",params.row.diskPer);
              }
          },
          {
              title: "ela",
              key: "elapsed_time",
              align:'center',
              sortable: true,
              render: (h, params) => {
                  return h("span",params.row.elapsedTime);
              }
          },
          {
              title: "el%",
              key: "elap_per",
              align:'center',
              sortable: true,
              render: (h, params) => {
                  return h("span",params.row.elapPer);
              }
          },
          {
              title: "cp%",
              key: "cput_per",
              align:'center',
              sortable: true,
              render: (h, params) => {
                  return h("span",params.row.cputPer);
              }
          },
          {
              title: "pc%",
              key: "parse_per",
              align:'center',
              sortable: true,
              render: (h, params) => {
                  return h("span",params.row.parsePer);
              }
          },
          {
              title: "so%",
              key: "sort_per",
              align:'center',
              sortable: true,
              render: (h, params) => {
                  return h("span",params.row.sortPer);
              }
          },
          {
              title: "row",
              key: "rows_processed",
              align:'center',
              sortable: true,
              render: (h, params) => {
                  return h("span",params.row.rowsProcessed);
              }
          },
          {
              title: "app",
              key: "app_name",
              align:'center',
              sortable: true,
              render: (h, params) => {
                  return h("span",params.row.appName);
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
    init() {
      this.getSqlSummaryList();
    },
    changePage(v) {
      this.pageNumber = v;
      this.getSqlSummaryList();
      this.clearSelectAll();
    },
    changePageSize(v) {
      this.pageSize = v;
      this.getSqlSummaryList();
    },
    changeSort(e) {
      this.sortColumn = e.key;
      this.sortType = e.order;
      if (e.order == "normal") {
        this.sortType = "";
      }
      this.getSqlSummaryList();
    },
    handleSearch: util.debounce(function() {
        this.pageNumber = 1;
        this.pageSize = 10;
        this.getSqlSummaryList();
    }, 500),
    handleReset: util.debounce(function() {
        this.$refs.searchForm.resetFields();
        this.pageNumber = 1;
        this.pageSize = 10;

        // 重新加载数据
        this.getSqlSummaryList();
    }, 500),
    getSqlSummaryList() {
      this.loading = true;
      let params = {
        pageNumber: this.pageNumber,
        pageSize: this.pageSize,
        sort: this.sortColumn,
        order: this.sortType,
        service:this.service,
        name:this.searchForm.name.trim()
      };

      getSqlSummaryList(params).then(res => {
        this.loading = false;
        if (res.success) {
          this.data = res.result.records;
          this.total = res.result.total;
        }
      });
    },
    clearSelectAll() {
      this.$refs.table.selectAll(false);
    },
    changeSelect(e) {
      this.selectList = e;
      this.selectCount = e.length;
    },
    close() {
        this.$emit("close", true);
    }
  },
  mounted() {
    this.init();
  }
};
</script>