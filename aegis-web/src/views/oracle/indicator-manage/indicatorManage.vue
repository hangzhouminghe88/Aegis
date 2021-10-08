<template>
  <div class="trend-metric">
    <template v-if="currview === 'index'">
      <div class="trend-metric__title">
        <div class="title inline-block" style="font-size: 16px;margin-right: 10px;">关键指标趋势</div>
        <div style="margin-right:25px; width: 8rem" class="inline-block">
          <Select v-model="selectService" @on-change="changeService">
            <Option
              v-for="item in serviceList"
              :value="item.name"
              :key="item.name"
            >{{item.name}}</Option>
          </Select>
        </div>
      </div>
      <div class="trend-metric__search">
        <Select v-model="interval" style="width: 120px;margin-right: 10px;" @on-change="changeTime">
          <Option v-for="item in timeTab" :value="item.label" :key="item.label">{{ item.value }}</Option>
        </Select>
        <RadioGroup
          v-model="currSelectQuotaTab"
          @on-change="toggleQuota"
          type="button"
          style="margin-right:10px"
        >
          <Radio v-for="item in quotaTabs" :label="item.label" :key="item.value"></Radio>
        </RadioGroup>
        <a-form-item type="slot">
          <slot>
            <Select
              v-model="searchStr"
              style="width: 120px;margin-right: 10px;"
              @on-change="changeTemplateName"
            >
              <Option
                v-for="item in templateNameList"
                :value="item.label"
                :key="item.label"
              >{{ $t(item.value) }}</Option>
            </Select>
            <Button @click="handleReset">重置</Button>
          </slot>
        </a-form-item>
      </div>
      <keep-alive>
        <v-charts
          :chartData="{service: service, dataSet, chunkDataSet, pageIndex, isLink: true}"
          @linkToHistory="linkToHistory"
          @linkToDetail="linkToDetail"
          :loading="loading"
        ></v-charts>
      </keep-alive>
      <div class="page-pagination">
        <pagination
          :total="total"
          v-show="total > 0 && chunkDataSet.length > 0"
          :currentPage="pageIndex"
          @size-change="handleSizeChange"
          :pageSize="pageSize"
          @change="handleCurrentChange"
          :sizes="[5]"
        ></pagination>
      </div>
    </template>
    <template v-if="currview === 'history'">
      <history :param="historyParam" @close="closeOtherPage"></history>
    </template>
    <template v-if="currview === 'detailByName'">
      <detail-metric-by-name :param="detailByNameParam" @close="closeOtherPage"></detail-metric-by-name>
    </template>
    <template v-if="currview === 'alert'">
      <trend-log :param="alertParam" @close="closeAlert"></trend-log>
    </template>
  </div>
</template>

