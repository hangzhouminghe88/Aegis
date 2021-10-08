<style lang="less" scoped>
@import "../../../styles/table-common.less";
@import "./recordChangeManage.less";
</style>
<template>
  <div class="search">
    <historyManage
      v-if="currView=='his'"
      :service="service"
      :tableName="tableName"
      @close="currView='index'"
    ></historyManage>
    <Card v-show="currView=='index'">
      <div class="form">
        <a-form-item v-model="searchForm.name" label="表名称" placeholder="请输入表名称"></a-form-item>
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
              <Radio v-for="item in modeList" :label="item.label" :key="item.value">{{item.value}}</Radio>
            </RadioGroup>
          </div>
          <div>
            <Select
              v-model="tabService"
              @on-change="changeService"
              style="width: 8rem;"
              class="margin-right-10"
            >
              <Option
                v-for="item in hostList"
                :value="item.name"
                :key="item.name"
              >{{item.name}}</Option>
            </Select>
            <setting-table-column
              :columns="mode==='report' || mode==='app' ? columns : columnsSys"
              @setting-change="handleSettingChange"
            ></setting-table-column>
          </div>
        </div>
      </Row>
      <Row>
        <Table
          v-if="mode==='report' || mode==='app'"
          :loading="loading"
          border
          :columns="columnsIsSetting"
          :data="data"
          ref="table"
          sortable="custom"
          @on-sort-change="changeSort"
          @on-selection-change="changeSelect"
        ></Table>
        <Table
          v-if="mode==='sys'"
          :loading="loading"
          border
          :columns="columnsSysIsSetting"
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
import { getTableMonList, getAllHostList } from "@/api/index";
import historyManage from "./historyManage.vue";
import AFromItem from "@views/my-components/form/FormItem";
import SettingTableColumn from "@views/my-components/settingTableColumn/SettingTableColumn";
import util from "@/libs/util.js";
import { formatRateTo4, formatSize } from "@/libs/date";
import { mapGetters } from "vuex";
export default {
  name: "record-change-manage",
  components: {
    historyManage,
    "a-form-item": AFromItem,
    "setting-table-column": SettingTableColumn,
  },
  computed: {
    columnsIsSetting() {
      return this.columns ? this.columns.filter((item) => item.isSetting) : [];
    },
    columnsSysIsSetting() {
      return this.columnsSys
        ? this.columnsSys.filter((item) => item.isSetting)
        : [];
    },
    selectService: {
      get() {
        return this.$store.state.service.selectService;
      },
      set(value) {
        this.$store.state.service.selectService=value
      }
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
      sortColumn: "create_time",
      sortType: "desc",
      modalType: 0,
      modalTitle: "",
      modalVisible: false,
      hostList: [],
      searchForm: { name: "" },
      mode: "report",
      modeList: [
        {
          label: "report",
          value: "记录变更报表",
        },
        {
          label: "app",
          value: "应用模式对象",
        },
        {
          label: "sys",
          value: "系统模式对象",
        },
      ],
      form: {},
      formValidate: {},
      tabService: "",
      service: "",
      tableName: "",
      submitLoading: false,
      selectList: [],
      selectCount: 0,
      columns: [
        {
          title: "创建者",
          key: "owner",
          sortable: false,
          isSetting: true,
        },
        {
          title: "表名",
          key: "table_name",
          sortable: true,
          width: 200,
          isSetting: true,
          render: (h, params) => {
            return h("span", params.row.tableName);
          },
        },
        {
          title: "新增数",
          key: "ins_diff",
          sortable: true,
          isSetting: true,
          render: (h, params) => {
            return h("span", params.row.insDiff);
          },
        },
        {
          title: "新增%",
          sortable: false,
          isSetting: true,
          render: (h, params) => {
            let per = this.getPercent(params.row.insDiff, params.row.dmlDiff);
            return h("span", per);
          },
        },
        {
          title: "更新数",
          key: "upd_diff",
          sortable: true,
          isSetting: true,
          render: (h, params) => {
            return h("span", params.row.updDiff);
          },
        },
        {
          title: "更新%",
          sortable: false,
          isSetting: true,
          render: (h, params) => {
            let per = this.getPercent(params.row.updDiff, params.row.dmlDiff);
            return h("span", per);
          },
        },
        {
          title: "删除数",
          key: "del_diff",
          sortable: true,
          isSetting: false,
          render: (h, params) => {
            return h("span", params.row.delDiff);
          },
        },
        {
          title: "删除%",
          sortable: false,
          isSetting: false,
          render: (h, params) => {
            let per = this.getPercent(params.row.delDiff, params.row.dmlDiff);
            return h("span", per);
          },
        },
        {
          title: "DML数",
          key: "dml_diff",
          sortable: true,
          isSetting: false,
          render: (h, params) => {
            return h("span", params.row.dmlDiff);
          },
        },
        {
          title: "总行数",
          key: "num_rows",
          sortable: true,
          isSetting: true,
          render: (h, params) => {
            return h("span", params.row.numRows);
          },
        },
        {
          title: "trunc",
          key: "truncated",
          sortable: false,
          isSetting: true,
          render: (h, params) => {
            return h("table-state", {
              props: {
                state: params.row.truncated,
                content: params.row.truncated,
              },
            });
          },
        },
        {
          title: "更新时间",
          key: "createTime",
          sortable: true,
          isSetting: true,
        },
        {
          title: "操作",
          key: "action",
          align: "center",
          width: 250,
          isSetting: true,
          render: (h, params) => {
            return h("div", [
              h(
                "Button",
                {
                  props: {
                    type: "error",
                    size: "small",
                  },
                  style: {
                    marginRight: "15px",
                  },
                  on: {
                    click: () => {
                      this.service = params.row.service;
                      this.tableName = params.row.tableName;
                      this.currView = "his";
                    },
                  },
                },
                "查看历史"
              ),
            ]);
          },
        },
      ],
      columnsSys: [
        {
          title: "更新时间",
          key: "createTime",
          sortable: true,
          isSetting: true,
        },
        {
          title: "创建者",
          sortable: true,
          key: "owner",
          isSetting: true,
        },
        {
          title: "表名",
          sortable: true,
          key: "tableName",
          isSetting: true,
        },
        {
          title: "新增数",
          sortable: true,
          key: "insDiff",
          isSetting: true,
          render: (h, param) => {
            return h("span", formatSize(param.row.insDiff));
          },
        },
        {
          title: "新增%",
          key: "dmlDiffPer",
          isSetting: true,
          render: (h, param) => {
            return h(
              "span",
              formatRateTo4(param.row.insDiff / (param.row.dmlDiff + 1))
            );
          },
        },
        {
          title: "更新数",
          key: "updDiff",
          isSetting: true,
          render: (h, param) => {
            return h("span", formatSize(param.row.updDiff));
          },
        },
        {
          title: "删除数",
          key: "delDiff",
          sortable: true,
          isSetting: false,
          render: (h, param) => {
            return h("span", formatSize(param.row.delDiff));
          },
        },
        {
          title: "DML数",
          key: "dmlDiff",
          sortable: true,
          isSetting: false,
          render: (h, param) => {
            return h("span", formatSize(param.row.dmlDiff));
          },
        },
        {
          title: "总行数",
          key: "numRows",
          sortable: true,
          isSetting: false,
          render: (h, param) => {
            return h("span", formatSize(param.row.numRows));
          },
        },
        {
          title: "trunc",
          key: "truncated",
          isSetting: false,
          render: (h, params) => {
            return h("table-state", {
              props: {
                state: params.row.truncated,
                content: params.row.truncated,
              },
            });
          },
        },
        {
          title: "历史数据",
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
                    icon: "ios-eye-outline",
                  },
                  style: {
                    marginRight: "15px",
                  },
                  on: {
                    click: () => {
                      this.service = params.row.service;
                      this.tableName = params.row.tableName;
                      this.currView = "his";
                    },
                  },
                },
                "查看历史"
              ),
            ]);
          },
        },
      ],
      data: [],
      pageNumber: 1,
      pageSize: 10,
      total: 0,
      selectAllFlag: false,
      dataType: 0,
      groupList: [],
    };
  },
  methods: {
    init() {
      if(this.selectService)
      this.getAllService();
    },
    changePage(v) {
      this.pageNumber = v;
      this.getTableMonList();
      this.clearSelectAll();
    },
    changePageSize(v) {
      this.pageSize = v;
      this.pageNumber = 1;
      this.getTableMonList();
    },
    changeSort(e) {
      this.sortColumn = e.key.replace(/([A-Z])/g, "_$1").toLowerCase();
      this.sortType = e.order;
      if (e.order == "normal") {
        this.sortType = "";
      }
      this.getTableMonList();
    },
    handleSearch: util.debounce(function () {
      this.pageNumber = 1;
      this.pageSize = 10;
      this.getTableMonList();
    }, 500),
    handleReset: util.debounce(function () {
      this.searchForm.name = "";
      this.pageNumber = 1;
      this.pageSize = 10;

      // 重新加载数据
      this.getTableMonList();
    }, 500),
    refresh: util.debounce(function () {
      this.getTableMonList();
    }, 500),
    getPercent(val, total) {
      let availablePercent = Number(val) / Number(total);
      if (Number(val) <= 0) return 0;
      return parseInt(availablePercent.toFixed(2) * 100);
    },
    getAllService() {
      this.hostList = this.serviceList;
      this.tabService = this.selectService;
      this.getTableMonList();
    },
    changeService(e) {
      this.pageNumber = 1;
      this.$store.commit('SETSELECTSERVICE', e);
      this.getTableMonList();
    },
    getTableMonList() {
      this.loading = true;
      this.data = [];
      let params = {
        pageNumber: this.pageNumber,
        pageSize: this.pageSize,
        sort: this.sortColumn,
        order: this.sortType,
        name: this.searchForm.name.trim(),
        service: this.tabService,
        mode: this.mode === "report" ? "" : this.mode,
      };

      getTableMonList(params).then((res) => {
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
    changeMode(v) {
      const _this = this;
      _this.mode = v;
      _this.pageNumber = 1;
      _this.getTableMonList();
    },
    handleSettingChange(columns) {
      if (this.mode === "report" || this.mode === "app") {
        this.columns.map((item) => {
          if (item.title === columns.title) {
            item.isSetting = !columns.isSetting;
          }
        });
      } else {
        this.columnsSys.map((item) => {
          if (item.title === columns.title) {
            item.isSetting = !columns.isSetting;
          }
        });
      }
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
