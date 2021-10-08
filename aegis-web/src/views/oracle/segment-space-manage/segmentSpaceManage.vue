<style lang="less" scoped>
@import "../../../styles/table-common.less";
@import "./segment-space-rpt.less";
</style>
<template>
  <div class="search">
    <historyManage v-if="currView=='his'" :param="segmentHistoryParam" @close="currView='index'"></historyManage>
    <Card v-show="currView=='index'">
      <div class="form">
        <a-form-item v-model="searchForm.name" label="段名" placeholder="请输入段名"></a-form-item>
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
        <div style="display: flex; flex-grow: 1; justify-content: space-between;">
          <Button @click="refresh" icon="md-refresh">刷新</Button>
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
  </div>
</template>

<script>
import { getSegmentSpaceSumReportList, getAllHostList } from "@/api/index";
import util from "@/libs/util.js";
import historyManage from "@views/oracle/segment-space-manage/historyManage";
import AFromItem from "@views/my-components/form/FormItem";
import expandRow from "@views/oracle/segment-space-manage/ExpandRow";
import { mapGetters } from "vuex";

export default {
  name: "segmentSpaceManage",
  components: {
    historyManage,
    "a-form-item": AFromItem,
    expandRow,
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
      hostList: [],
      tabService: "",
      selectCount: 0,
      segmentHistoryParam: {},
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
              },
            });
          },
        },
        {
          title: "更新时间",
          key: "createTime",
          sortable: false,
          width: 150,
        },
        {
          title: "创建者",
          key: "owner",
          sortable: false,
          width: 150,
        },
        {
          title: "段名",
          key: "segmentName",
          sortable: false,
          tooltip: true,
          width: 150,
        },
        {
          title: "分区名",
          key: "partitionName",
          sortable: false,
          width: 150,
        },
        {
          title: "段类型",
          key: "segmentType",
          sortable: false,
          width: 150,
        },
        {
          title: "使用(MB)",
          key: "usedSpace",
          sortable: false,
          width: 150,
        },
        {
          title: "增量(%)",
          key: "diffSpace",
          sortable: false,
          width: 150,
        },
        {
          title: "最后DDL时间",
          key: "lastDdlTime",
          sortable: false,
          width: 200,
        },
        {
          title: "历史数据",
          key: "action",
          align: "center",
          width: 150,
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
                      this.segmentHistoryParam = params.row;
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
      this.getSegmentSpaceSumReportList();
      this.clearSelectAll();
    },
    changePageSize(v) {
      this.pageSize = v;
      this.getSegmentSpaceSumReportList();
    },
    changeSort(e) {
      this.sortColumn = e.key;
      this.sortType = e.order;
      if (e.order == "normal") {
        this.sortType = "";
      }
      this.getSegmentSpaceSumReportList();
    },
    handleSearch: util.debounce(function () {
      this.searchForm.pageNumber = 1;
      this.searchForm.pageSize = 10;
      this.getSegmentSpaceSumReportList();
    }, 500),
    handleReset: util.debounce(function () {
      this.searchForm.name = "";
      this.searchForm.pageNumber = 1;
      this.searchForm.pageSize = 10;

      // 重新加载数据
      this.getSegmentSpaceSumReportList();
    }, 500),
    refresh: util.debounce(function(){
      this.getSegmentSpaceSumReportList();
    }, 500),
    getSegmentSpaceSumReportList() {
      this.loading = true;
      let params = {
        pageNumber: this.pageNumber,
        pageSize: this.pageSize,
        sort: this.sortColumn,
        order: this.sort,
        segmentName: this.searchForm.name.trim(),
        service: this.selectService,
      };

      getSegmentSpaceSumReportList(params).then((res) => {
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
    //获得所有主机
    getAllService() {
      this.hostList = this.serviceList
      this.tabService = this.selectService;
      this.getSegmentSpaceSumReportList();
    },
    changeService(tab) {
      let _this = this;
      this.tabService = tab;
      this.pageNumber = 1;
      this.$store.dispatch("SETSELECTSERVICE", tab);
      this.getSegmentSpaceSumReportList();
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