<script>
import pagination from "@/views/my-components/pagination/Pagination";
import Charts from "@/views/my-components/metric/charts";
import {
  getAllHostList,
  getCurvDispbyhList,
  getTemplateName,
  getTemplateByPagination
} from "@/api/index";
import history from "@/views/oracle/indicator-manage/history";
import detailMetricByName from "@/views/oracle/indicator-manage/detailMetricByName";
import { formatSize, formatDate } from "@/libs/date";
import Loading from "@/views/my-components/loading/Loading";
import TrendLog from "@/views/oracle/indicator-manage/trendLog";
import AFromItem from "@views/my-components/form/FormItem";
import { mapGetters, mapState } from "vuex";
import _ from "lodash";
export default {
  name: "TrendMetricPage",
  components: {
    pagination,
    history,
    "v-charts": Charts,
    "detail-metric-by-name": detailMetricByName,
    Loading,
    "trend-log": TrendLog,
    "a-form-item": AFromItem,
  },
  computed: {
    selectService: {
      get() {
        return this.$store.state.service.selectService;
      },
      set(value) {
        this.$store.state.service.selectService=value
      }
    },
    ...mapGetters({
      serviceList: "getServiceList",
    }),
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
      service: "",
      currview: "index",
      historyParam: {},
      detailByNameParam: "",
      alertParam: "",
      tabs: [],
      loading: false,
      isError: false,
      interval: 60 * 60,
      templateNameList: [],
      quotaTabs: [
        {
          label: "none",
          value: "none",
        },
        {
          label: "core",
          value: "core",
        },
        {
          label: "host",
          value: "host",
        },
        {
          label: "trans",
          value: "trans",
        },
        {
          label: "network",
          value: "network",
        },
        {
          label: "alert",
          value: "alert",
        },
      ],
      timeTab: [
        {
          label: 5 * 60,
          value: "最近5分钟",
        },
        {
          label: 60 * 60,
          value: "最近一小时",
        },
        {
          label: 24 * 60 * 60,
          value: "最近一天",
        },
        {
          label: 7 * 24 * 3600,
          value: "最近一周",
        },
        {
          label: 30 * 24 * 60 * 60,
          value: "最近一月",
        },
        {
          label: 365 * 24 * 60 * 60,
          value: "最近一年",
        },
      ],
      dataSet: [],
      chunkDataSet: [],
      activeSession: {},
    };
  },
  mounted() {
    const _this = this;
    if(this.selectService)
    _this.queryService();
  },
  methods: {
    //查询service
    queryService() {
      const _this = this;
      this.hostList = _this.serviceList;
      _this.service = _this.selectService;
      _this.getTemplateName();
      _this.init();
      _this.timerInterval = setInterval(() => {
        _this.init();
      }, 5 * 60 * 1000);
    },
    //查询条件
    getCondition() {
      const _this = this;
      return {
        tags:
        _this.currSelectQuotaTab === "none" ? "" : _this.currSelectQuotaTab,
        service: _this.service,
        interval: _this.interval,
        search: _this.searchStr == "" ? null : _this.searchStr,
        pageNumber: _this.pageIndex,
        pageSize: _this.pageSize,
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
      let templateList  = resp && resp.result &&  resp.result.records;
      for(let i in templateList) {
        let resp = await getCurvDispbyhList({..._this.getCondition(), name: templateList[i].name, sname: templateList[i].sname, templateType: templateList[i].type});
         _this.dataSet[i] = resp && resp.result && resp.result[0][Object.keys(resp.result[0])];
         _this.chunkDataSet = _this.dataSet.sort((a, b) => {
                              return a[0].name < b[0].name;
                            });
        _this.loading = false;
      }
      // templateList.forEach((it) => {
      //   //p.push(getCurvDispbyhList({..._this.getCondition(), name: it.name, sname: it.sname, templateType: it.type}))
      //   getCurvDispbyhList({..._this.getCondition(), name: it.name, sname: it.sname, templateType: it.type})
      //                    .then(res => {
                          
      //                    })
      // })
      // Promise.all(p).then(res => {
      //   res.forEach(it =>  _this.dataSet.push(it.result[0][Object.keys(it.result[0])]))
        
      // });
    },
    changeService(tab) {
      const _this = this;
      _this.pageIndex = 1;
      _this.total = 0;
      this.$store.dispatch('SETSELECTSERVICE', tab);
       _this.service = _this.selectService;
      _this.init();
    },
    toggleQuota(tab) {
      const _this = this;
      _this.pageIndex = 1;
      _this.total = 0;
      _this.getTemplateName();
      if (_this.currSelectQuotaTab !== "alert") _this.init();
      else {
        _this.alertParam = _this.service;
        _this.currview = "alert";
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
      _this.currview = "history";
      clearInterval(_this.timerInterval);
      _this.timerInterval = null;
    },
    //查看详情
    linkToDetail(data) {
      const _this = this;
      _this.detailByNameParam = data;
      _this.currview = "detailByName";
      clearInterval(_this.timerInterval);
      _this.timerInterval = null;
    },
    //关闭alert页面
    closeAlert() {
      const _this = this;
      _this.currview = "index";
      _this.currSelectQuotaTab = "none";
      _this.getTemplateName();
      _this.init();
    },
    //搜索
    handleSearch: _.debounce(function() {
      const _this = this;
      _this.total = 0;
      _this.pageIndex = 1;
      _this.init();
    }, 500),
    /**
     * 由于清空searchStr会触发select故不作请求处理
     *
     */
    handleReset: _.debounce(function () {
      const _this = this;
      if (!_this.searchStr) {
        _this.pageIndex = 1;
        _this.init();
      } else {
        _this.searchStr = "";
        _this.pageIndex = 1;
      }
    }, 500),
    /**
     * 改变时间触发的事件
     */
    changeTime(v) {
      const _this = this;
      _this.interval = v;
      _this.init();
    },
    /**
     * 改变指标名称来搜索数据
     */
    changeTemplateName(v) {
      const _this = this;
      _this.searchStr = v;
      _this.pageIndex = 1;
      _this.init();
    },
    closeOtherPage() {
      const _this = this;
      _this.timerInterval = setInterval(() => {
        _this.init();
      }, 5 * 60 * 1000);
      _this.currview = "index";
    },
    getTemplateName() {
      const _this = this,
        params = {
          tags:
            _this.currSelectQuotaTab === "none" ? "" : _this.currSelectQuotaTab,
          name: _this.name,
        };
      getTemplateName(params).then((res) => {
        let nameList = res && res.result && res.result.map((item) => item.name);
        _this.templateNameList = Array.from(new Set(nameList)).map((item) => {
          return {
            value: item,
            label: item,
          };
        });
      });
    },
  },
  destroyed() {
    const _this = this;
    //消除定时器
    clearInterval(_this.timerInterval);
    _this.timerInterval = null;
  },
  watch: {
    '$store.state.service.serviceList': function(newVal, oldVal) {
      if(newVal !== oldVal) {
        this.queryService();
      }
    }
   }
};
</script>

<style lang="less" scoped>
@import url("~@views/oracle/indicator-manage/indicator-manage.less");
</style>
