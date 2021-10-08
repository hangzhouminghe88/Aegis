<style lang="less" scoped>
@import "../../../styles/table-common.less";
@import "./segmentSpaceManage.less";
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
  getSegmentSpaceSumList
} from "@/api/index";
import util from "@/libs/util.js";
import AFromItem from '@views/my-components/form/FormItem';
export default {
  name: "history-segmentspace-manage",
  props: {
      service: String,
      segmentName: String
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
              title: "实例名",
              key: "service",
              sortable: false
          },
          {
              title: "段名",
              key: "segmentName",
              sortable: false
          },
          {
              title: "分区名",
              key: "partitionName",
              sortable: false
          },
          {
              title: "使用(MB)",
              key: "usedSpace",
              sortable: false
          },
          {
              title: "增量(MB)",
              key: "diffSpace",
              sortable: false
          },
          {
              title: "最后DDL时间",
              key: "lastDdlTime",
              sortable: false
          },
          {
              title: "更新日期",
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
      this.getSegmentSpaceSumList();
    },
    changePage(v) {
      this.pageNumber = v;
      this.getSegmentSpaceSumList();
      this.clearSelectAll();
    },
    changePageSize(v) {
      this.pageSize = v;
      this.getSegmentSpaceSumList();
    },
    changeSort(e) {
      this.sortColumn = e.key;
      this.sortType = e.order;
      if (e.order == "normal") {
        this.sortType = "";
      }
      this.getSegmentSpaceSumList();
    },
    getSegmentSpaceSumList() {
      this.loading = true;
      let params = {
        pageNumber: this.pageNumber,
        pageSize: this.pageSize,
        sort: this.sortColumn,
        order: this.sortType,
        segmentName:this.segmentName,
        service:this.service
      };

      getSegmentSpaceSumList(params).then(res => {
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
    handleSearch() {
      const _this = this;
      _this.getSegmentSpaceSumList();
    }
  },
  mounted() {
    this.init();
  }
};
</script>
