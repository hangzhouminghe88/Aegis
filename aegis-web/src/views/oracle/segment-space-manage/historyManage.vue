<style lang="less" scoped>
@import "../../../styles/table-common.less";
@import "./segment-space-rpt.less";
</style>
<template>
  <div>
    <Card>
      <div slot="title">
        <a @click="close" class="back-title">
          <Icon type="ios-arrow-back" style="margin: 0 0 2px 0" />返回
        </a>
      </div>
      <Row>
        <div class="form">
          <a-form-item v-model="searchForm.name" label="段名" placeholder="请输入段名(表名|索引名)"></a-form-item>
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
import { getSegmentSpaceSumHistoryList } from "@/api/index";
import AFromItem from '@views/my-components/form/FormItem';
import util from "@/libs/util.js";
export default {
  name: "history-tablespace-manage",
  props: {
    param: {
      type: Object,
      default: () => {
        return {};
      }
    }
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
      modalVisible: false,
      submitLoading: false,
      selectList: [],
      selectCount: 0,
      searchForm: {
        name: ""
      },
      columns: [
        {
          title: "实例名",
          key: "service",
          sortable: false
        },
        {
          title: "更新时间",
          key: "createTime",
          sortable: false
        },
        {
          title: "创建者",
          key: "owner",
          sortable: false
        },
        {
          title: "段名",
          key: "segmentName",
          sortable: false
        },
        {
          title: "段类型",
          key: "segmentType",
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
          title: "增量(%)",
          key: "diffSpace",
          sortable: false
        },
        {
          title: "最后DDL时间",
          key: "lastDdlTime",
          sortable: false
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
    init() {
      this.getSegmentSpaceSumHistoryList();
    },
    changePage(v) {
      this.pageNumber = v;
      this.getSegmentSpaceSumHistoryList();
      this.clearSelectAll();
    },
    changePageSize(v) {
      this.pageSize = v;
      this.getSegmentSpaceSumHistoryList();
    },
    changeSort(e) {
      this.sortColumn = e.key;
      this.sortType = e.order;
      if (e.order == "normal") {
        this.sortType = "";
      }
      this.getSegmentSpaceSumHistoryList();
    },
    getSegmentSpaceSumHistoryList() {
      this.loading = true;
      let params = {
        pageNumber: this.pageNumber,
        pageSize: this.pageSize,
        sort: this.sortColumn,
        order: this.sortType,
        segmentName: this.param.segmentName,
        service: this.param.service
      };

      getSegmentSpaceSumHistoryList(params).then(res => {
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
    //搜索
    handleSearch() {
      this.pageNumber = 1;
      this.searchForm.pageSize = 10;
      this.getSegmentSpaceSumHistoryList();
    },
    handleReset() {
      this.searchForm.name = '';
      this.pageNumber = 1;
      this.searchForm.pageSize = 10;

      // 重新加载数据
      this.getSegmentSpaceSumHistoryList();
    }
  },
  mounted() {
    this.init();
  }
};
</script>