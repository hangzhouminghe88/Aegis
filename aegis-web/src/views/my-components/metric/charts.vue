<template>
  <div style="min-height: 30vh;" >
    <Draggable>
      <div class="trend-metric__chart" v-cloak v-for="(data, index) in chunkDataSet" :key="index">
        <template v-if="data && data.length > 0">
          <div class="trend-metric__chart_title">
            Graph Kind: {{data[0] && data[0].type}} | {{data[0] && data[0].service}}
            <a
                :class="{'a-link': chartData.isLink, 'normal': !chartData.isLink}"
                @click="linkToDetail(data[0])"
            >{{data[0] && $t(data[0].name)}}</a>-
            <a class="a-link" @click="linkToHistory(data)">history</a>
            | repeat times: {{data[0] && data[0].times}} | total score: {{data[0] && data[0].score}}
          </div>
          <div v-show="loading" class="loading">
            <Icon type="md-sync" style="font-size: 20px;" :class="{'loading-rotate': loading}"/>
          </div>
          <line-metric :value="{data: setMetricData(data)}"></line-metric>
        </template>
        <template v-else>
          <div
              class="trend-metric__chart"
              v-cloak
              style="text-align: center;"
          >
          <span slot="empty" class="table-nodata">
            <p class="empty-text" v-text="'暂无数据'"></p>
          </span>
          </div>
        </template>
      </div>
      <div
          v-cloak
          v-show="!loading && chunkDataSet.length<=0"
          style="text-align: center;"
      >
         <span slot="empty" class="table-nodata">
            <p class="empty-text" v-text="'暂无数据'"></p>
          </span>
      </div>
      <div
          v-cloak
          v-show="loading && chunkDataSet.length <= 0"
          style="background: #fff;height: 35vh;
        border-radius: 5px;
          padding: 20px;margin-bottom: 10px;"
      >
        <div style="background: #f8f8f8; width: 300px;height: 16px;"></div>
        <div style="background: #f8f8f8; margin-top: 10px; height: 28vh"></div>
      </div>
    </Draggable>
  </div>
</template>

