<template>
	 <div>
      <a @click="$emit('close')" class="back-title" style="line-height: 30px;">
        <Icon type="ios-arrow-back" style="margin: 0 0 2px 0" />返回
      </a>
      <div class="detail-search">
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
      </div>
			<template>
				<div class="page-table">
          <Table :columns="columns" :data="data" border ref="table" @on-sort-change="changeSort"></Table>
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
	 </div>
</template>

<script>
import { getAlertHisList } from '@/api/index';
import AFromItem from "@views/my-components/form/FormItem";
export default {
	name: 'AlertHistory',
	components: {
		"a-form-item": AFromItem
	},
	props: {
		param: {
			type: String
		}
	},
	data() {
		return {
			searchForm: {
				name: ''
			},
			pageNumber: 1,
			pageSize: 10,
			total: 0,
			sortColumn: "gmt_create",
			sortType: 'desc',
			columns: [
				{
					title:'gmt_create',
					key: 'gmtCreate',
					sortable: true
				},
				{
					title: 'name',
					key: 'name',
					sortable: true
				},
				{
					title: 'sname',
					key: 'sname',
					sortable: true
				},
				{
					title: 'currval',
					key: 'currval',
					sortable: true
				},
				{
					title: 'lastval',
					key: 'lastval',
					sortable: true
				},
				{
					title: 'ratio',
					key: 'ratio',
					sortable: true
				},
				{
					title: 'score',
					key: 'score',
					sortable: true
				},
				{
					title: 'super',
					key: 'superby',
					sortable: true
				},
				{
					title: 'times',
					key: 'times',
					sortable: true
				}
			],
			data: []
		}
	},
	mounted() {
		const _this = this;
		_this.getAlertHisList();
	},
	methods: {
		//搜索
		handleSearch() {
			const _this = this;
			_this.pageNumber = 1;
			_this.getAlertHisList();
		},
		//重置
		handleReset() {
			const _this = this;
			_this.searchForm.name = '';
			_this.pageNumber = 1;
			_this.getAlertHisList();
		},
		//页面展示个数改变
		changePageSize(v) {
			const _this = this;
			_this.pageSize = v;
			_this.pageNumber = 1;
			_this.getAlertHisList();
		},
		 //页码改变
		changePage(v) {
			const _this = this;
			_this.pageNumber = v;
			_this.getAlertHisList();
		},
		getAlertHisList() {
			const _this = this,
			params = {
				pageNumber: _this.pageNumber,
				pageSize: _this.pageSize,
				name: _this.searchForm.name.trim(),
				service: _this.param,
				sort: _this.sortColumn,
				order: _this.sortType
			}
			getAlertHisList(params).then(res => {
				if(res.success === true) {
					_this.data = res.result.records;
					_this.total = res.result.total;
				}
			})
		},
		changeSort(e) {
      this.sortColumn = e.key.replace(/([A-Z])/g, '_$1').toLowerCase();
      this.sortType = e.order;
      if (e.order == "normal") {
        this.sortType = "";
      }
      this.getAlertHisList();
    },
	}
}
</script>

<style lang="less" scoped>
@import url("~@views/oracle/indicator-manage/indicator-manage.less");
</style>
