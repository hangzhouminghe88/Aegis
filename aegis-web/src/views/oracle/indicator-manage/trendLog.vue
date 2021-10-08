<template>
  <Card>
    <template v-if="currview === 'alert'">
      <a @click="$emit('close')" class="back-title" style="verical-align: top;">
        <Icon type="ios-arrow-back" style="margin: 0 0 2px 0" />返回
      </a>
      <form class="detail-search">
        <a-form-item v-model="searchForm.name" label="表名称" placeholder="请输入表名称"></a-form-item>
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
      </form>
      <template>
        <div class="page-table">
          <Table :columns="columns" :data="data" border ref="table"></Table>
        </div>
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
      </template>
    </template>
		<template v-if="currview === 'history'">
       <alert-history :param="param" @close="currview = 'alert'"></alert-history>
		</template>
  </Card>
</template>

<script>
import { getCurvAlertList } from "@/api/index";
import AFromItem from "@views/my-components/form/FormItem";
import AlertHistory from  '@views/oracle/indicator-manage/AlertHistory';
export default {
  name: "TrendLog",
  props: {
    param: String
  },
  components: {
		"a-form-item": AFromItem,
		AlertHistory
  },
  data() {
		const _this = this;
    return {
      searchForm: {
        name: ""
      },
      data: [],
      pageNumber: 1,
      pageSize: 10,
			total: 0,
			currview:'alert',
      columns: [
        {
          title: "gmt_create",
          key: "gmt_create"
        },
        {
          title: "service",
          key: "service"
        },
        {
          title: "name",
          key: "name"
        },
        {
          title: "cnt",
          key: "cnt"
        },
        {
          title: "first_time",
          key: "begin_time"
        },
        {
          title: "last_time",
          key: "last_time"
        },
        {
          title: "操作",
          render: (h, params) => {
            return h(
              "Button",
              {
                props: {
                  type: "primary"
								},
								on: {
									click: () => {
						        _this.currview = 'history';
									}
								}
              },
              "查看历史"
            );
          }
        }
      ]
    };
  },
  mounted() {
    const _this = this;
    _this.getCurvAlertList();
  },
  methods: {
    handleSearch() {
      const _this = this;
      _this.pageNumber = 1;
      _this.getCurvAlertList();
    },
    handleReset() {
      const _this = this;
      _this.pageNumber = 1;
      _this.searchForm.name = "";
      _this.getCurvAlertList();
    },
    getCurvAlertList() {
      const _this = this,
        params = {
          pageNumber: _this.pageNumber,
          pageSize: _this.pageSize,
          name: _this.searchForm.name.trim(),
          service: _this.param
        };
      getCurvAlertList(params).then(res => {
        if (res.success === true) {
          _this.data = res.result.records;
          _this.total = res.result.total;
        }
      });
    },
    //分页
    changePage(v) {
      this.pageNumber = v;
      this.getCurvAlertList();
    },
    //改变页数
    changePageSize(v) {
      this.pageSize = v;
      this.getCurvAlertList();
    }
  }
};
</script>

<style lang="less" scoped>
@import url("~@views/oracle/indicator-manage/indicator-manage.less");
</style>
