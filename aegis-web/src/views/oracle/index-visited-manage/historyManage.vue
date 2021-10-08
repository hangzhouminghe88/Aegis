<style lang="less" scoped>
  @import "../../../styles/table-common.less";
  @import "./indexVisitedManage.less";
</style>
<template>
  <div>
    <Card>
      <div slot="title">
        <a @click="close" class="back-title">
          <Icon type="ios-arrow-back" style="margin: 0 0 2px 0"/>
          返回
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
              >搜索
              </Button>
              <Button @click="handleReset" class="margin-right-10">重置</Button>
              <Button @click="handleSearch" icon="md-refresh" class="margin-right-10">刷新</Button>
              <setting-table-column :columns="columns" @setting-change="handleSettingChange"></setting-table-column>
            </slot>
          </a-form-item>
        </div>
      </div>
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
    getTableMonIndsList
  } from "@/api/index";
  import util from "@/libs/util.js";
  import AFromItem from '@views/my-components/form/FormItem';
  import SettingTableColumn from "@views/my-components/settingTableColumn/SettingTableColumn";

  export default {
    name: "history-index-visited-manage",
    props: {
      service: String,
      tableName: String
    },
    components: {
      "a-form-item": AFromItem,
      'setting-table-column': SettingTableColumn
    },
    computed: {
      columnsIsSetting() {
        return this.columns ? this.columns.filter(item => item.isSetting) : []
      },
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
          name: ''
        },
        columns: [
          {
            title: "创建者",
            key: "owner",
            sortable: false,
            isSetting: true
          },
          {
            title: "表名",
            key: "tableName",
            sortable: false,
            width: 200,
            isSetting: true
          },
          {
            title: "存储(MB)",
            key: "totalSize",
            sortable: false,
            isSetting: true
          },
          {
            title: "SQL总数",
            key: "sqlTotal",
            sortable: false,
            isSetting: true
          },
          {
            title: "索引数",
            key: "indTotal",
            sortable: false,
            isSetting: true
          },
          {
            title: "冗余数",
            key: "reduTotal",
            sortable: false,
            isSetting: false
          },
          {
            title: "使用数",
            key: "indUsed",
            sortable: false,
            isSetting: false
          },
          {
            title: "未用数",
            key: "indUnused",
            sortable: false,
            isSetting: false
          },
          {
            title: "最大列",
            key: "colMax",
            sortable: false,
            isSetting: false
          },
          {
            title: "平均列",
            key: "colAvg",
            sortable: false,
            isSetting: true
          },
          {
            title: "更新时间",
            key: "createTime",
            sortable: false,
            isSetting: true
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
        this.getTableMonIndsList();
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
      getTableMonIndsList() {
        this.loading = true;
        let params = {
          pageNumber: this.pageNumber,
          pageSize: this.pageSize,
          sort: this.sortColumn,
          order: this.sortType,
          service: this.service,
          tableName: this.tableName,
          name: this.searchForm.name.trim()
        };

        getTableMonIndsList(params).then(res => {
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
        _this.getTableMonIndsList();
      },
      handleReset() {
        const _this = this;
        this.searchForm.name = "";
        _this.getTableMonIndsList();
      },
      handleSettingChange(columns) {
        this.columns.map(item => {
          if (item.title === columns.title) {
            item.isSetting = !columns.isSetting;
          }
        })
      }
    },
    mounted() {
      this.init();
    }
  };
</script>
