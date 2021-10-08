<template>
  <div class="trend-metric">
    <template v-if="currview === 'index'">
			<div class="trend-metric__title">
      <span class="title" style="font-size: 16px;margin-right: 10px;">趋势图预警</span>
    </div>
    <div class="trend-metric__search">
      <Select v-model="interval" style="width: 120px;margin-right: 10px;" @on-change="changeTime">
        <Option v-for="item in timeTab" :value="item.label" :key="item.label">{{ item.value }}</Option>
      </Select>
      <Button type="default" icon="md-refresh"  @click="refresh"></Button>
    </div>
		<v-charts :loading="loading" :chartData="{service: service, dataSet, chunkDataSet, pageIndex, isLink: true}" @linkToHistory="linkToHistory" @linkToDetail="linkToDetail"></v-charts>
    <div class="page-pagination">
      <pagination
        :total="total"
        v-show="total > 0"
        :currentPage="pageIndex"
        @size-change="handleSizeChange"
        :pageSize="pageSize"
        @change="handleCurrentChange"
        :sizes="[5, 10, 15, 20]"
      ></pagination>
    </div>
		</template>
		<template v-if="currview === 'history'">
			<history  :param="historyParam" @close="currview = 'index'"></history>
		</template>
		<template v-if="currview === 'detailByName'">
			<detail-metric-by-name  :param="detailByNameParam" @close="currview = 'index'"></detail-metric-by-name>
		</template>
  </div>
</template>

<script>
import pagination from '@/views/my-components/pagination/Pagination'
import Charts from "@/views/my-components/metric/charts";
import { getAllHostList, getTrendMetricList } from '@/api/index'
import history from '@/views/oracle/indicator-manage/history';
import detailMetricByName from '@/views/oracle/indicator-manage/detailMetricByName';
import { formatSize, formatDate } from "@/libs/date";
import Loading from  '@/views/my-components/loading/Loading';
import util from '@/libs/util/'
import _ from "lodash";
export default {
  name: "TrendMetricPage",
  components: {
		 pagination,
		 history,
		 "v-charts": Charts,
		 'detail-metric-by-name': detailMetricByName,
		 Loading
  },
  data() {
    return {
      currSelectQuotaTab: "none",
      name: "",
      searchStr: "",
      timerInterval: null,
      pageIndex: 1,
      pageSize: 5,
			total: 0,
			hostList: [],
			service: '',
			currview: 'index',
			historyParam: {},
			detailByNameParam: '',
			loading: false,
      interval: 60 * 60,
      timeTab: [
        {
          label: 5 * 60,
          value: '最近5分钟'
        },
        {
          label: 60 * 60,
          value: '最近一小时'
        },
        {
          label: 24 * 60 * 60,
          value: '最近一天'
        },
        // {
        //   label: 7 * 24 * 3600,
        //   value: '最近一周'
        // },
        // {
        //   label: 30* 24 * 60 * 60,
        //   value: '最近一月'
        // },
        // {
        //   label: 365 * 24 * 60 * 60,
        //   value: '最近一年'
        // }
      ],
			tabs: [],
			hostIds: [],
      quotaTabs: [
				{
					label: 'none',
					value: 'none'
				},
        {
          label: "core",
          value: "core"
        },
        {
          label: "host",
          value: "host"
        },
        {
          label: "trans",
          value: "trans"
        },
        {
          label: "network",
          value: "network"
        },
        {
          label: "alert",
          value: "alert"
        }
      ],
      dataSet: [],
      chunkDataSet: [],
      activeSession: {}
    };
  },
  mounted() {
    let _this = this;
      _this.queryService();
  },
  methods: {
    //查询service
    queryService() {
			const _this = this;
      getAllHostList().then(res => {
        if (res.success) {
          this.hostList = res.result;
          if (this.hostList.length > 0) {
						this.hostIds = this.hostList.map(it => it.id);
						this.init();
							_this.timerInterval = setInterval(() => {
				        _this.init();
			        }, 5 * 60 * 1000)
           }
        }
      });
    },
    //查询条件
    getCondition() {
			let _this = this;
      return {
        tags: _this.currSelectQuotaTab === 'none' ? '' : _this.currSelectQuotaTab,
        hostIds: _this.hostIds.join(','),
        interval: _this.interval,
        search: _this.searchStr.trim(),
        pageNumber: _this.pageIndex,
        pageSize: _this.pageSize
      };
    },
    //初始化查询
    init() {
      let _this = this;
      //清空查询数据
      _this.chunkDataSet = [];
			_this.loading = true;
      getTrendMetricList(_this.getCondition()).then(resp => {
        //数据按照名称分组
        let data = _.groupBy(resp.result, o => {
          return o.name;
				});
        //整理数据
         //整理数据
        if(resp.success === true && resp.result.records[0]) {
          //图表排序处理
          Object.keys(resp.result.records[0]).sort((a, b) => a > b ? 1 : -1 ).forEach((item) => {
            _this.chunkDataSet.push(resp.result.records[0][item]);
          })
        }
        //总条数
        _this.total = resp.result.total;
				_this.loading = false;
      });
    },
    changeService(tab) {
      let _this = this;
      _this.pageIndex = 1;
      _this.total = 0;
      _this.init();
    },
    toggleQuota(tab) {
      let _this = this;
      _this.pageIndex = 1;
      _this.total = 0;
      _this.init();
    },
    //改变页码
    handleSizeChange(size) {
      let _this = this;
      if (size === _this.pageSize) return;
      _this.pageSize = size;
      _this.pageIndex = 1;
      _this.init();
    },
    //改变当前页
    handleCurrentChange(pageIndex) {
      let _this = this;
      if (_this.pageIndex === pageIndex) return;
			_this.pageIndex = pageIndex;
			_this.init();
		},
		//查看历史
		linkToHistory(data) {
			const _this = this;
			_this.historyParam = data;
			_this.currview = 'history';
		},
		//查看详情
		linkToDetail(data) {
			const _this = this;
			_this.detailByNameParam = data;
			_this.currview = 'detailByName';
		},
    changeTime(v) {
      const _this = this;
      _this.interval = v;
      _this.init();
    },
    refresh: util.debounce(function() {
      this.init();
    }, 500)
  },
  destroyed() {
    let _this = this;
    //消除定时器
    clearInterval(_this.timerInterval);
    _this.timerInterval = null;
  }
};
</script>

<style lang="less" scoped>
  @import url("~@views/oracle/indicator-manage/indicator-manage.less");
</style>
