<style lang="less" scoped>
@import "../../../styles/table-common.less";
@import "~@views/oracle/dict-manage/dicManage.less";
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
  getDictIndexesInfoList
} from "@/api/index";
import util from "@/libs/util.js";
import AFromItem from '@views/my-components/form/FormItem';
export default {
  name: "dictColumnsInfo",
  props: {
      param: {
				type: Object,
				default: () => {
					return {}
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
      sortColumn: "index_name",
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
              title: "创建者",
              key: "owner",
              sortable: false
          },
          {
              title: "索引名",
              key: "indexName",
              sortable: false
          },
          {
              title: "存储(MB)",
              key: "segSize",
              sortable: false
          },
          {
              title: "SQL数",
              key: "sqlTotal",
              sortable: false
          },
          {
              title: "类型",
              key: "indexType",
              sortable: false
					},
					 {
              title: "是否用",
              key: "usedStats",
              sortable: false
					},
					 {
              title: "冗余否",
              key: "reduStats",
              sortable: false
          },
          {
              title: "唯一否",
              key: "uniqueness",
              sortable: false
          },
          {
              title: "创建时间",
              key: "gmtCreate",
              sortable: false
          },
          {
              title: "包含列",
              key: "includeColumn",
              sortable: false
          }
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
      this.getDictIndexesInfoList();
    },
    changePage(v) {
      this.pageNumber = v;
      this.getDictIndexesInfoList();
      this.clearSelectAll();
    },
    changePageSize(v) {
      this.pageSize = v;
      this.getDictIndexesInfoList();
    },
    changeSort(e) {
      this.sortColumn = e.key;
      this.sortType = e.order;
      if (e.order == "normal") {
        this.sortType = "";
      }
      this.getDictIndexesInfoList();
    },
    getDictIndexesInfoList() {
			this.loading = true;
      let params = {
        pageNumber: this.pageNumber,
        pageSize: this.pageSize,
        sort: this.sortColumn,
        order: this.sortType,
				service:this.param.service,
				name: this.param.tableName,
        owner: this.searchForm.name.trim()
      };

      getDictIndexesInfoList(params).then(res => {
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
      _this.getDictIndexesInfoList();
    },
    handleReset() {
      const _this = this;
      this.searchForm.name = "";
      _this.getDictIndexesInfoList();
    }
  },
  mounted() {
    this.init();
  }
};
</script>
