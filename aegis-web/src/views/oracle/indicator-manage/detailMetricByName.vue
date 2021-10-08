<template>
  <div class="trend-metric">
    <template v-if="currview === 'index'">
      <div class="trend-metric__title">
				<a @click="$emit('close')" class="back-title margin-right-10">
          <Icon type="ios-arrow-back" style="margin: 0 0 2px 0" />返回
        </a>
        <span class="title" style="font-size: 16px;">关键指标趋势详情</span>
      </div>
      <div class="trend-metric__search">
        <Select v-model="interval" style="width: 120px;margin-right: 10px;" @on-change="changeTime">
          <Option v-for="item in timeTab" :value="item.label" :key="item.label">{{ item.value }}</Option>
        </Select>
        <Button type="default" icon="md-refresh"  @click="init"></Button>
      </div>
      <v-charts-detail
        :chartData="{service: service, chunkDataSet, pageIndex, isLink: false}"
        @linkToHistory="linkToHistory"
        :loading="loading"
      ></v-charts-detail>
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
      <history :param="historyParam" @close="currview = 'index'"></history>
    </template>
  </div>
</template>

<script>
import pagination from "@/views/my-components/pagination/Pagination";
import ChartsDetail from "@/views/my-components/metric/chartsDetail";
import { getAllHostList, getCurvDispbyhDetailMetricList } from "@/api/index";
import history from "@/views/oracle/indicator-manage/history";
import Loading from  '@/views/my-components/loading/Loading';
import { formatSize, formatDate } from "@/libs/date";
import _ from "lodash";
export default {
  name: "TrendMetricPage",
  components: {
    pagination,
    history,
    "v-charts-detail": ChartsDetail,
    Loading
  },
  props: {
    param: {
      type: Object,
      default : () => {
        return{}
      }
    }
  },
  data() {
    return {
      name: "",
      searchStr: "",
      timerInterval: null,
      pageIndex: 1,
      pageSize: 5,
      total: 0,
      hostList: [],
      service: "",
      currview: "index",
      historyParam: {},
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
        {
          label: 7 * 24 * 3600,
          value: '最近一周'
        },
        {
          label: 30* 24 * 60 * 60,
          value: '最近一月'
        },
        {
          label: 365 * 24 * 60 * 60,
          value: '最近一年'
        }
      ],
      dataSet: [],
      chunkDataSet: [],
      activeSession: {},
      loading: false,
      isError: false
    };
  },
  mounted() {
    let _this = this;
    this.init();
    _this.timerInterval = setInterval(() => {
      _this.init();
    }, 5 * 60 * 1000);
  },
  methods: {
    //查询条件
    getCondition() {
      let _this = this;
      return {
        interval: _this.interval,
        pageNumber: _this.pageIndex,
        pageSize: _this.pageSize,
        "search": _this.param.name.trim(),
        type: _this.param.type
      };
    },
    //初始化查询
    init() {
      let _this = this;
      //清空查询数据
      _this.dataSet = [];
      _this.total = 0;
      _this.loading = true;
      getCurvDispbyhDetailMetricList(_this.getCondition()).then(resp => {
        //数据按照名称分组
        if(resp.success === true && resp.result.records[0]) {
           for (let i in resp.result.records[0]) {
            _this.dataSet.push(resp.result.records[0][i]);
				   }
           _this.chunkDataSet = _this.dataSet;
        }
        //总条数
        _this.total = resp.result.total;
        _this.loading = false;
      });
    },
    changeService(tab) {
      let _this = this;
      _this.pageIndex = 1;
      _this.init();
    },
    toggleQuota(tab) {
      let _this = this;
      _this.pageIndex = 1;
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
      _this.currview = "history";
    },
    changeTime(v) {
      const _this = this;
      _this.interval = v;
      _this.init();
    }
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
