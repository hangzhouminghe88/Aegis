<style lang="less" scoped>
@import "../../../styles/table-common.less";
@import "./indexVisitedManage.less";
</style>
<template>
  <div class="search">
    <historyManage
      v-if="currView=='his'"
      :service="service"
      :tableName="tableName"
      @close="currView='index'"
    ></historyManage>
    <Card v-if="currView=='index'">
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
          :columns="columnsIsSetting"
          :data="data"
          ref="table"
          sortable="custom"
          @on-sort-change="changeSort"
          @on-selection-change="changeSelect"
          @on-row-click="handleExpand"
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
    <template v-if="currView === 'indexe'">
      <ind-exe-manage @close="currView = 'index'"></ind-exe-manage>
    </template>
  </div>
</template>

<script>
import { getTableMonIndsList, getAllHostList } from "@/api/index";
import historyManage from "./historyManage.vue";
import AFromItem from "@views/my-components/form/FormItem";
import SettingTableColumn from "@views/my-components/settingTableColumn/SettingTableColumn";
import util from "@/libs/util.js";
import IndexesManage from "@views/oracle/index-visited-manage/indexesManage";
import expandRow from "@views/oracle/index-visited-manage/ExpandRow";
import { mapGetters } from "vuex";

export default {
  name: "index-visited-manage",
  components: {
    historyManage,
    "a-form-item": AFromItem,
    "setting-table-column": SettingTableColumn,
    "ind-exe-manage": IndexesManage,
    expandRow,
  },
  computed: {
    columnsIsSetting() {
      return this.columns ? this.columns.filter((item) => item.isSetting) : [];
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
      sortColumn: "ind_unused",
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
      tableName: "",
      submitLoading: false,
      selectList: [],
      selectCount: 0,
      indexeParam: {},
      testSet: new Set(),
      columns: [
        {
          type: "expand",
          title: "展开",
          width: 100,
          isSetting: true,
          render: (h, params) => {
            return h(expandRow, {
              props: {
                row: params.row,
              },
            });
          },
        },
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
          isSetting: true,
          ellipsis: true,
          tooltip: true,
          render: (h, params) => {
            return h(
              "a",
              {
                on: {
                  click: () => {
                    this.indexeParam = params.row;
                    this.currView = "indexe";
                  },
                },
              },
              params.row.tableName
            );
          },
        },
        {
          title: "存储(MB)",
          key: "total_size",
          isSetting: true,
          render: (h, params) => {
            return h("span", params.row.totalSize);
          },
        },
        {
          title: "SQL总数",
          key: "sql_total",
          sortable: true,
          isSetting: true,
          render: (h, params) => {
            return h("span", params.row.sqlTotal);
          },
        },
        {
          title: "索引数",
          key: "ind_total",
          isSetting: true,
          sortable: true,
          render: (h, params) => {
            return h("span", params.row.indTotal);
          },
        },
        {
          title: "冗余数",
          key: "redu_total",
          sortable: true,
          isSetting: true,
          render: (h, params) => {
            return h("span", params.row.reduTotal);
          },
        },
        {
          title: "使用数",
          key: "ind_used",
          sortable: true,
          isSetting: false,
          render: (h, params) => {
            return h("span", params.row.indUsed);
          },
        },
        {
          title: "未用数",
          key: "ind_unused",
          sortable: true,
          isSetting: false,
          render: (h, params) => {
            return h("span", params.row.indUnused);
          },
        },
        {
          title: "最大列",
          key: "col_max",
          sortable: true,
          isSetting: false,
          render: (h, params) => {
            return h("span", params.row.colMax);
          },
        },
        {
          title: "平均列",
          key: "col_avg",
          sortable: true,
          isSetting: false,
          render: (h, params) => {
            return h("span", params.row.colAvg);
          },
        },
        {
          title: "更新时间",
          key: "createTime",
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
      this.getTableMonIndsList();
      this.clearSelectAll();
    },
    changePageSize(v) {
      this.pageSize = v;
      this.getTableMonIndsList();
    },
    changeSort(e) {
      this.sortColumn = e.key;
      this.sortType = e.order;
      if (e.order == "normal") {
        this.sortType = "";
      }
      this.getTableMonIndsList();
    },
    handleSearch: util.debounce(function () {
      this.pageNumber = 1;
      this.pageSize = 10;
      this.getTableMonIndsList();
    }, 500),
    handleReset: util.debounce(function () {
      this.searchForm.name = "";
      this.pageNumber = 1;
      this.pageSize = 10;
      // 重新加载数据
      this.getTableMonIndsList();
    }, 500),
    refresh: util.debounce(function () {
      this.getTableMonIndsList();
    }, 500),
    getAllService() {
      this.hostList = this.serviceList;
      this.tabService = this.selectService;
      this.getTableMonIndsList();
    },
    changeService(e) {
      this.pageNumber = 1;
      this.$store.commit('SETSELECTSERVICE', e);
      this.getTableMonIndsList();
    },
    getTableMonIndsList() {
      this.loading = true;
      let params = {
        pageNumber: this.pageNumber,
        pageSize: this.pageSize,
        sort: this.sortColumn,
        order: this.sortType,
        tableName: this.searchForm.name.trim(),
        service: this.selectService,
      };

      getTableMonIndsList(params).then((res) => {
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
    /**
     * 点解行展开
     */
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
        this.getAllService();
      }
    }
   }
};
</script>
