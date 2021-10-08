<style lang="less" scoped>
@import "../../../styles/table-common.less";
@import "./recordChangeManage.less";
</style>
<template>
  <div>
    <Card>
      <div slot="title">
        <a @click="close" class="back-title">
          <Icon type="ios-arrow-back" style="margin: 0 0 2px 0"/>返回
        </a>
        <div class="detail-search">
        <a-form-item type="slot">
          <slot>
            <Button @click="handleSearch" icon="md-refresh">刷新</Button>
          </slot>
        </a-form-item>
        </div>
      </div>
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
  getTableMonHistoryList
} from "@/api/index";
import util from "@/libs/util.js";
import { formatRateTo4,formatCount } from '@/libs/date';
import AFromItem from '@views/my-components/form/FormItem';
export default {
  name: "history-record-change-manage",
  props: {
      service: String,
      tableName: String
  },
  components: {
    "a-form-item": AFromItem
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
      columns: [
          {
              title: "创建者",
              key: "owner",
              sortable: false
          },
          {
              title: "表名",
              key: "table_name",
              sortable: true,
              width:200,
              render: (h, params) => {
                  return h("span",formatCount(params.row.tableName))
              }
          },
          {
              title: "新增数",
              key: "ins_diff",
              sortable: true,
              render: (h, params) => {
                  return h("span",formatCount(params.row.insDiff))
              }
          },
          {
              title: "新增%",
              key: "",
              sortable: false,
              render: (h, params) => {
                  let per = this.getPercent(params.row.insDiff,params.row.dmlDiff);
                  return h("span",per)
              }
          },
          {
              title: "更新数",
              key: "upd_diff",
              sortable: true,
              render: (h, params) => {
                  return h("span",params.row.updDiff)
              }
          },
          {
              title: "更新%",
              key: "",
              sortable: false,
              render: (h, params) => {
                  let per = this.getPercent(params.row.updDiff,params.row.dmlDiff);
                  return h("span",per)
              }
          },
          {
              title: "删除数",
              key: "del_diff",
              sortable: true,
              render: (h, params) => {
                  return h("span",formatCount(params.row.delDiff))
              }
          },
          {
              title: "删除%",
              key: "",
              sortable: false,
              render: (h, params) => {
                  let per = this.getPercent(params.row.delDiff,params.row.dmlDiff);
                  return h("span",per)
              }
          },
          {
              title: "DML数",
              key: "dml_diff",
              sortable: true,
              render: (h, params) => {
                  return h("span",formatCount(params.row.dmlDiff))
              }
          },
          {
              title: "总行数",
              key: "num_rows",
              sortable: true,
              render: (h, params) => {
                  return h("span",formatCount(params.row.numRows))
              }
          },
          {
              title: "trunc",
              key: "truncated",
              sortable: false
          },
          {
              title: "更新时间",
              key: "createTime",
              sortable: false
          },
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
      this.getTableMonHistoryList();
    },
    changePage(v) {
      this.pageNumber = v;
      this.getTableMonHistoryList();
      this.clearSelectAll();
    },
    changePageSize(v) {
      this.pageSize = v;
      this.getTableMonHistoryList();
    },
    changeSort(e) {
      this.sortColumn = e.key;
      this.sortType = e.order;
      if (e.order == "normal") {
        this.sortType = "";
      }
      this.getTableMonHistoryList();
    },
    getPercent(val,total){
        let availablePercent = Number(val) / Number(total);
        if (Number(val) <= 0) return 0;
        return ((availablePercent) * 100).toPrecision(4)
    },
    getTableMonHistoryList() {
      this.loading = true;
      let params = {
        pageNumber: this.pageNumber,
        pageSize: this.pageSize,
        sort: this.sortColumn,
        order: this.sortType,
        service:this.service,
        tableName:this.tableName
      };

      getTableMonHistoryList(params).then(res => {
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
    },
    //搜索
    handleSearch() {
      const _this = this;
      _this.getTableMonHistoryList();
    }
  },
  mounted() {
    this.init();
  }
};
</script>
