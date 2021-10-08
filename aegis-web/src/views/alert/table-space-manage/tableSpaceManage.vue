<style lang="less" scoped>
  @import "../../../styles/table-common.less";
  @import "./tableSpaceManage.less";
</style>
<template>
  <div class="search">
    <historyManage v-if="currView=='his'" :name="tablespace" :service="service" @close="closeHistory"></historyManage>
    <Card v-show="currView=='index'">
      <div class="form">
        <a-form-item v-model="searchForm.name" label="表空间名" placeholder="请输入表空间名"></a-form-item>
        <a-form-item type="slot">
          <slot>
            <Button @click="handleSearch" type="primary" icon="ios-search" class="margin-right-10">搜索</Button>
            <Button @click="handleReset">重置</Button>
          </slot>
        </a-form-item>
      </div>

      <Row class="operation" type="flex" justify="space-between">
        <Button @click="refresh" icon="md-refresh">刷新</Button>
        <setting-table-column :columns="columns" @setting-change="handleSettingChange"></setting-table-column>
      </Row>
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
    getTableSpaceSumList
  } from "@/api/index";
  import historyManage from "./historyManage.vue";
  import util from "@/libs/util.js";
  import AFromItem from '@views/my-components/form/FormItem';
  import SettingTableColumn from "@views/my-components/settingTableColumn/SettingTableColumn";
  import {formatDate} from "@/libs/date";

  export default {
    name: "alert-tablespace-manage",
    components: {
      historyManage,
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
        searchForm: {name: ''},
        form: {},
        formValidate: {},
        tablespace: '',
        service: '',
        submitLoading: false,
        selectList: [],
        selectCount: 0,
        gmtCreate: '',
        columns: [
          {
            title: "实例名",
            key: "service",
            sortable: false,
            isSetting: true
          },
          {
            title: "表空间",
            key: "name",
            sortable: false,
            isSetting: true
          },
          {
            title: "存储总数(MB)",
            key: "totalSpace",
            sortable: false,
            isSetting: document.documentElement.clientWidth > 1500 ? true : false
          },
          {
            title: "使用(MB)",
            key: "usedSpace",
            sortable: false,
            isSetting: true
          },
          {
            title: "空间(MB)",
            key: "freeSpace",
            sortable: false,
            isSetting: true
          },
          {
            title: "增量(MB)",
            key: "diffSpace",
            sortable: false,
            isSetting: true
          },
          {
            title: "均值(MB)",
            key: "avgSpace",
            sortable: false,
            isSetting: document.documentElement.clientWidth > 1300 ? true : false
          },
          {
            title: "使用率(%)",
            key: "usedRate",
            sortable: false,
            isSetting: document.documentElement.clientWidth > 1300 ? true : false
          },
          {
            title: "可用天数",
            key: "usedDays",
            sortable: false,
            isSetting: document.documentElement.clientWidth > 1300 ? true : false
          },
          {
            title: "更新日期",
            key: "gmtCreate",
            sortable: false,
            isSetting: true
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
                      icon: 'ios-eye-outline'
                    },
                    on: {
                      click: () => {

                        this.tablespace = params.row.name;
                        this.service = params.row.service;
                        this.currView = 'his'
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
      init() {
        this.getTableSpaceSumList();
      },
      changePage(v) {
        this.pageNumber = v;
        this.getTableSpaceSumList();
        this.clearSelectAll();
      },
      changePageSize(v) {
        this.pageSize = v;
        this.getTableSpaceSumList();
      },
      changeSort(e) {
        this.sortColumn = e.key;
        this.sortType = e.order;
        if (e.order == "normal") {
          this.sortType = "";
        }
        this.getTableSpaceSumList();
      },
      handleSearch: util.debounce(function() {
        this.searchForm.pageNumber = 1;
        this.searchForm.pageSize = 10;
        this.getTableSpaceSumList();
      }, 500),
      handleReset: util.debounce(function() {
        this.searchForm.name = "";
        this.searchForm.pageNumber = 1;
        this.searchForm.pageSize = 10;

        // 重新加载数据
        this.getTableSpaceSumList();
      }, 500),
      refresh: util.debounce(function() {
        this.init();
      }, 500),
      getTableSpaceSumList() {
        this.loading = true;
        let params = {
          pageNumber: this.pageNumber,
          pageSize: this.pageSize,
          sort: this.sortColumn,
          order: this.sort,
          name: this.searchForm.name.trim(),
          gmtCreate: this.gmtCreate
        };

        getTableSpaceSumList(params).then(res => {
          this.loading = false;
          if (res.success) {
            this.data = res.result.records;
            this.total = res.result.total;
          }
        });
      },
      closeHistory(gmtCreate) {
        this.currView = 'index';
        this.gmtCreate = gmtCreate === '' ? formatDate(new Date()) : gmtCreate;
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
