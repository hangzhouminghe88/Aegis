<style lang="less" scoped>
@import "../../../styles/table-common.less";
@import "./sequenceMonManage.less";
</style>
<template>
  <div class="search">
    <historyManage
      v-if="currView=='his'"
      :service="service"
      :sequence="sequence"
      @close="currView='index'"
    ></historyManage>
    <Card v-show="currView=='index'">
      <div class="form">
        <a-form-item v-model="searchForm.name" label="序列名" placeholder="请输入序列名"></a-form-item>
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
            <Select v-model="selectService" @on-change="changeService" style="margin-right:25px">
              <Option
                v-for="item in serviceList"
                :value="item.name"
                :key="item.name"
              >{{item.name}}</Option>
            </Select>
          </div>
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
import { getSequenceReportList, getAllHostList } from "@/api/index";
import historyManage from "./historyManage.vue";
import AFromItem from "@views/my-components/form/FormItem";
import util from "@/libs/util.js";
import expandRow from "@views/oracle/sequence-mon-manage/ExpandRow";
import { mapGetters } from "vuex";
export default {
  name: "sequence-mon-manage",
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
      sequence: "",
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
          title: "所有者",
          key: "owner",
          sortable: false,
          width: 150,
        },
        {
          title: "序列名称",
          key: "sequence",
          sortable: true,
          width: 150,
          tooltip: true,
        },
        {
          title: "最小值",
          key: "min_value",
          sortable: true,
          width: 100,
          render: (h, params) => {
            return h("span", params.row.minValue);
          },
        },
        {
          title: "递增数",
          key: "incr_by",
          sortable: true,
          width: 100,
          render: (h, params) => {
            return h("span", params.row.incrBy);
          },
        },
        {
          title: "最后序列号",
          key: "last_number",
          sortable: true,
          width: 100,
          render: (h, params) => {
            return h("span", params.row.lastNumber);
          },
        },
        {
          title: "每天增量",
          key: "delta",
          sortable: true,
          width: 100,
        },
        {
          title: "缓存数",
          key: "cache_size",
          sortable: true,
          width: 100,
          render: (h, params) => {
            return h("span", params.row.cacheSize);
          },
        },
        {
          title: "更新时间",
          key: "createTime",
          sortable: false,
          width: 150,
        },
        {
          title: "操作",
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
                      this.service = params.row.service;
                      this.sequence = params.row.sequence;
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
      this.getSequenceReportList();
      this.clearSelectAll();
    },
    changePageSize(v) {
      this.pageSize = v;
      this.getSequenceReportList();
    },
    changeSort(e) {
      this.sortColumn = e.key;
      this.sortType = e.order;
      if (e.order == "normal") {
        this.sortType = "";
      }
      this.getSequenceReportList();
    },
    handleSearch: util.debounce(function() {
      this.pageNumber = 1;
      this.pageSize = 10;
      this.getSequenceReportList();
    }, 500),
    handleReset: util.debounce(function() {
      this.searchForm.name = "";
      this.pageNumber = 1;
      this.pageSize = 10;

      // 重新加载数据
      this.getSequenceReportList();
    }, 500),
    getPercent(val, total) {
      let availablePercent = Number(val) / Number(total);
      if (Number(val) <= 0) return 0;
      return parseInt(availablePercent.toFixed(2) * 100);
    },
    refresh: util.debounce(function() {
      this.getSequenceReportList();
    }, 500),
    getAllService() {
      this.hostList = this.serviceList;
      this.tabService = this.selectService;
      this.getSequenceReportList();
    },
    changeService(e) {
      this.pageNumber = 1;
      this.$store.dispatch("SETSELECTSERVICE", e);
      this.getSequenceReportList();
    },
    getSequenceReportList() {
      this.loading = true;
      let params = {
        pageNumber: this.pageNumber,
        pageSize: this.pageSize,
        sort: this.sortColumn,
        order: this.sortType,
        name: this.searchForm.name.trim(),
        service: this.selectService,
      };

      getSequenceReportList(params).then((res) => {
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
         this.init();
      }
    }
   }
};
</script>
