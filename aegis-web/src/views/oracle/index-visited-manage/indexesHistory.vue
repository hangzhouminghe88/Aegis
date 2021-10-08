<template>
  <div>
    <header>
      <a class="back-title" @click="close">
        <Icon type="ios-arrow-back"></Icon>
        <span>返回</span>
      </a>
      <div class="detail-search">
        <a-form-item type="slot">
          <slot>
            <Button @click="handleSearch" icon="md-refresh" class="margin-right-10">刷新</Button>
            <setting-table-column :columns="columns" @setting-change="handleSettingChange"></setting-table-column>
          </slot>
        </a-form-item>
      </div>
    </header>
    <section>
      <Table :columns="columnsIsSetting"
             :data="data"
             :loading="loading"
             border
             class="margin-bottom-10 margin-top-20"></Table>
      <Row type="flex" justify="end" class="page">
        <Page show-elevator
              show-sizer
              :total="total"
              :page-size="pageSize"
              :current="pageIndex"
              size="small"
              :page-size-opts="[10,20,50,100]"
              @on-change="changePage"
              @on-page-size-change="changePageSize"
              show-total></Page>
      </Row>
    </section>
  </div>
</template>

<script>
  import AFromItem from '@views/my-components/form/FormItem';
  import SettingTableColumn from "@views/my-components/settingTableColumn/SettingTableColumn";
  import { getIndexeMonHistory } from '@api/index';
  import IndexesHistory from '@views/oracle/index-visited-manage/indexesHistory';
  export default {
    name: "indexesHistory",
    components: {
      "a-form-item": AFromItem,
      'setting-table-column': SettingTableColumn,
      'indexes-history': IndexesHistory
    },
    computed: {
      columnsIsSetting() {
        return this.columns ? this.columns.filter(item => item.isSetting) : []
      },
    },
    props: {
      param: {
        type: Object,
        default: () => {
          return {}
        }
      }
    },
    data() {
      const _this = this;
      return {
        //展示页面
        currview: 'index',
        //是否加载中
        loading: false,
        //表格列
        columns: [
          {
            title: '创建者',
            width: 100,
            isSetting: true,
            key: 'owner'
          },
          {
            title: '索引名',
            width: 200,
            isSetting: true,
            key: 'indexName'
          },
          {
            title: '存储/MB',
            width: 100,
            isSetting: true,
            key: 'segSize'
          },
          {
            title: 'SQL数',
            width: 100,
            isSetting: true,
            key: 'sqlTotal'
          },
          {
            title: '使用否',
            width: 100,
            isSetting: true,
            key: 'usedStats'
          },
          {
            title: '冗余否',
            width: 100,
            isSetting: true,
            key: 'reduStats'
          },
          {
            title: '类型',
            width: 150,
            isSetting: document.documentElement.clientWidth >= 1300 ? true : false,
            key: 'indexType'
          },
          {
            title: '唯一',
            width: 100,
            isSetting: document.documentElement.clientWidth >= 1300 ? true : false,
            key: 'uniqueness'
          },
          {
            title: '创建时间',
            width: 200,
            isSetting: true,
            key: 'gmtModified'
          },
          {
            title: '包含列',
            width: 200,
            isSetting: document.documentElement.clientWidth >= 1300 ? true : false,
            key: 'includeColumn'
          }
        ],
        //表格数据
        data: [],
        //每页展示数
        pageSize: 10,
        //页码
        pageIndex: 1,
        //总条数
        total: 0,
        //搜索
        searchForm: {
          name: ''
        }
      }
    },
    mounted() {
      this.getIndexeMonHistory();
    },
    methods: {
      handleSettingChange(columns) {
        this.columns.map(item => {
          if (item.title === columns.title) {
            item.isSetting = !columns.isSetting;
          }
        })
      },
      handleSearch() {
        const _this = this;
        _this.getIndexeMonHistory();
      },
      handleReset() {
        const _this = this;
        _this.searchForm.name = '';
        _this.pageIndex = 1;
        _this.getIndexeMonHistory();
      },
      getIndexeMonHistory() {
        const _this = this,
          params = {
            service: _this.param.service,
            indexName: _this.param.indexName,
            search: _this.searchForm.name.trim(),
            pageNumber: _this.pageIndex,
            pageSize: _this.pageSize,
          };
        _this.loading = true;
        getIndexeMonHistory(params).then(res=> {
          if(res.success) {
            _this.total = res.result.total;
            _this.data = res.result.records
          }
          _this.loading = false;
        }, () => {
          _this.loading = false;
        })
      },
      changePage(v) {
        this.pageIndex = v;
        this.getIndexeMonHistory();
      },
      changePageSize(v) {
        this.pageSize = v;
        this.getIndexeMonHistory();
      },
      close() {
        const _this = this;
        _this.$emit('close')
      }
    }
  }
</script>

<style scoped>

</style>
