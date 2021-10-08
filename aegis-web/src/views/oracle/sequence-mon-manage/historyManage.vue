<style lang="less" scoped>
@import "../../../styles/table-common.less";
@import "./sequenceMonManage.less";
</style>
<template>
  <div>
    <Card>
      <div slot="title">
        <a @click="close" class="back-title">
          <Icon type="ios-arrow-back" style="margin: 0 0 2px 0"/>返回
        </a>
        <div class="detail-search">
          <a-form-item v-model="searchForm.name" label="创建者" placeholder="请输入创建者"></a-form-item>
          <a-form-item type="slot">
            <slot>
              <Button
                  @click="handleSearch"
                  type="primary"
                  icon="ios-search"
                  class="margin-right-10"
              >搜索</Button>
              <Button @click="handleReset"  class="margin-right-10">重置</Button>
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
import {
  getSequenceReportList
} from "@/api/index";
import util from "@/libs/util.js";
import AFromItem from '@views/my-components/form/FormItem';
import expandRow from "@views/oracle/sequence-mon-manage/ExpandRow";
export default {
  name: "history-sequence-mon-manage",
  props: {
      service: String,
      sequence: String,
      expandRow
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
      searchForm: {
        name: ''
      },
      columns: [
          {
            type: 'expand',
            title: "展开",
            width: 100,
            render: (h, params) => {
               return h(expandRow, {
                 props: {
                   row: params.row
                 }
               })
            }
          },
          {
              title: "所有者",
              key: "owner",
              sortable: false
          },
          {
              title: "序列名称",
              key: "sequence",
              sortable: false,
              width:200,
              tooltip: true
          },
          {
              title: "最小值",
              key: "minValue",
              sortable: false
          },
          {
              title: "递增数",
              key: "incrBy",
              sortable: false
          },
          {
              title: "最后序列号",
              key: "lastNumber",
              sortable: false
          },
          {
              title: "每天增量",
              key: "delta",
              sortable: false
          },
          {
              title: "缓存数",
              key: "cacheSize",
              sortable: false
          },
          {
              title: "更新时间",
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
      this.getSequenceReportList();
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
    getPercent(val,total){
        let availablePercent = Number(val) / Number(total);
        if (Number(val) <= 0) return 0;
        return parseInt((availablePercent.toFixed(2)) * 100)
    },
    getSequenceReportList() {
      this.loading = true;
      let params = {
        pageNumber: this.pageNumber,
        pageSize: this.pageSize,
        sort: this.sortColumn,
        order: this.sortType,
        service:this.service,
        sequence:this.sequence,
        name: this.searchForm.name.trim()
      };

      getSequenceReportList(params).then(res => {
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
      const _this = this;
      _this.getSequenceReportList();
    },
    handleReset() {
      const _this = this;
      this.searchForm.name = "";
      _this.getSequenceReportList();
    },
    /**
     * 点击行展开
    */
    handleExpand(row, index) {
      this.data.forEach((item, i) => {
        //这个循环是为了每次只能展开一个，其他自动收起，不需要可以去掉
        i !== index ? (this.data[i]._expanded = false) : "";
      });
      this.data[index]._expanded = !this.data[index]._expanded;
      this.data.sort();
    }
  },
  mounted() {
    this.init();
  }
};
</script>
