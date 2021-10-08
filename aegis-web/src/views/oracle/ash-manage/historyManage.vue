<style lang="less" scoped>
  @import "../../../styles/table-common.less";
  @import "./ashManage.less";
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
            :columns="columnsSettings"
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
    sysAshWaitRelationList,
  } from "@/api/index";
  import util from "@/libs/util.js";
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
      columnsSettings() {
        return this.columns ? this.columns.filter(item => item.isSetting) : []
      }
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
            title: "sql_id",
            key: "sqlId" || "sql_id",
            sortable: false,
            isSetting: true,
            width: 150
          },
          {
            title: "sql_fulltext",
            key: "sqlText" || "sql_text",
            sortable: false,
            isSetting: true,
            ellipsis: true,
            tooltip: true,
            width: 200
          },
          {
            title: "fsc",
            key: "scan_type" || "scanType",
            sortable: false,
            isSetting: true,
            width: 150
          },
          {
            title: "exe",
            key: "executions",
            sortable: false,
            isSetting: true,
            width: 150
          },
          {
            title: "ex%",
            key: "execPer",
            sortable: false,
            isSetting: true,
            width: 150
          },
          {
            title: 'buf',
            key: 'bufferGets' || 'buffer_gets',
            isSetting: false,
            width: 150
          },
          {
            title: 'bu%',
            key: 'buffPer' || 'buff_per',
            isSetting: false,
            width: 150
          },
          {
            title: 'dsk',
            key: 'diskReads' || 'disk_reads',
            isSetting: false,
            width: 150
          },
          {
            title: 'di%',
            key: 'diskPer' || 'disk_per',
            isSetting: false,
            width: 150
          },
          {
            title: 'ela',
            key: 'elapsedTime' || 'elapsed_time',
            isSetting: false,
            width: 150
          },
          {
            title: 'el%',
            key: 'elapPer' || 'elap_per',
            isSetting: false,
            width: 150
          },
          {
            title: 'cp%',
            key: 'cputPer' || 'cput_per',
            isSetting: false,
            width: 150
          },
          {
            title: 'pc%',
            key: 'parsePer' || 'parse_per',
            isSetting: false,
            width: 150
          },
          {
            title: 'so%',
            key: 'sortPer' || 'sort_per',
            isSetting: true,
            width: 150
          },
          {
            title: 'row',
            key: 'rowsProcessed',
            isSetting: true,
            width: 150
          },
          {
            title: 'app',
            key: 'appName' || 'app_name',
            isSetting: true,
            width: 150
          },
          {
            title: 'mod',
            key: 'module',
            isSetting: true,
            width: 150
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
      //初始化请求
      init() {
        this.sysAshWaitRelationList();
      },
      //改变当前页
      changePage(v) {
        this.pageNumber = v;
        this.sysAshWaitRelationList();
        this.clearSelectAll();
      },
      //改变页码
      changePageSize(v) {
        this.pageSize = v;
        this.sysAshWaitRelationList();
      },
      //排序
      changeSort(e) {
        this.sortColumn = e.key;
        this.sortType = e.order;
        if (e.order == "normal") {
          this.sortType = "";
        }
        this.getSqlBindList();
      },
      sysAshWaitRelationList() {
        this.loading = true;
        let params = {
          pageNumber: this.pageNumber,
          pageSize: this.pageSize,
          sort: this.sortColumn,
          order: this.sortType,
          service: this.param.service,
          objName: this.param.objectName,
          sqlId: this.searchForm.name.trim()
        };

        sysAshWaitRelationList(params).then(res => {
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
        _this.sysAshWaitRelationList();
      },
      handleReset() {
        const _this = this;
        this.searchForm.name = "";
        _this.sysAshWaitRelationList();
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
