<style lang="less" scoped>
  @import "../../../styles/table-common.less";
  @import "./awrManage.less";
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
          <a-form-item v-model="searchForm.name" label="sql_id" placeholder="请输入sql_id"></a-form-item>
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
    getSqlStatTopSqlList
  } from "@/api/index";
  import util from "@/libs/util.js";
  import {formatSize} from '@/libs/date';
  import AFromItem from '@views/my-components/form/FormItem';
  import SettingTableColumn from "@views/my-components/settingTableColumn/SettingTableColumn";

  export default {
    name: "history-sqlbind-manage",
    props: {
      param: {
        type: Object,
        default: () => {
          return {}
        }
      }
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
        sortColumn: "snap_time",
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
            title: "sql_id",
            key: "sqlId",
            sortable: false,
            isSetting: true
          },
          {
            title: "sql_fulltext",
            key: "sqlFulltext",
            sortable: false,
            tooltip: true,
            ellipsis: true,
            isSetting: true
          },
          {
            title: "fsc",
            key: "sqlCnt",
            sortable: true,
            isSetting: true
          },
          {
            title: "new",
            key: "isnew",
            sortable: true,
            isSetting: true
          },
          {
            title: "exec",
            key: "execDelta",
            sortable: true,
            isSetting: true,
            render: (h, param) => {
              return h("span", formatSize(param.row.execDelta))
            }
          },
          {
            title: 'ex%',
            key: 'execPer',
            sortable: true,
            isSetting: true
          },
          {
            title: 'buf',
            key: 'buffDelta',
            sortable: true,
            isSetting: false,
            render: (h, param) => {
              return h("span", formatSize(param.row.buffDelta))
            }
          },
          {
            title: 'bu%',
            key: 'buffPer',
            isSetting: false,
            sortable: true
          },
          {
            title: 'dsk',
            key: 'diskDelta',
            sortable: true,
            isSetting: false,
            render: (h, param) => {
              return h("span", formatSize(param.row.diskDelta))
            }
          },
          {
            title: 'di%',
            key: 'diskPer',
            isSetting: document.documentElement.clientWidth >= 1300 ? true : false,
            sortable: true
          },
          {
            title: 'ela',
            key: 'elap_1d',
            sortable: true,
            isSetting: document.documentElement.clientWidth >= 1300 ? true : false,
            render: (h, param) => {
              return h("span", formatSize(param.row.elap_1d))
            }
          },
          {
            title: 'pc%',
            key: 'parsePer',
            isSetting: document.documentElement.clientWidth >= 1300 ? true : false,
            sortable: true
          },
          {
            title: 'so%',
            key: 'sortPer',
            sortable: true,
            isSetting: document.documentElement.clientWidth >= 1300 ? true : false,
          },
          {
            title: 'row',
            key: 'rowsDelta',
            sortable: true,
            isSetting: true,
            render: (h, param) => {
              return h("span", formatSize(param.row.rowsDelta))
            }
          },
          {
            title: 'egf',
            key: 'fetchDelta',
            sortable: true,
            isSetting: document.documentElement.clientWidth >= 1300 ? true : false,
          },
          {
            title: 'bgf',
            key: 'buffGfact',
            sortable: true,
            isSetting: true,
            render: (h, param) => {
              return h("span", formatSize(param.row.buffGfact))
            }
          },
          {
            title: 'app',
            key: 'appName',
            isSetting: true,
            sortable: true
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
        this.getSqlStatTopSqlList();
      },
      changePage(v) {
        this.pageNumber = v;
        this.getSqlStatTopSqlList();
      },
      changePageSize(v) {
        this.pageSize = v;
        this.getSqlStatTopSqlList();
      },
      changeSort(e) {
        this.sortColumn = e.key;
        this.sortType = e.order;
        if (e.order == "normal") {
          this.sortType = "";
        }
        this.getSqlStatTopSqlList();
      },
      getSqlStatTopSqlList() {
        debugger;
        this.loading = true;
        let params = {
          pageNumber: this.pageNumber,
          pageSize: this.pageSize,
          sort: this.sortColumn,
          order: this.sortType,
          service: this.param.service,
          snapId: this.param.endSnap,
          sqlId: this.searchForm.name.trim()
        };

        getSqlStatTopSqlList(params).then(res => {
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
        _this.getSqlStatTopSqlList();
      },
      handleReset() {
        const _this = this;
        this.searchForm.name = "";
        _this.getSqlStatTopSqlList();
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
