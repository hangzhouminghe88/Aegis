<template>
  <Card>
    <template>
			<div class="toolbar">
      <div class="toolbar-close">
				<a  @click="close" class="back-title">
          <Icon type="ios-arrow-back" style="margin: 0 0 2px 0"/>返回
        </a>
			</div>
			<Form ref="searchForm" :model="searchForm" inline :label-width="100" class="toolbar-form">
        <Form-item label="metric_name" prop="name">
          <Input
                  type="text"
                  v-model="searchForm.name"
                  clearable
                  placeholder=""
                  style="width: 200px"
          />
        </Form-item>
        <Form-item style="margin-left:-35px;" class="br">
          <Button @click="handleSearch" type="primary" icon="ios-search">搜索</Button>
          <Button @click="handleReset">重置</Button>
        </Form-item>
      </Form>
			</div>
		</template>
		<template>
      <div class="table-content">
				<Table :data="data" :columns="columns" border :loading="loading"></Table>
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
	</Card>
</template>

<script>
import { getMetricReportList } from '@/api/index';
import { formatCount } from "@/libs/date"
export default {
	name: 'metricReport',
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
			searchForm: {
				name: ''
			},
			pageNumber: 1,
			pageSize: 10,
			loading: false,
			total: 0,
			data: [],
			columns: [
				{
					title: '快照时间',
					key: 'gmtCreate'
				},
				{
					title: '度量ID',
					key: 'metricId'
				},
				{
					title: '度量名称',
					key: 'metricName'
				},
				{
					title: '平均值',
					key: 'avg',
          render: (h, param) => {
					  return h("span", formatCount(param.row.avg));
          }
				},
				{
					title: '最大值',
					key: 'max',
          render: (h, param) => {
            return h("span", formatCount(param.row.max));
          }
				},
				{
					title: '标准差',
					key: 'sdval',
          render: (h, param) => {
            return h("span", formatCount(param.row.sdval));
          }
				},
				{
					title: '最近一周',
					key: 'last1w',
          render: (h, param) => {
            return h("span", formatCount(param.row.last1w));
          }
				},
				{
					title: '最近一月',
					key: 'last1m',
          render: (h, param) => {
            return h("span", formatCount(param.row.last1m));
          }
				},
				{
					title: '最近一季',
					key: 'last1q',
          render: (h, param) => {
            return h("span", formatCount(param.row.last1q));
          }
				},
			]
		}
	},
	mounted() {
		const _this = this;
		_this.init();
	},
	methods: {
		handleSearch() {
        this.pageNumber = 1;
        this.getMetricReportList();
		},
		handleReset(e) {
			this.pageNumber = 1;
			this.$refs.searchForm.resetFields();
      this.getMetricReportList();
		},
		close() {
			this.$emit('close', true)
		},
		init() {
			const _this = this;
			_this.getMetricReportList();
		},
		getMetricReportList() {
			const _this = this,
			params = {
				service: _this.param.service,
				snapId: _this.param.endSnap,
				pageNumber: _this.pageNumber,
				pageSize: _this.pageSize,
				metricName: _this.searchForm.name.trim()
			};
			_this.loading = true;
			getMetricReportList(params).then(res => {
			  _this.loading = false;
        if (res.success) {
          _this.data = res.result.records;
          _this.total = res.result.total;
        }
			}, () => {
			  _this.loading = false;
      })
		},
    //改变页码
    changePage(v) {
      this.pageNumber = v;
      this.getMetricReportList();
    },
    //改变每页展示数量
    changePageSize(v) {
      this.pageSize = v;
      this.pageNumber = 1;
      this.getMetricReportList();
    },
	}
}
</script>

<style lang="less" scoped>
  @import url("~@views/oracle/awr-manage/awrManage.less");
</style>
