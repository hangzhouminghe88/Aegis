<style lang="less" scoped>
@import "../../../styles/table-common.less";
@import "./segmentSpaceManage.less";
</style>
<template>
  <div class="search">
    <historyManage
      v-if="currView=='his'"
      :segmentName="segmentName"
      :service="service"
      @close="currView='index'"
    ></historyManage>
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
        <Button @click="refresh" icon="md-refresh">刷新</Button>
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
import { getSegmentSpaceSumAlertList } from "@/api/index";
import historyManage from "./historyManage.vue";
import AFromItem from "@views/my-components/form/FormItem";
import util from "@/libs/util.js";
export default {
  name: "alert-segmentspace-manage",
  components: {
    historyManage,
    "a-form-item": AFromItem
  },
  data() {
    return {
      currView: "index",
      openTip: false,
      openLevel: "0",
      loading: true,
      searchKey: "",
      sortColumn: "createTime",
      sortType: "desc",
      modalType: 0,
      modalTitle: "",
      modalVisible: false,
      searchForm: { segmentName: "" },
      form: {},
      formValidate: {},
      segmentName: "",
      service: "",
      submitLoading: false,
      selectList: [],
      selectCount: 0,
      columns: [
        {
          title: "实例名",
          key: "service",
          sortable: false,
          width: 120,
          ellipsis: true,
          tooltip: true,
          isSetting: true
        },
        {
          title: "段名",
          key: "segmentName",
          sortable: false,
          width: 120,
          ellipsis: true,
          tooltip: true,
          isSetting: true
        },
        {
          title: "分区名",
          key: "partitionName",
          sortable: false,
          width: 120,
          ellipsis: true,
          tooltip: true,
          isSetting: true
        },
        {
          title: "使用(MB)",
          key: "usedSpace",
          sortable: false,
          width: 120,
          ellipsis: true,
          tooltip: true,
          isSetting: true
        },
        {
          title: "增量(MB)",
          key: "diffSpace",
          sortable: false,
          width: 120,
          ellipsis: true,
          tooltip: true,
          isSetting: true
        },
        {
          title: "最后DDL时间",
          key: "lastDdlTime",
          sortable: false,
          width: 120,
          ellipsis: true,
          tooltip: true,
          isSetting: true
        },
        {
          title: "更新日期",
          key: "createTime",
          sortable: false,
          width: 120,
          ellipsis: true,
          tooltip: true
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
                    icon: "ios-eye-outline"
                  },
                  on: {
                    click: () => {
                      this.segmentName = params.row.segmentName;
                      this.service = params.row.service;
                      this.currView = "his";
                    }
                  }
                },
                "查看历史"
              )
            ]);
          }
        }
      ],
      data: [],
      pageNumber: 1,
      pageSize: 10,
      total: 0,
      selectAllFlag: false,
      dataType: 0,
      groupList: []
    };
  },
  methods: {
    //初始化查询段空间列表
    init() {
      this.getSegmentSpaceSumAlertList();
    },
    //切换当前页发出请求
    changePage(v) {
      this.pageNumber = v;
      this.getSegmentSpaceSumAlertList();
      this.clearSelectAll();
    },
    //切换页码发出的请求
    changePageSize(v) {
      this.pageSize = v;
      this.getSegmentSpaceSumAlertList();
    },
    changeSort(e) {
      this.sortColumn = e.key;
      this.sortType = e.order;
      if (e.order == "normal") {
        this.sortType = "";
      }
      this.getSegmentSpaceSumAlertList();
    },
    //连续搜索防抖处理
    handleSearch: util.debounce(function() {
      this.searchForm.pageNumber = 1;
      this.searchForm.pageSize = 10;
      this.getSegmentSpaceSumAlertList();
    }, 500),
    //连续点击刷新防抖处理
    refresh: util.debounce(function() {
      this.init();
    }, 500),
    //连续点击重置防抖处理
    handleReset: util.debounce(function () {
      this.searchForm.name = "";
      this.searchForm.pageNumber = 1;
      this.searchForm.pageSize = 10;

      // 重新加载数据
      this.getSegmentSpaceSumAlertList();
    }, 500),
    getSegmentSpaceSumAlertList() {
      this.loading = true;
      let params = {
        pageNumber: this.pageNumber,
        pageSize: this.pageSize,
        sort: this.sortColumn,
        order: this.sort,
        segmentName: this.searchForm.segmentName.trim()
      };

      getSegmentSpaceSumAlertList(params).then(res => {
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
    }
  },
  mounted() {
    this.init();
  }
};
</script>