<script>
import LineMetric from "@/views/my-components/metric/Line-metric";
import { formatSize, formatDate, dateFormatToStamp, formatMilliSecond, formatCount } from "@/libs/date";
import Loading from "../loading/Loading";
import _ from "lodash";
import Draggable from "vuedraggable"
export default {
  name: "charts",
  props: {
    chartData: {
      type: Object,
      default: () => {
        return {};
      }
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  components: {
    "line-metric": LineMetric,
    Loading,
    Draggable
  },
  data() {
    return {
      service: "",
      dataSet: [],
      chunkDataSet: [],
      pageIndex: 1,
      colors: [
        {
          line: {
            type: "linear",
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              {
                offset: 0,
                color: "#d8ace0"
              },
              {
                offset: 1,
                color: "#d8ace0"
              }
            ],
            globalCord: false
          },
          start: "rgba(216, 172, 224, 0.1)",
          end: "rgba(216, 172, 224, 0.3)"
        },
        {
          line: {
            type: "linear",
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              {
                offset: 0,
                color: "#009ffb"
              },
              {
                offset: 1,
                color: "#009ffb"
              }
            ],
            globalCord: false
          },
          start: "rgba(33, 117, 217, 0.1)",
          end: "rgba(33, 117, 217, 0.4)"
        },
        {
          line: {
            type: "linear",
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              {
                offset: 0,
                color: '#00b6c2'
              },
              {
                offset: 1,
                color: '#00b6c2'
              }
            ],
            globalCord: false
          },
          start: "rgba(255, 0, 0, 0.1)",
          end: "rgba(255, 0, 0, 0.2)"
        }
      ]
    };
  },
  mounted() {
    const _this = this;
    _this.service = _this.chartData.service;
    _this.chunkDataSet = _this.chartData.chunkDataSet;
  },
  methods: {
    //设置画图数据
    setMetricData(data) {
      let _this = this,
        dataList = [];
      if (!!data && data.length > 0) {
        //整理画图数据形式为[[当前值],[最后值],[最近一天]]
        dataList = [
          data.map(item => {
            return {
              time: item.time,
              value: _this.isTimeUnit(item) ? item.currval * 1000 * 1000: _this.isCount(item) ?  item.currval * 1000 : item.currval,
              label: "currval"
            };
          }),
          data.map(item => {
            return {
              time: item.time,
              value: _this.isTimeUnit(item) ? item.lastval * 1000 * 1000 : _this.isCount(item) ?  item.lastval * 1000 : item.lastval,
              label: "lastval"
            };
          }),
          data.map(item => {
            return {
              time: item.time,
              value: _this.isTimeUnit(item) ? item.last1d * 1000 * 1000 : _this.isCount(item) ?  item.last1d * 1000  : item .last1d,
              label: "lastday"
            };
          })
        ];
        return _this.reorganizeData(
          dataList, //对应画图数据
          `${data[0].service}-${_this.$t(data[0].name)}`, //对应图表名称
          _this.isTimeUnit(data[0]) ? formatMilliSecond : formatCount, //对应数据展示格式
          _this.colors, //对应相应颜色
          data[0].sname //对应纵轴名称
        );
      }
    },
    reorganizeData(dataList, title, func, color, sname) {
      let _this = this,
        avaerage = 0,
        max = 0,
        value = [];
      dataList.forEach((item, index) => {
        let values = [];
        if (!item) item = [];
        //数据按照时间分组
        let data = _.groupBy(item, o => {
          return o.time;
        });
        //遍历时间
        for (let key in data) {
          let _value;
          //按时间分组求和求某一时间下的平均值
          _value =
            _.sumBy(data[key], function(o) {
              return o.value;
            }) / data[key].length;
          values.push({
            time: key,
            value: _value
          });
        }
        //计算平均值与最大值
        if (values.length > 0) {
          avaerage = _.sumBy(values, it => it.value) / values.length;
          let maxValue = values.map(it => it.value);
          max = _.max(maxValue);
        }
        value.push({
          values,
          color: color[index],
          avaerage: avaerage,
          max: max,
          label: item[0].label
        });
      });
      return {
        dataList: value,
        title,
        func,
        color,
        sname
      };
    },
    linkToHistory(data) {
      const _this = this;
      _this.$emit("linkToHistory", data);
    },
    linkToDetail(data) {
      const _this = this;
      _this.$emit("linkToDetail", data);
    },
    isTimeUnit(arg) {
      if(arg.type==='SYSWTIM' || (arg.type=== 'SYSSTAT' && arg.name==='parse time elapsed')) return true;
      return false;
    },
    isCount(arg) {
      return ['buffer busy waits','enqueue waits', 'physical writes', 'logons cumulative', 'table scans (long tables)','table scans (short tables)', 'parse count (failures)', 'parse count (hard)', 'transaction rollbacks', 'user commits', 'user rollbacks', 'leaf node 90-10 splits', 'leaf node splits'].includes(arg.name) ? true : false
    }
  },
  watch: {
    //监听chartData的变化
    chartData: function(newVal, oldVal) {
      if (newVal !== oldVal) {
        const _this = this;
        _this.service = _this.chartData.service;
        _this.chunkDataSet = _this.chartData.chunkDataSet;
      }
    }
  }
};
</script>

<style lang="less" scoped>
.table-nodata {
  width: 100%;
  height: 30vh;
  line-height: 30vh;
  display: inline-block;
}
.normal {
  color: #222;
}
 .loading{
   position: absolute;
   right: 7rem;
   &-rotate{
     animation: rotate 1s linear infinite;
   }
 }
  @keyframes rotate {
    from {
      transform: rotate(0deg);
    }
    to{
      transform: rotate(360deg);
    }
  }
  .trend-metric__chart{
    clear: both;
    zoom: 1;
  }
</style>
