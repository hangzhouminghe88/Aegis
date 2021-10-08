<style lang="less" scoped>
@import "../../../styles/table-common.less";
@import "./tableSpaceManage.less";
</style>
<template>
  <div class="search">
    <historyManage
      v-if="currView=='his'"
      :name="tablespace"
      :service="service"
      @close="currView='index'"
    ></historyManage>
    <Card v-show="currView=='index'">
      <div class="form">
        <a-form-item v-model="searchForm.name" label="表空间名" placeholder="请输入表空间名"></a-form-item>
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
        <div class="tablespace_operation">
          <Button @click="refresh" icon="md-refresh">刷新</Button>
          <span>
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
          </span>
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
    <template v-if="currView === 'include'">
      <include-report :param="includeParam" @close="currView='index'"></include-report>
    </template>
  </div>
</template>

<script>
import { getTableSpaceSumReportList, getAllHostList } from "@/api/index";
import historyManage from "./historyManage.vue";
import AFromItem from "@views/my-components/form/FormItem";
import SettingTableColumn from "@views/my-components/settingTableColumn/SettingTableColumn";
import IncludeReport from "@views/oracle/table-space-manage/IncludeReport";
import util from "@/libs/util.js";
import { mapGetters } from "vuex";
export default {
  name: "alert-tablespace-manage",
  components: {
    historyManage,
    "a-form-item": AFromItem,
    "setting-table-column": SettingTableColumn,
    "include-report": IncludeReport,
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
      sortColumn: "gmt_create",
      sortType: "desc",
      modalType: 0,
      modalTitle: "",
      modalVisible: false,
      detailModalTitle: "",
      detailModalVisible: false,
      currentTableSpaceSum: null,
      searchForm: { name: "" },
      form: {},
      formValidate: {},
      tablespace: "",
      service: "",
      submitLoading: false,
      selectList: [],
      selectCount: 0,
      hostList: [],
      tabService: "",
      //include页面携带的参数
      includeParam: {},
      columns: [
        {
          title: "实例名",
          key: "service",
          sortable: false,
          isSetting: true,
          width: 150,
        },
        {
          title: "表空间",
          key: "name",
          sortable: false,
          isSetting: true,
          width: 150,
          render: (h, params) => {
            return h(
              "a",
              {
                on: {
                  click: () => {
                    this.includeParam = params.row;
                    this.currView = "include";
                  },
                },
              },
              params.row.name
            );
          },
        },
        {
          title: "存储总数(MB)",
          key: "totalSpace",
          sortable: false,
          isSetting: true,
          width: 150,
        },
        {
          title: "使用(MB)",
          key: "usedSpace",
          sortable: false,
          isSetting: true,
          width: 150,
        },
        {
          title: "空间(MB)",
          key: "freeSpace",
          sortable: false,
          isSetting: true,
          width: 150,
        },
        {
          title: "增量(MB)",
          key: "diffSpace",
          sortable: false,
          isSetting: true,
          width: 150,
        },
        {
          title: "均值(MB)",
          key: "avgSpace",
          sortable: false,
          width: 100,
          isSetting: document.documentElement.clientWidth > 1300 ? true : false,
        },
        {
          title: "使用率(%)",
          key: "usedRate",
          sortable: false,
          width: 150,
          isSetting: document.documentElement.clientWidth > 1300 ? true : false,
        },
        {
          title: "可用天数",
          key: "usedDays",
          width: 150,
          sortable: false,
          isSetting: document.documentElement.clientWidth > 1300 ? true : false,
        },
        {
          title: "更新日期",
          key: "gmtCreate",
          width: 150,
          sortable: false,
          isSetting: true,
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
                    icon: "ios-eye-outline",
                  },
                  on: {
                    click: () => {
                      this.tablespace = params.row.name;
                      this.service = params.row.service;
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
      this.getTableSpaceSumReportList();
      this.clearSelectAll();
    },
    changePageSize(v) {
      this.pageSize = v;
      this.getTableSpaceSumReportList();
    },
    getAllService() {
      this.hostList = this.serviceList
      this.tabService = this.selectService;
      this.getTableSpaceSumReportList();
    },
    changeSort(e) {
      this.sortColumn = e.key;
      this.sortType = e.order;
      if (e.order == "normal") {
        this.sortType = "";
      }
      this.getTableSpaceSumReportList();
    },
    handleSearch: util.debounce(function() {
      this.searchForm.pageNumber = 1;
      this.searchForm.pageSize = 10;
      this.getTableSpaceSumReportList();
    }, 500),
    handleReset: util.debounce(function() {
      this.searchForm.name = "";
      this.searchForm.pageNumber = 1;
      this.searchForm.pageSize = 10;
      // 重新加载数据
      this.getTableSpaceSumReportList();
    }, 500),
    refresh: util.debounce(function() {
      this.getTableSpaceSumReportList();
    }, 500),
    getTableSpaceSumReportList() {
      this.loading = true;
      let params = {
        pageNumber: this.pageNumber,
        pageSize: this.pageSize,
        sort: this.sortColumn,
        order: this.sort,
        name: this.searchForm.name.trim(),
        service: this.selectService,
      };

      getTableSpaceSumReportList(params).then((res) => {
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
    changeService(tab) {
      let _this = this;
      this.tabService = tab;
      this.pageNumber = 1;
      this.$store.dispatch("SETSELECTSERVICE", tab);
      this.getTableSpaceSumReportList();
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
