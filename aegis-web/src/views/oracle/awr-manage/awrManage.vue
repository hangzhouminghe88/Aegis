<style lang="less" scoped>
@import "../../../styles/table-common.less";
@import "./awrManage.less";
</style>
<template>
  <div class="search">
    <historyManage v-if="currView=='his'" :param="topSqlParam" @close="currView='index'"></historyManage>
    <metric-report v-if="currView=='metric'" :param="metricParam" @close="currView='index'"></metric-report>
    <Card v-show="currView=='index'">
      <div class="form">
        <a-form-item v-model="searchForm.name" label="快照ID" placeholder="请输入快照ID"></a-form-item>
        <a-form-item type="slot">
          <slot>
            <label name="timeRange" class="mh-input__label margin-right-10">请选择日期范围</label>
            <DatePicker
              type="datetimerange"
              v-model="timeRange"
              placeholder="请选择日期范围"
              style="width: 385px"
            ></DatePicker>
          </slot>
        </a-form-item>
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
            <setting-table-column :columns="columns" @setting-change="handleSettingChange"></setting-table-column>
          </div>
        </div>
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
import { getSnapshotList, getAllHostList } from "@/api/index";
import historyManage from "./historyManage.vue";
import AFromItem from "@views/my-components/form/FormItem";
import MetricReport from "./MetricReport.vue";
import SettingTableColumn from "@views/my-components/settingTableColumn/SettingTableColumn";
import util from "@/libs/util.js";
import { formatDate } from "@/libs/date";
import { mapGetters, mapState, mapActions } from "vuex";

export default {
  name: "ash-manage",
  components: {
    historyManage,
    "metric-report": MetricReport,
    "a-form-item": AFromItem,
    "setting-table-column": SettingTableColumn,
  },
  computed: {
    columnsSettings() {
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
      form: {},
      formValidate: {},
      tabService: "",
      service: "",
      submitLoading: false,
      selectList: [],
      selectCount: 0,
      topSqlParam: {},
      metricParam: {},
      timeRange: [new Date(new Date().getTime() - 604800000), new Date()],
      columns: [
        {
          title: "快照ID",
          key: "id",
          width: 100,
          sortable: false,
          isSetting: true,
        },
        {
          title: "全扫描",
          key: "fscan",
          sortable: false,
          isSetting: true,
        },
        {
          title: "新SQL",
          key: "isNew",
          sortable: false,
          isSetting: true,
        },
        {
          title: "I/U/D",
          key: "",
          sortable: false,
          isSetting: true,
          render: (h, params) => {
            let title =
              params.row.ins + "/" + params.row.upd + "/" + params.row.del;

            return h("span", title.replace(/null/g, ""));
          },
        },
        {
          title: "SQL总数",
          key: "total",
          sortable: false,
          isSetting: false,
        },
        {
          title: "快照开始时间",
          key: "createTime",
          sortable: false,
          isSetting: true,
        },
        {
          title: "快照结束时间",
          key: "endTime",
          sortable: false,
          isSetting: true,
        },
        {
          title: "报表",
          key: "action",
          align: "center",
          width: 320,
          isSetting: true,
          render: (h, params) => {
            return h("div", [
              h(
                "Button",
                {
                  props: {
                    type: "info",
                    size: "small",
                  },
                  style: {
                    marginRight: "8px",
                  },
                  on: {
                    click: () => {
                      this.topSqlParam = params.row;
                      this.currView = "his";
                    },
                  },
                },
                "TOPSQL"
              ),
              params.row.reportName
                ? h(
                    "Button",
                    {
                      props: {
                        type: "success",
                        size: "small",
                      },
                      style: {
                        marginRight: "8px",
                      },
                      on: {
                        click: () => {
                          let str = window.location.origin +
                            "/tmp/aegis/awr/" +
                            params.row.reportName;
                          window.open(str, '_blank')
                        },
                      },
                    },
                    "AWR"
                  )
                : null,
              params.row.addmRptName
                ? h(
                    "Button",
                    {
                      props: {
                        type: "warning",
                        size: "small",
                      },
                      style: {
                        marginRight: "8px",
                      },
                      on: {
                        click: () => {
                          let str = window.location.origin +
                            "/tmp/aegis/adm/" +
                            params.row.addmRptName;
                          window.open(str, '_blank')
                        }
                      },
                    },
                    "ADDM"
                  )
                : null,
              h(
                "Button",
                {
                  props: {
                    type: "error",
                    size: "small",
                  },
                  on: {
                    click: () => {
                      this.metricParam = params.row;
                      this.currView = "metric";
                    },
                  },
                },
                "METRIC"
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
    //请求所有实例
    init() {
      if(this.selectService)
      this.getAllService();
    },
    //改变当前页
    changePage(v) {
      this.pageNumber = v;
      this.getSnapshotList();
      this.clearSelectAll();
    },
    //改变当前页码
    changePageSize(v) {
      this.pageSize = v;
      this.getSnapshotList();
    },
    //排序
    changeSort(e) {
      this.sortColumn = e.key;
      this.sortType = e.order;
      if (e.order == "normal") {
        this.sortType = "";
      }
      this.getSnapshotList();
    },
    //搜索
    handleSearch: util.debounce(function () {
      this.pageNumber = 1;
      this.pageSize = 10;
      this.getSnapshotList();
    }, 500),
    //重置
    handleReset: util.debounce(function () {
      this.searchForm.name = "";
      this.pageNumber = 1;
      this.pageSize = 10;
      // 重新加载数据
      this.getSnapshotList();
    }, 500),
    //获得所有实例
    getAllService() {
      this.hostList = this.serviceList;
      this.tabService = this.selectService;
      this.getSnapshotList();
    },
    //切换实例
    changeService(e) {
      this.pageNumber = 1;
      this.$store.commit('SETSELECTSERVICE', e);
      this.getSnapshotList();
    },
    //刷新
    refresh: util.debounce(function () {
      this.getSnapshotList();
    }, 500),
    getSnapshotList() {
      this.loading = true;
      let params = {
        pageNumber: this.pageNumber,
        pageSize: this.pageSize,
        sort: this.sortColumn,
        order: this.sortType,
        sqlId: this.searchForm.name.trim(),
        service: this.selectService,
        startTime: formatDate(this.timeRange[0], "yyyy-MM-dd hh:mm:ss"),
        endTime: formatDate(this.timeRange[1], "yyyy-MM-dd hh:mm:ss"),
      };

      getSnapshotList(params).then((res) => {
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
      this.columns.map((item) => {
        if (item.title === columns.title) {
          item.isSetting = !columns.isSetting;
        }
      });
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
