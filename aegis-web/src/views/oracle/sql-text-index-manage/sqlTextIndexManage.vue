<style lang="less" scoped>
@import "../../../styles/table-common.less";
@import "./sqlTextIndexManage.less";
.sqltab .ivu-table-cell {
  padding: 5px;
}
.col-left .ivu-table-cell {
  text-align: left;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2; /* 可以显示的行数，超出部分用...表示*/
  -webkit-box-orient: vertical;
}
</style>
<template>
  <div class="search">
    <sql-plan :param="sqlPlanParam" v-if="currView == 'sqlPlan'" @close="currView = 'index'"></sql-plan>
    <Card v-if="currView ===  'index'">
      <div class="form">
        <a-form-item v-model="searchForm.name" label="SQL全文搜索" palceholder="SQL全文搜索"></a-form-item>
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
        <a-form-item label="注意" type="slot" v-show="['report', 'summary'].includes(mode)">
          <Alert banner type="warning" style="display: inline-block;" v-text="tips"></Alert>
        </a-form-item>
      </div>
      <Row class="operation">
        <div class="oss-operation">
          <div>
            <Button @click="refresh" icon="md-refresh">刷新</Button>
            <RadioGroup
              v-model="mode"
              @on-change="changeMode"
              type="button"
              style="margin-left: 10px;"
            >
              <Radio v-for="item in modeList" :key="item.value" :label="item.label">{{item.value}}</Radio>
            </RadioGroup>
          </div>
          <div>
            <Select
              v-model="selectService"
              @on-change="changeService"
              style="width: 8rem;"
              class="margin-right-10"
            >
              <Option
                v-for="item in serviceList"
                :value="item.name"
                :key="item.name"
              >{{item.name}}</Option>
            </Select>
            <setting-table-column
              :columns="columns"
              @setting-change="handleSettingChange"
              v-if="mode === 'report'"
            ></setting-table-column>
          </div>
        </div>
      </Row>

      <Row class="sqltab">
        <Table
          v-if="mode === 'report'"
          :loading="loading"
          border
          :columns="columnsIsSetting"
          :data="data"
          :resizable="true"
          ref="table"
          sortable="custom"
          @on-sort-change="changeSort"
          @on-selection-change="changeSelect"
          @on-row-click="handleExpand"
        ></Table>
        <Table
          v-if="mode === 'summary'"
          :loading="loading"
          border
          :columns="summaryColumns"
          :data="data"
          :resizable="true"
          ref="table"
          sortable="custom"
          @on-sort-change="changeSort"
          @on-selection-change="changeSelect"
        ></Table>
        <Table
          v-if="mode === 'sqlbind'"
          :loading="loading"
          border
          :columns="sqlBindColumns"
          :data="data"
          :resizable="true"
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
  getSqlSummaryList,
  getAllHostList,
  getSqlSummaryListActionBySummary,
  getSqlSummaryListActionBySqlBind,
} from "@/api/index";
import util from "@/libs/util.js";
import { formatDate, formatSize } from "@/libs/date.js";
import SqlPlan from "@views/oracle/sql-text-index-manage/SqlPlan";
import AFromItem from "@views/my-components/form/FormItem";
import SettingTableColumn from "@views/my-components/settingTableColumn/SettingTableColumn";
import expandRow from "@views/oracle/sql-text-index-manage/ExpandRow";
import { mapGetters } from "vuex";
export default {
  name: "oracle-sql-text-manage",
  props: {
    service: String,
  },
  components: {
    "sql-plan": SqlPlan,
    "a-form-item": AFromItem,
    "setting-table-column": SettingTableColumn,
    expandRow,
  },
  computed: {
    tips() {
      const _this = this;
      if (_this.mode && _this.mode === "report")
        return "每天采集1次,execs是sql在1天内的执行次数";
      else if (_this.mode && _this.mode === "summary")
        return "说明: execs/lgrds/phrds(=0/<=1k/<=10k/>10k); cpu/elapsed/sorts(=0/<=1/<=10/>10)";
      else return "";
    },
    columnsIsSetting() {
      return this.columns ? this.columns.filter((item) => item.isSetting) : [];
    },
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
    let _this = this;
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
      submitLoading: false,
      selectList: [],
      selectCount: 0,
      searchForm: { name: "" },
      sqlPlanParam: {},
      showSqlPlan: false,
      mode: "report",
      modeList: [
        {
          label: "report",
          value: "SQL全文索引",
        },
        {
          label: "summary",
          value: "SQL区段分析报表",
        },
        {
          label: "sqlbind",
          value: "SQL未绑定变量报表",
        },
      ],
      columns: [
        {
          type: "expand",
          width: 100,
          isSetting: true,
          title: "展开",
          render: (h, params) => {
            return h(expandRow, {
              props: {
                row: params.row,
                type: "sql_text",
              },
            });
          },
        },
        {
          title: "sql ID",
          key: "sqId",
          align: "center",
          sortable: false,
          isSetting: true,
          render: (h, params) => {
            return h(
              "tooltip",
              {
                props: {
                  placement: "right",
                },
              },
              [
                h(
                  "a",
                  {
                    on: {
                      click: () => _this.toLinkSqlPlan(params.row),
                    },
                  },
                  params.row.sqlId
                ),
                h(
                  "span",
                  {
                    slot: "content",
                    style: {
                      whiteSpace: "normal",
                      wordBreak: "break-all",
                    },
                  },
                  "可查看sql执行计划、明细数据"
                ),
              ]
            );
          },
          renderHeader: (h, params) => {
            return params.column._width >= 80
              ? h("span", {}, "sql ID")
              : h(
                  "span",
                  {
                    domProps: {
                      title: "sql ID",
                    },
                  },
                  "sql ID"
                );
          },
        },
        {
          title: "sql语句",
          key: "sqlText",
          align: "left",
          sortable: true,
          ellipsis: true,
          tooltip: true,
          isSetting: true,
          renderHeader: (h, params) => {
            return params.column._width >= 80
              ? h("span", {}, "sql语句")
              : h(
                  "span",
                  {
                    domProps: {
                      title: "sql语句",
                    },
                  },
                  "sql语句"
                );
          },
        },
        {
          key: "scanType",
          align: "center",
          title: "fsc",
          sortable: true,
          isSetting: true,
          render: (h, params) => {
            return h(
              "tooltip",
              {
                props: {
                  placement: "right",
                },
              },
              [
                h("span", params.row.scanType),
                h(
                  "span",
                  {
                    slot: "content",
                    style: {
                      whiteSpace: "normal",
                      wordBreak: "break-all",
                    },
                  },
                  "代表该SQL是全表或全索引扫描"
                ),
              ]
            );
          },
          renderHeader: (h, params) => {
            return params.column._width >= 80
              ? h("span", {}, "fsc")
              : h(
                  "span",
                  {
                    domProps: {
                      title: "fsc",
                    },
                  },
                  "fsc"
                );
          },
        },
        {
          title: "exe",
          key: "execDelta",
          align: "center",
          sortable: true,
          isSetting: true,
          render: (h, params) => {
            return h(
              "tooltip",
              {
                props: {
                  placement: "right",
                },
              },
              [
                h("span", formatSize(params.row.execDelta)),
                h(
                  "span",
                  {
                    slot: "content",
                    style: {
                      whiteSpace: "normal",
                      wordBreak: "break-all",
                    },
                  },
                  "代表该SQL最近一天内总执行次数"
                ),
              ]
            );
          },
          renderHeader: (h, params) => {
            return params.column._width >= 80
              ? h("span", {}, "exe")
              : h(
                  "span",
                  {
                    domProps: {
                      title: "exe",
                    },
                  },
                  "exe"
                );
          },
        },
        {
          title: "ex%",
          key: "execPer",
          align: "center",
          sortable: true,
          isSetting: true,
          render: (h, params) => {
            return h(
              "tooltip",
              {
                props: {
                  placement: "right",
                },
              },
              [
                h("span", params.row.execPer),
                h(
                  "span",
                  {
                    slot: "content",
                    style: {
                      whiteSpace: "normal",
                      wordBreak: "break-all",
                    },
                  },
                  "代表该SQL占总执行次数的百分比"
                ),
              ]
            );
          },
          renderHeader: (h, params) => {
            return params.column._width >= 80
              ? h("span", {}, "ex%")
              : h(
                  "span",
                  {
                    domProps: {
                      title: "ex%",
                    },
                  },
                  "ex%"
                );
          },
        },
        {
          title: "buf",
          key: "bufferGets",
          align: "center",
          sortable: true,
          isSetting: false,
          render: (h, params) => {
            return h(
              "tooltip",
              {
                props: {
                  placement: "right",
                },
              },
              [
                h("span", formatSize(params.row.bufferGets)),
                h(
                  "span",
                  {
                    slot: "content",
                    style: {
                      whiteSpace: "normal",
                      wordBreak: "break-all",
                    },
                  },
                  "代表该SQL单次执行的逻辑读"
                ),
              ]
            );
          },
          renderHeader: (h, params) => {
            return params.column._width >= 80
              ? h("span", {}, "buf")
              : h(
                  "span",
                  {
                    domProps: {
                      title: "buf",
                    },
                  },
                  "buf"
                );
          },
        },
        {
          title: "bu%",
          key: "buffPer",
          align: "center",
          sortable: true,
          isSetting: false,
          render: (h, params) => {
            return h(
              "tooltip",
              {
                props: {
                  placement: "right",
                },
              },
              [
                h("span", params.row.buffPer),
                h(
                  "span",
                  {
                    slot: "content",
                    style: {
                      whiteSpace: "normal",
                      wordBreak: "break-all",
                    },
                  },
                  "代表该SQL单次执行占综逻辑读的百分比"
                ),
              ]
            );
          },
          renderHeader: (h, params) => {
            return params.column._width >= 80
              ? h("span", {}, "bu%")
              : h(
                  "span",
                  {
                    domProps: {
                      title: "bu%",
                    },
                  },
                  "bu%"
                );
          },
        },
        {
          title: "dsk",
          key: "diskReads",
          align: "center",
          sortable: true,
          isSetting: false,
          render: (h, params) => {
            return h(
              "tooltip",
              {
                props: {
                  placement: "right",
                },
              },
              [
                h("span", formatSize(params.row.diskReads)),
                h(
                  "span",
                  {
                    slot: "content",
                    style: {
                      whiteSpace: "normal",
                      wordBreak: "break-all",
                    },
                  },
                  "代表该SQL单次执行的磁盘读"
                ),
              ]
            );
          },
          renderHeader: (h, params) => {
            return params.column._width >= 80
              ? h("span", {}, "dsk")
              : h(
                  "span",
                  {
                    domProps: {
                      title: "dsk",
                    },
                  },
                  "dsk"
                );
          },
        },
        {
          title: "di%",
          key: "diskPer",
          align: "center",
          sortable: true,
          isSetting: false,
          render: (h, params) => {
            return h(
              "tooltip",
              {
                props: {
                  placement: "right",
                },
              },
              [
                h("span", params.row.diskPer),
                h(
                  "span",
                  {
                    slot: "content",
                    style: {
                      whiteSpace: "normal",
                      wordBreak: "break-all",
                    },
                  },
                  "代表该SQL单次执行的占总磁盘读的百分比"
                ),
              ]
            );
          },
          renderHeader: (h, params) => {
            return params.column._width >= 80
              ? h("span", {}, "di%")
              : h(
                  "span",
                  {
                    domProps: {
                      title: "di%",
                    },
                  },
                  "di%"
                );
          },
        },
        {
          title: "ela",
          key: "elapsedTime",
          align: "center",
          sortable: true,
          isSetting: false,
          render: (h, params) => {
            return h(
              "tooltip",
              {
                props: {
                  placement: "right",
                },
              },
              [
                h("span", params.row.elapsedTime),
                h(
                  "span",
                  {
                    slot: "content",
                    style: {
                      whiteSpace: "normal",
                      wordBreak: "break-all",
                    },
                  },
                  "代表该SQL单次执行所消耗的时间"
                ),
              ]
            );
          },
          renderHeader: (h, params) => {
            return params.column._width >= 80
              ? h("span", {}, "ela")
              : h(
                  "span",
                  {
                    domProps: {
                      title: "ela",
                    },
                  },
                  "ela"
                );
          },
        },
        {
          title: "el%",
          key: "elapPer",
          align: "center",
          sortable: true,
          isSetting: false,
          render: (h, params) => {
            return h(
              "tooltip",
              {
                props: {
                  placement: "right",
                },
              },
              [
                h("span", params.row.elapPer),
                h(
                  "span",
                  {
                    slot: "content",
                    style: {
                      whiteSpace: "normal",
                      wordBreak: "break-all",
                    },
                  },
                  "代表该SQL单次执行占总消耗时间的百分比"
                ),
              ]
            );
          },
          renderHeader: (h, params) => {
            return params.column._width >= 80
              ? h("span", {}, "el%")
              : h(
                  "span",
                  {
                    domProps: {
                      title: "el%",
                    },
                  },
                  "el%"
                );
          },
        },
        {
          title: "cp%",
          key: "cputPer",
          align: "center",
          sortable: true,
          isSetting: false,
          render: (h, params) => {
            return h(
              "tooltip",
              {
                props: {
                  placement: "right",
                },
              },
              [
                h("span", params.row.cputPer),
                h(
                  "span",
                  {
                    slot: "content",
                    style: {
                      whiteSpace: "normal",
                      wordBreak: "break-all",
                    },
                  },
                  "代表该SQL单次执行cpu时间占总cpu时间的百分比"
                ),
              ]
            );
          },
          renderHeader: (h, params) => {
            return params.column._width >= 80
              ? h("span", {}, "cp%")
              : h(
                  "span",
                  {
                    domProps: {
                      title: "cp%",
                    },
                  },
                  "cp%"
                );
          },
        },
        {
          title: "pc%",
          key: "parsePer",
          align: "center",
          sortable: true,
          isSetting: false,
          render: (h, params) => {
            return h(
              "tooltip",
              {
                props: {
                  placement: "right",
                },
              },
              [
                h("span", params.row.parsePer),
                h(
                  "span",
                  {
                    slot: "content",
                    style: {
                      whiteSpace: "normal",
                      wordBreak: "break-all",
                    },
                  },
                  "代表该SQL parse占总parse的百分比"
                ),
              ]
            );
          },
          renderHeader: (h, params) => {
            return params.column._width >= 80
              ? h("span", {}, "pc%")
              : h(
                  "span",
                  {
                    domProps: {
                      title: "pc%",
                    },
                  },
                  "pc%"
                );
          },
        },
        {
          title: "so%",
          key: "sortPer",
          align: "center",
          sortable: true,
          isSetting: false,
          render: (h, params) => {
            return h(
              "tooltip",
              {
                props: {
                  placement: "right",
                },
              },
              [
                h("span", params.row.sortPer),
                h(
                  "span",
                  {
                    slot: "content",
                    style: {
                      whiteSpace: "normal",
                      wordBreak: "break-all",
                    },
                  },
                  "代表该SQL排序占总排序的百分比"
                ),
              ]
            );
          },
          renderHeader: (h, params) => {
            return params.column._width >= 80
              ? h("span", {}, "so%")
              : h(
                  "span",
                  {
                    domProps: {
                      title: "so%",
                    },
                  },
                  "so%"
                );
          },
        },
        {
          title: "row",
          key: "rowsProcessed",
          align: "center",
          sortable: true,
          isSetting: false,
          render: (h, params) => {
            return h(
              "tooltip",
              {
                props: {
                  placement: "right",
                },
              },
              [
                h("span", formatSize(params.row.rowsProcessed)),
                h(
                  "span",
                  {
                    slot: "content",
                    style: {
                      whiteSpace: "normal",
                      wordBreak: "break-all",
                    },
                  },
                  "代表该SQL单次执行总结果集的均值"
                ),
              ]
            );
          },
          renderHeader: (h, params) => {
            return params.column._width >= 80
              ? h("span", {}, "row")
              : h(
                  "span",
                  {
                    domProps: {
                      title: "row",
                    },
                  },
                  "row"
                );
          },
        },
        {
          title: "app",
          key: "appName",
          align: "center",
          sortable: true,
          isSetting: true,
          render: (h, params) => {
            return h(
              "tooltip",
              {
                props: {
                  placement: "left",
                },
              },
              [
                h("span", params.row.appName),
                h(
                  "span",
                  {
                    slot: "content",
                    style: {
                      whiteSpace: "normal",
                      wordBreak: "break-all",
                    },
                  },
                  "app name"
                ),
              ]
            );
          },
          renderHeader: (h, params) => {
            return params.column._width >= 80
              ? h("span", {}, "app")
              : h(
                  "span",
                  {
                    domProps: {
                      title: "app",
                    },
                  },
                  "app"
                );
          },
        },
      ],
      summaryColumns: [
        {
          title: "更新时间",
          key: "gmtCreate",
        },
        {
          title: "实例",
          key: "service",
        },
        {
          title: "total",
          key: "total",
        },
        {
          title: "fsc",
          key: "fscan",
        },
        {
          title: "exec",
          key: "execs",
        },
        {
          title: "buff",
          key: "lgrds",
        },
        {
          title: "disk",
          key: "phrds",
        },
        {
          title: "rows",
          key: "rows",
        },
        {
          title: "elap",
          key: "elapsed",
        },
        {
          title: "sort",
          key: "sorts",
        },
      ],
      sqlBindColumns: [
        {
          type: "expand",
          width: 100,
          isSetting: true,
          title: "展开",
          render: (h, params) => {
            return h(expandRow, {
              props: {
                row: params.row,
              },
            });
          },
        },
        {
          title: "gmt_create",
          key: "gmtCreate",
        },
        {
          title: "service",
          key: "service",
        },
        {
          title: "sql_text",
          key: "sqlText",
        },
        {
          title: "sql_cnt",
          key: "sqlCnt",
        },
        {
          title: "sql_delta",
          key: "sqlDelta",
        },
      ],
      data: [],
      pageNumber: 1,
      pageSize: 10,
      total: 0,
      selectAllFlag: false,
      dataType: 0,
      hostList: [],
      tabService: "",
      currView: "index",
    };
  },
  methods: {
    init() {
      this.$route.params && this.$route.params.service ? this.$store.dispatch("SETSELECTSERVICE", this.$route.params.service) : null;
      if(this.selectService)
      this.getAllService();
    },
    changePage(v) {
      this.pageNumber = v;
      this.goToReq();
      this.clearSelectAll();
    },
    changePageSize(v) {
      this.pageSize = v;
      this.goToReq();
    },
    changeSort(e) {
      this.sortColumn = e.key;
      this.sortType = e.order;
      if (e.order == "normal") {
        this.sortType = "";
      }
      this.goToReq();
    },
    handleSearch: util.debounce(function() {
      this.pageNumber = 1;
      this.pageSize = 10;
      this.goToReq();
    }, 500),
    handleReset: util.debounce(function () {
      this.searchForm.name = "";
      this.pageNumber = 1;
      this.pageSize = 10;

      // 重新加载数据
      this.goToReq();
    }, 500),
    refresh: util.debounce(function() {
      this.getSqlSummaryList();
    }, 500),
    getSqlSummaryList() {
      this.loading = true;
      let params = {
        pageNumber: this.pageNumber,
        pageSize: this.pageSize,
        sort: this.sortColumn.replace(/([A-Z])/, "_$1").toLowerCase(),
        order: this.sortType,
        service: this.selectService.trim(),
        name: this.searchForm.name.trim(),
        gmtCreate: formatDate(new Date(), "yyyy-MM-dd"),
      };

      getSqlSummaryList(params).then((res) => {
        this.loading = false;
        if (res.success) {
          this.data = res.result.records;
          this.total = res.result.total;
        }
      });
    },
    getSqlSummaryListActionBySummary() {
      this.loading = true;
      let params = {
        pageNumber: this.pageNumber,
        pageSize: this.pageSize,
        service: this.selectService,
        name: this.searchForm.name,
      };

      getSqlSummaryListActionBySummary(params).then((res) => {
        this.loading = false;
        if (res.success) {
          this.data = res.result.records;
          this.total = res.result.total;
        }
      });
    },
    getAllService() {
      this.hostList = this.serviceList;
      this.tabService = this.$route.params.service ? this.$route.params.service : this.selectService;
      this.$store.dispatch('SETSELECTSERVICE', this.tabService);
      this.getSqlSummaryList();
    },
    changeService(e) {
      this.pageNumber = 1;
      this.$store.dispatch("SETSELECTSERVICE", e);
      this.goToReq();
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
    //跳转到sqlPlan页面
    toLinkSqlPlan(param) {
      const _this = this;
      _this.sqlPlanParam = param;
      _this.currView = "sqlPlan";
    },
    changeMode(mode) {
      const _this = this;
      _this.mode = mode;
      _this.data = [];
      _this.total = 0;
      _this.pageNumber = 1;
      _this.goToReq();
    },
    //获得sqlBind列表
    getSqlSummaryListActionBySqlBind() {
      this.loading = true;
      let params = {
        pageNumber: this.pageNumber,
        pageSize: this.pageSize,
        sort: this.sortColumn,
        order: this.sortType,
        service: this.selectService,
        name: this.searchForm.name,
      };

      getSqlSummaryListActionBySqlBind(params).then((res) => {
        this.loading = false;
        if (res.success) {
          this.data = res.result.records;
          this.total = res.result.total;
        }
      });
    },
    //设置走哪个分支请求
    goToReq() {
      const _this = this;
      switch (_this.mode) {
        case "report":
          _this.getSqlSummaryList();
          break;
        case "summary":
          _this.getSqlSummaryListActionBySummary();
          break;
        case "sqlbind":
          _this.getSqlSummaryListActionBySqlBind();
          break;
        default:
          _this.getSqlSummaryList();
      }
    },
    handleSettingChange(columns) {
      this.columns.map((item) => {
        if (item.title === columns.title) {
          item.isSetting = !columns.isSetting;
        }
      });
    },
    //处理展开
    handleExpand(row, index) {
      this.data.forEach((item, i) => {
        //这个循环是为了每次只能展开一个，其他自动收起，不需要可以去掉
        i !== index ? (this.data[i]._expanded = false) : "";
      });
      this.data[index]._expanded = !this.data[index]._expanded;
      this.data.sort();
    },
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
