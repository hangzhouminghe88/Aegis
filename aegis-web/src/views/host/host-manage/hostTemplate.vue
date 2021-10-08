<template>
  <Card>
    <header slot="title">
      <span class="back-title" @click="$emit('close')">
        <Icon class="ivu-icon ivu-icon-ios-arrow-back"></Icon>
        <span>返回</span>
      </span>
      <form class="detail-search">
        <a-form-item v-model="searchForm.name" label="名称" placeholder="请输入名称"></a-form-item>
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
          </slot>
        </a-form-item>
      </form>
    </header>
    <section>
      <Row class="page-table">
        <Table :data="data"
               :columns="columns"
               border
               :loading="loading"></Table>
      </Row>
      <Row type="flex" justify="end" class="page" style="padding-top: 10px">
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
    </section>
  </Card>
</template>

<script>
  import { getHostTemplate } from '@api/index';
  import AFromItem from '@views/my-components/form/FormItem';
  export default {
    name: "hostTemplate",
    components: {
      "a-form-item": AFromItem
    },
    props: {
      param: {
        type: Object,
        default:() => {
          return {}
        }
      }
    },
    data() {
      return {
        data: [],
        pageNumber: 1,
        pageSize: 10,
        total: 0,
        loading: false,
        sortColumn: 'gmt_modified',
        sortType: 'asc',
        searchForm: {
          name: ''
        },
        columns: [
          {
            title: '主机名称',
            key: 'hostName'
          },
          {
            title: '名称',
            key: 'name'
          },
          {
            title: '简称',
            key: 'sname'
          },
          {
            title: '类型',
            key: 'type'
          },
          {
            title: '排序',
            key: 'sortBy'
          },
          {
            title: 'currval',
            key: 'currval'
          },
          {
            title: 'lastval',
            key: 'lastval'
          },
          {
            title: 'super',
            key: 'superBy'
          },
          {
            title: 'times',
            key: 'timeWaited'
          },
          {
            title: '创建时间',
            key: 'gmtModified'
          }
        ]
      }
    },
    mounted() {
      let _this = this;
      _this.getHostTemplateList();
    },
    methods: {
      //得到所有主机模板实例
      getHostTemplateList() {
        let params = {
          pageNumber: this.pageNumber,
          pageSize: this.pageSize,
          hostName: this.param.name,
          sortColumn: this.sortColumn,
          sortType: this.sortType,
          name: this.searchForm.name
        }
        this.total = 0; this.data = []; this.loading = true;
        getHostTemplate(params).then(res => {
          if(res.success ===  true) {
            this.data = res.result.records;
            this.total = res.result.total;
          }
          this.loading = false;
        }).catch(() => {
          this.loading = false;
        })
      },
      //分页
      changePage(page) {
        this.pageNumber = page;
        this.getHostTemplateList();
      },
      //改变页码pageSize
      changePageSize(size) {
        this.pageSize = size;
        this.pageNumber = 1;
        this.getHostTemplateList();
      },
      //搜索
      handleSearch() {
        const _this = this;
        _this.getHostTemplateList();
      },
      //重置
      handleReset() {
        const _this = this;
        //清空表单
        this.searchForm.name = "";
        _this.getHostTemplateList();
      },
    }
  }
</script>

<style scoped>

</style>
