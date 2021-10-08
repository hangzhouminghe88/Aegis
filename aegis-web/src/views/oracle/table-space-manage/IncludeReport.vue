<template>
  <div>
    <template v-if="currView==='index'">
      <Card>
        <header class="margin-bottom-10">
          <a class="back-title" @click="$emit('close')">
            <Icon type="ios-arrow-back"></Icon>
            <span>返回</span>
          </a>
          <form class="detail-search">
            <i-form-item label="分区名"
                         v-model="searchForm.name"
                         placeholder="请输入分区名"></i-form-item>
            <i-form-item type="slot">
              <slot>
                <Button type="primary"
                        icon="ios-search"
                        @click="handleSearch"
                        class="margin-right-10">搜索
                </Button>
                <Button class="margin-right-10"
                        @click="handleReset">重置
                </Button>
                <Button icon="ios-refresh"
                        @click="handleRefresh">刷新
                </Button>
              </slot>
            </i-form-item>
          </form>
        </header>
        <section>
          <Table :columns="columns"
                 :loading="loading"
                 border
                 :data="data"></Table>
          <Row type="flex" justify="end" class="margin-top-10">
            <Page :total="total"
                  :current="pageIndex"
                  :page-size-opts="[10,20,50,100]"
                  size="small"
                  show-elevator
                  show-sizer
                  show-total
                  @on-change="changeCurrent"
                  @on-change-size="changePageSize"
                  :page-size="pageSize"></Page>
          </Row>
        </section>
      </Card>
    </template>
    <template v-if="currView === 'history'">
      <segment-space-history :param="segParam" @close="currView = 'index'"></segment-space-history>
    </template>
  </div>
</template>

<script>
  import FormItem from "@views/my-components/form/FormItem";
  import SegmentSpaceHistory from '@views/oracle/segment-space-manage/historyManage';
  import {getIncludeList} from '@api'

  export default {
    name: "IncludeReport",
    components: {
      'i-form-item': FormItem,
      'segment-space-history': SegmentSpaceHistory
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
      return {
        currView: 'index',
        segParam: {},
        //表格列
        columns: [
          {
            title: '更新时间',
            key: 'createTime'
          },
          {
            title: '创建者',
            key: 'owner'
          },
          {
            title: '段名',
            key: 'segmentName'
          },
          {
            title: '分区名',
            key: 'partitionName'
          },
          {
            title: '段类型',
            key: 'segmentType'
          },
          {
            title: '使用(MB)',
            key: 'usedSpace'
          },
          {
            title: '增量(MB)',
            key: 'diffSpace'
          },
          {
            title: '最后DDL时间',
            key: 'lastDdlTime'
          },
          {
            title: '历史数据',
            render: (h, params) => {
              return h('Button', {
                props: {
                  type: "primary",
                  size: "small",
                  icon: 'ios-eye-outline'
                },
                on: {
                  click: () => {
                    this.segParam = params.row;
                    this.currView = 'history';
                  }
                }
              }, '历史数据')
            }
          }
        ],
        //搜索
        searchForm: {
          name: ''
        },
        //总条数
        total: 0,
        //每页展示条数
        pageSize: 10,
        //当前页码
        pageIndex: 1,
        //是否展示loading
        loading: false,
        //表格数据
        data: []
      }
    },
    mounted() {
      const _this = this;
      _this.getIncludeList();
    },
    methods: {
      /**
       * @param pageIndex点击的页码
       * */
      changeCurrent(pageIndex) {
        const _this = this;
        _this.pageIndex = pageIndex;
        _this.getIncludeList();
      },
      /**
       * @param size当前更换的每页展示条数
       * */
      changePageSize(size) {
        const _this = this;
        _this.pageSize = size;
        _this.pageIndex = 1;
        _this.getIncludeList();
      },
      /**
       * 请求
       * */
      getIncludeList() {
        const _this = this,
          params = {
            pageNumber: _this.pageIndex,
            pageSize: _this.pageSize,
            partitionName: _this.searchForm.name.trim(),
            service: _this.param.service,
            tableName: _this.param.name.trim()
          };
        _this.loading = true;
        getIncludeList(params).then(res => {
          if (res.success) {
            _this.total = res.result.total;
            _this.data = res.result.records;
          }
          _this.loading = false;
        }, () => {
          _this.loading = false;
        })
      },
      /**
       * 搜索
       * */
      handleSearch() {
        const _this = this;
        _this.pageIndex = 1;
        _this.getIncludeList();
      },
      /**
       * 重置
       * */
      handleReset() {
        const _this = this;
        _this.searchForm.name = '';
        _this.pageIndex = 1;
        _this.getIncludeList();
      },
      /***
       * 刷新
       * */
      handleRefresh() {
        const _this = this;
        _this.getIncludeList();
      }
    }
  }
</script>

<style scoped>

</style>
