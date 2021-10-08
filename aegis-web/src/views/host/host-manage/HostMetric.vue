<template>
  <div class="metric-content">
    <template v-if="currview === 'index'">
      <header>
        <div class="trend-metric__search">
          <a @click="$emit('close')" class="back-title margin-right-10">
            <Icon type="ios-arrow-back" style="margin: 0 0 2px 0" />返回
          </a>
           <Select v-model="interval" style="width: 120px;margin-right: 10px;" @on-change="changeTime">
        <Option v-for="item in timeTab" :value="item.label" :key="item.label">{{ item.value }}</Option>
      </Select>
          <Button @click="handleSearch" type="primary" icon="el-icon-search" style="margin-right: 10px">搜索</Button>
        </div>
      </header>
      <section>
        <v-charts :loading="loading" :chartData="{service: service, dataSet, chunkDataSet, pageIndex, isLink: true}" @linkToHistory="linkToHistory" @linkToDetail="linkToDetail"></v-charts>
      </section>
      <footer>
        <div class="page-pagination">
          <pagination
              :total="total"
              v-show="total > 0"
              :currentPage="pageIndex"
              @size-change="handleSizeChange"
              :pageSize="pageSize"
              @change="handleCurrentChange"
              :sizes="[5]"
          ></pagination>
        </div>
      </footer>
    </template>
    <template v-if="currview === 'history'">
      <history  :param="historyParam" @close="currview = 'index'" ></history>
    </template>
    <template v-if="currview === 'detailByName'">
      <detail-metric-by-name  :param="detailByNameParam" @close="currview = 'index'" style="margin: 0px 0px;"></detail-metric-by-name>
    </template>
  </div>
</template>

<script>
  import detailMetricByName from '@/views/oracle/indicator-manage/detailMetricByName';
  import pagination from '@/views/my-components/pagination/Pagination'
  import { getAllHostList, getCurvDispbyhList, getTemplateByPagination } from '@/api/index'
  import Loading from  '@/views/my-components/loading/Loading';
  import history from '@/views/oracle/indicator-manage/history';
  import Charts from "@/views/my-components/metric/charts";
  import { formatSize, formatDate, stopBubble } from "@/libs/date";
  export default {
    name: "HostMetric",
    props: {
      param: {
        type: Object,
        default: () => {
          return {}
        }
      }
    },
    components: {
      pagination,
      history,
      "v-charts": Charts,
      'detail-metric-by-name': detailMetricByName,
      Loading
    },
    data() {
      return {
        total: 0,
        pageIndex: 1,
        pageSize: 5,
        loading: false,
        service: '',
        dataSet: [],
        chunkDataSet: [],
        timerInterval: null,
        isError: false,
        interval: 3600,
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
        currview: 'index'
      }
    },
    mounted() {
      const _this = this;
      _this.queryService();
    },
    methods: {
      //查询service
      queryService() {
        const _this = this;
        _this.init();
        _this.timerInterval = setInterval(() => {
            _this.init();
          }, 5 * 60 * 1000)
      },
      //查询条件
      getCondition() {
        const _this = this;
        return {
          tags: '',
          service: _this.param.name,
          interval: _this.interval,
          type: 'SYSHOST',
          pageNumber: _this.pageIndex,
          pageSize: _this.pageSize
        };
      },
      //初始化查询
      async init() {
        const _this = this;
        //清空查询数据
        _this.dataSet = [];
        _this.loading = true;
        _this.isError = false;
        let resp = await getTemplateByPagination(_this.getCondition())
        _this.total = resp.result && resp.result.total && resp.result.total;
        let templateList  = resp.result.records;
        let p = []
        templateList.forEach((it) => {
          p.push(getCurvDispbyhList({..._this.getCondition(), name: it.name, sname: it.sname, templateType: it.type}))
        })
        Promise.all(p).then(res => {
          res.forEach(it =>  _this.dataSet.push(it.result[0][Object.keys(it.result[0])]))
          _this.chunkDataSet = _this.dataSet;
          _this.loading = false;
        });
      },
      changeService(tab) {
        const _this = this;
        _this.pageIndex = 1;
        _this.total = 0;
        _this.init();
      },
      toggleQuota(tab) {
        const _this = this;
        _this.pageIndex = 1;
        _this.total = 0;
        if(_this.currSelectQuotaTab !== 'alert')
          _this.init();
        else{
          _this.alertParam = _this.service;
          _this.currview = 'alert'
        }
      },
      //改变页码
      handleSizeChange(size) {
        const _this = this;
        if (size === _this.pageSize) return;
        _this.pageSize = size;
        _this.pageIndex = 1;
        _this.init();
      },
      //改变当前页
      handleCurrentChange(pageIndex) {
        const _this = this;
        if (_this.pageIndex === pageIndex) return;
        _this.pageIndex = pageIndex;
        _this.init();
      },
      //查看历史
      linkToHistory(data) {
        const _this = this;
        _this.historyParam = data;
        _this.currview = 'history';
        clearInterval(_this.timerInterval);
        _this.timerInterval = null;
      },
      //查看详情
      linkToDetail(data) {
        const _this = this;
        _this.detailByNameParam = data;
        _this.currview = 'detailByName';
        clearInterval(_this.timerInterval);
        _this.timerInterval = null;
      },
      //关闭alert页面
      closeAlert() {
        const _this = this;
        _this.currview = 'index';
        _this.currSelectQuotaTab = 'none';
        _this.init();
      },
      //搜索
      handleSearch() {
        const _this = this;
        _this.total =0;
        _this.init()
      },
      changeTime(v) {
      const _this = this;
      _this.interval = v;
      _this.init();
    }
    },
    destroyed() {
      const _this = this;
      //消除定时器
      clearInterval(_this.timerInterval);
      _this.timerInterval = null;
    }
  }
</script>

<style lang="less" scoped>
  .metric-content{
    display: block;
    background: #fff;
    border-radius: 4px;
    font-size: 14px;
    position: relative;
    transition: all .2s ease-in-out;
    border: 1px solid #dcdee2;
    border-color: #e8eaec;
  }
  @import url("~@views/oracle/indicator-manage/indicator-manage.less");
</style>
