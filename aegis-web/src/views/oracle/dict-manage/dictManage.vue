<style lang="less" scoped>
@import "../../../styles/table-common.less";
@import "~@views/oracle/dict-manage/dicManage.less";
</style>
<template>
  <div class="search">
    <dict-columns-info
      v-if="currView == 'columns'"
      :param="columnsInfoParam"
      :tableName="tableName"
      @close="currView = 'index'"
    ></dict-columns-info>
    <dict-index-info
      v-if="currView == 'indexes'"
      :param="indexInfoParam"
      :tableName="tableName"
      @close="currView = 'index'"
    ></dict-index-info>
    <Card v-show="currView == 'index'">
      <div class="form">
        <a-form-item
          v-model="searchForm.name"
          label="表名"
          placeholder="请输入表名"
        ></a-form-item>
        <a-form-item type="slot">
          <slot>
            <Button
              @click="handleSearch"
              type="primary"
              icon="ios-search"
              class="margin-right-10"
              >搜索</Button
            >
            <Button @click="handleReset">重置</Button>
          </slot>
        </a-form-item>
        <a-form-item label="注意" type="slot">
          <Alert banner type="warning" style="display: inline-block"
            >说明:
            此处查询可以全模糊匹配,比如要查cps项目所有的表,输入cps即可</Alert
          >
        </a-form-item>
      </div>

      <Row class="operation">
        <div class="toolbar-container">
          <Button @click="refresh" icon="md-refresh">刷新</Button>
          <Select
            v-model="selectService"
            @on-change="changeService"
            style="width: 8rem"
            class="margin-right-10"
          >
            <Option
              v-for="item in serviceList"
              :value="item.name"
              :key="item.name"
              >{{ item.name }}</Option
            >
          </Select>
        </div>
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
          tooltip-theme="dark"
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
          :page-size-opts="[10, 20, 50]"
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
import { getDictReportList, getAllHostList } from "@/api/index";
import util from "@/libs/util.js";
import DictColumnsInfo from "@views/oracle/dict-manage/dictCloumnsInfo.vue";
import DictIndexInfo from "@views/oracle/dict-manage/dictIndexesInfo.vue";
import AFromItem from "@views/my-components/form/FormItem";
import expandRow from "@views/oracle/dict-manage/ExpandRow";
import { renderNode } from "@/libs/date";
import { mapGetters, mapState, mapActions } from "vuex";
export default {
  name: "dictMange",
  components: {
    "dict-columns-info": DictColumnsInfo,
    "dict-index-info": DictIndexInfo,
    "a-form-item": AFromItem,
    expandRow, //表格展开组件
  },
  computed: {
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
      currView: "index",
      openTip: false,
      openLevel: "0",
      loading: true,
      searchKey: "",
      sortColumn: "table_name",
      sortType: "desc",
      modalType: 0,
      modalTitle: "",
      modalVisible: false,
      hostList: [],
      columnsInfoParam: {},
      indexInfoParam: {},
      searchForm: { name: "" },
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
          type: "expand",
          title: "展开",
          width: 100,
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
          sortable: true,
          tooltip: true,
          width: 150,
        },
        {
          title: "表名",
          key: "tableName",
          sortable: true,
          tooltip: true,
          width: 150,
        },
        {
          title: "行数",
          key: "numRows",
          sortable: true,
          tooltip: true,
          width: 150,
        },
        {
          title: "块数",
          key: "blocks",
          sortable: false,
          width: 150,
        },
        {
          title: "分析时间",
          key: "lastAnalyzed",
          sortable: true,
          width: 150,
        },
        {
          title: "注释",
          key: "comment",
          sortable: false,
          width: 150,
        },
        {
          title: "列信息",
          key: "partitioned",
          sortable: true,
          width: 150,
          render: (h, params) => {
            return h(
              "a",
              {
                on: {
                  click: () => {
                    _this.columnsInfoParam = params.row;
                    _this.currView = "columns";
                  },
                },
              },
              "点击查看"
            );
          },
        },
        {
          title: "索引信息",
          key: "",
          sortable: false,
          width: 150,
          render: (h, params) => {
            return h(
              "a",
              {
                on: {
                  click: () => {
                    _this.indexInfoParam = params.row;
                    _this.currView = "indexes";
                  },
                },
              },
              "点击查看"
            );
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
      this.getDictReportList();
      this.clearSelectAll();
    },
    changePageSize(v) {
      this.pageSize = v;
      this.getDictReportList();
    },
    changeSort(e) {
      //将key值与数据库中的key值对应，将键与数据库列的值对应起来
      this.sortColumn = e.key.replace(/([A-Z])/g, "_$1").toLowerCase();
      this.sortType = e.order;
      if (e.order == "normal") {
        this.sortType = "";
      }
      this.getDictReportList();
    },
    handleSearch: util.debounce(function() {
      this.pageNumber = 1;
      this.pageSize = 10;
      this.getDictReportList();
    }, 500),
    handleReset: util.debounce(function() {
      this.searchForm.name = "";
      this.pageNumber = 1;
      this.pageSize = 10;

      // 重新加载数据
      this.getDictReportList();
    }, 500),
    refresh: util.debounce(function() {
      this.getDictReportList();
    }, 500),
    getPercent(val, total) {
      let availablePercent = Number(val) / Number(total);
      if (Number(val) <= 0) return 0;
      return parseInt(availablePercent.toFixed(2) * 100);
    },
    /**
     *获得数据库实例排序并初始化service
     */
    getAllService() {
      this.hostList = this.serviceList;
      this.tabService = this.selectService;
      this.getDictReportList();
    },
    changeService(e) {
      this.pageNumber = 1;
      this.$store.commit("SETSELECTSERVICE", e);
      this.getDictReportList();
    },
    getDictReportList() {
      this.loading = true;
      let params = {
        pageNumber: this.pageNumber,
        pageSize: this.pageSize,
        sort: this.sortColumn,
        order: this.sortType,
        name: this.searchForm.name.trim(),
        service: this.selectService,
      };

      getDictReportList(params).then((res) => {
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
    /**
     * 展开
     */
    handleExpand(row, index) {
      this.data.map((item, i) => {
        i !== index ? (item._expanded = false) : "";
        return item;
      });
      this.data[index]._expanded = !this.data[index]._expanded;
      /**
       * 只要导致表格数据重新渲染即可
       */
      this.data = this.data.splice(0);
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
