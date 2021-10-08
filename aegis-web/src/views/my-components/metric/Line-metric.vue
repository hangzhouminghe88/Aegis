<template>
  <div class="line-metric">
		<!-- <div class="tab-containner">
      <span class="tab-item" v-for="(data, index) in metricLabel"
			      :key="index"
						:style="{'background': data.color}"
						@click="changeLabel(index)"></span>
		</div> -->
    <div v-if="!isLoad" ref="chart-content" class="line-metric__content"></div>
    <div
      v-if="!isLoad"
      class="line-metric__lengend"
      v-for="(data, index) in metricLabel"
      :key="index"
    >
      <div class="line-metric__lengend-item">
        <div class="line-metric__lengend-item__label" :style="{'background': data.color}">
          <input type="checkbox"  @change.stop="selectLabel($event, index)"/>
        </div>
        <span class="line-metric__lengend-item__value_label">{{sname}}</span>
        <span class="line-metric__lengend-item__value_label">{{data.label}}:</span>
        <span class="line-metric__lengend-item__value">{{data.value}}</span>
        <span class="line-metric__lengend-item__value_label">Avagerage:</span>
        <span class="line-metric__lengend-item__value">{{data.avaerage}}</span>
        <span class="line-metric__lengend-item__value_label">Max:</span>
        <span class="line-metric__lengend-item__value">{{data.max}}</span>
      </div>
    </div>
    <Loading v-if="isLoad && !nodata" :cover="true"></Loading>
    <template
      v-if="nodata"
      style="width: 100%; text-align:center;height: 100%;position: absolute;line-height: 40vh;"
    >
      <span class="table-nodata">
        <p class="empty-text" v-text="'暂无数据'"></p>
      </span>
    </template>
  </div>
</template>

<script>
import { formatSize, formatDate, formatMilliSecond, debounce, throttle } from "@/libs/date";
//按需加载echarts
import echarts from "echarts/lib/echarts";
import 'echarts/lib/component/legend';
import 'echarts/lib/component/title';
import 'echarts/lib/component/tooltip'
import 'echarts/lib/chart/line';
import Loading from  '../loading/Loading';
import _ from "lodash";
export default {
	name: "LineMetric",
	components: {
		Loading
	},
  props: {
    value: {
      type: Object,
      default: {}
    }
  },
  data() {
	  let _this = this;
    return {
      chartInstance: "",
      colors: [],
      isLoad: false,
      nodata: false,
      sname: "",
      checkIndex: '',
      unitForSize: ['phyw', 'logc'],
      options: {
        title: {
          textStyle: {
            color: "#222", //颜色
            fontStyle: "normal", //风格
            fontWeight: 550, //粗细
            fontFamily: "'Courier New',Microsoft yahei", //字体
            fontSize: 14 //大小
          },
          textAlign: "center", //水平对齐,
          left: "50%"
        },
        tooltip: {
          trigger: "axis",
          confine: true,
          formatter: function(param) {
            let str = `<div style="padding: 0px 30px;opcity: 0;box-size: border-box;border-radius: 2px; background: #fff;display: inline-block">
            <div>${param[0].axisValueLabel}</div>`;
            for (let i in param) {
              str += `<div style="padding: 5px 0px;">
                 <span style="display: inline-block;width: 10px;height: 10px; background:${param[i].color.colorStops[1].color}"></span>
                 <span style="dispaly: inline-block;color: #222;">${param[i].seriesName}\r\t${_this.value.data.func(param[i].value)}</span>
               </div>`;
            }
            str += `</div>`;
            return str;
          },
          backgroundColor: "rgba(255,255,255,0.98)",
          borderColor: "#EEF3F7",
          borderWidth: 1,
          extraCssText:
            "box-shadow: 0 2px 4px 0 rgba(217,225,233,0.40);border-radius: 2px;padding: 10px;",
          textStyle: {
            color: "#5E6978",
            fontFamily: "PingFangSC-Regula",
            fontSize: "12"
          }
        },
        grid: {
          left: "3%",
          right: "4%",
          bottom: "3%",
          containLabel: true
        },
        xAxis: {
          type: "category",
          boundaryGap: false,
          data: [],
          interval: 7,
          splitLine: {
            show: true,
            lineStyle: {
              color: "#555",
              opacity: 0.9,
              type: "dashed"
            }
          },
          axisLabel: {
            fontSize: 12,
            align: "center",
            color: "#222",
            fontFamily:'Courier New',
            fontWeight: 550,
            padding: [0,0,0,0]
          },
          axisLine: {
            color: {
              type: "radial",
              x: 0.5,
              y: 0.5,
              r: 0.5,
              colorStops: [
                {
                  offset: 0,
                  color: "red" // 0% 处的颜色
                },
                {
                  offset: 1,
                  color: "blue" // 100% 处的颜色
                }
              ],
              global: false // 缺省为 false
            },
            lineStyle: {
              color: "#369"
            }
          }
        },
        yAxis: {
          type: "value",
          name: "sess/5min",
          nameLocation: "middle",
          splitLine: {
            show: true,
            lineStyle: {
              color: "#555",
              opacity: 0.9,
              type: "dashed"
            }
          },
          nameTextStyle: {
            color: "#222",
            fontWeight: 500,
            padding: [0, 0, 45, 10],
            fontSize: 16,
            fontFamily:'Courier New'
          },
          axisLine: {
            color: {
              type: "radial",
              x: 0.5,
              y: 0.5,
              r: 0.5,
              colorStops: [
                {
                  offset: 0,
                  color: "red" // 0% 处的颜色
                },
                {
                  offset: 1,
                  color: "blue" // 100% 处的颜色
                }
              ],
              global: false // 缺省为 false
            },
            lineStyle: {
              color: "#222"
            }
          },
          axisLabel: {
            formatter: function(value) {
              return _this.value.data.func(value);
						},
            padding: [15, 0, 15, 50],
          }
        },
        series: []
      }
    };
  },
  mounted() {
    let _this = this;
    //获得图标dom;
		_this.chartInstance = echarts.init(_this.$refs["chart-content"]);
    //初始化表格数据
    _this.initMetricData(_this.value.data);
    window.addEventListener("resize", throttle(_this.resizeChart, 1000, 2), false);
  },
  computed: {
	  metricLabel() {
	    let _this = this;
	    let labels = [];
      _this.value.data.dataList.forEach((it, index) => {
        //设置当前值
        let currentValue = _.last(it.values).value;
        currentValue =  _.has(_this.value.data, "func")
          ? _this.value.data.func(currentValue)
         : currentValue;
        //格式化当前值设置当前值label以及颜色
        labels.push({
          value: currentValue,
          color: it.color.line.colorStops[1].color,
          text: it.id,
          max:  _.has(_this.value.data, "func")
            ? _this.value.data.func(it.max)
            : it.max,
          avaerage:  _.has(_this.value.data, "func")
            ? _this.value.data.func(it.avaerage)
            : it.avaerage,
          label: it.label,
          checked: false
        });
      });
      return labels;
    }
  },
  methods: {
    initMetricData(data) {
      let _this = this;
      //如果含有title则标题为title
      if (_.has(data, "title")) {
        _this.options.title.text = data.title;
      }
      //如果含又dataList则整理数据
      if (_.has(data, "dataList")) {
        //load为true
        _this.isLoad = true;
        _this.title = data.title;
        _this.sname = data.sname;
      }
      _this.drawChart(
        _this.chartInstance,
        _this.value.data.dataList,
        _this.options,
        _this.value.data.func,
        _this.value.data.name
      );
		},
    drawChart(chart, dataList, option, formatter, name) {
      let _this = this;
      if (!dataList) {
        _this.nodata = true;
        return;
      }
      _this.colors = [];
      let timeLine = _this.getTimeLine(dataList);
      let series = [];
      //设置基线
      let seriesLineBase = {
        type: "line",
        smooth: true,
        animationEasing: 'linear',
        animationDuration: 1000,
        symbol: "circle",
        showSymbol: false,
        data: series[0],
        lineStyle: {
          normal: {
            width: 2,
            color: "#0088EF"
          }
        },
        areaStyle: {
          normal: {
            color: {
              type: "linear",
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [],
              globalCoord: false // 缺省为 false
            }
          }
        }
      };
      //遍历数据设置线条颜色设置图片数据series;
      dataList.forEach(it => {
        let newLine = _.cloneDeep(seriesLineBase);
        newLine.data = it.values.map(item => item.value);
        newLine.name = it.label;
        newLine.lineStyle.normal.color = it.color.line;
        newLine.areaStyle.normal.color.colorStops.push(
          {
            offset: 0,
            color: it.color.start
          },
          {
            offset: 1,
            color: it.color.end
          }
        );
        newLine.itemStyle = {
          normal: {
            color: it.color.line
          }
        };
        _this.colors.push(it.color.end);
        series.push(newLine);
      });
      if (chart) {
        //克隆画图配置选项
        let _option = _.cloneDeep(option);
        //格式化y轴坐标
        //_option.yAxis.axisLabel.formatter = formatter;
        //格式化x轴坐标
				_option.xAxis.data = timeLine;
        //画图数据赋值
        _option.series = series;
        if(series.length <= 0) {
          _option.yAxis.axisLine.show = false;
          _option.xAxis.axisLine.show = false;
        }else {
          _option.yAxis.axisLine.show = true;
          _option.xAxis.axisLine.show = true;
        }
        //设置纵轴标题
        _option.yAxis.name = _this.sname ? _this.sname + "/5min" : "";
        //画图表
        chart.setOption(_option, true);
        _this.isLoad = false;
        _this.nodata = false;
      }
    },
    //格式化x轴
    getTimeLine: function(dataList) {
      if (!dataList) return;
      if (dataList && !dataList[0]) return;
      let showday = false;
      //如果大于七天则只显示日期不现实分秒
      showday =
        dataList[0].values[1] && dataList[0].values[0]
          ? Number(dataList[0].values[dataList[0].values.length -1].time) - Number(dataList[0].values[0].time) >=
            7 * 24 * 60 * 60 * 1000
          : false;
      let timeLine;
      if (dataList.length > 0 && showday) {
        if (!_.isUndefined(dataList[0])) {
          timeLine = dataList[0].values.map(item => {
            return formatDate(new Date(Number(item.time)), "yyyy-MM-dd");
          });
        }
      } else {
        if (!_.isUndefined(dataList[0])) {
          timeLine = dataList[0].values.map(item => {
            return formatDate(
              new Date(Number(item.time)),
              "yyyy-MM-dd hh:mm"
            );
          });
        }
      }
      return timeLine;
    },
    //设置x轴显示几个坐标点
    resizeChart(e) {
      let _this = this;
      if (_this.chartInstance) _this.chartInstance.resize();
		},
    selectLabel(event, index) {
      const _this = this, value = [];
      if(event.target.checked){
        event.target.parentNode.classList.add("checked");
        _this.metricLabel[index].checked = true;
        for(let i = 0; i <= _this.metricLabel.length - 1; i++) {
          if(!_this.metricLabel[i].checked){
            value.push(_this.value.data.dataList[i]);
          }
        }
        _this.drawChart(
          _this.chartInstance,
          value,
          _this.options,
          _this.value.data.func,
          _this.value.data.name
        );
      }else{
        event.target.parentNode.classList.remove("checked");
        _this.metricLabel[index].checked = false;
        for(let j = 0; j <= _this.metricLabel.length - 1; j++) {
          if(!_this.metricLabel[j].checked){
            value.push(_this.value.data.dataList[j]);
          }
        }
        _this.drawChart(
          _this.chartInstance,
          value,
          _this.options,
          _this.value.data.func,
          _this.value.data.name
        );
      }
      event.stopPropagation();
    }
  },
  beforeDestroy() {
    let _this = this;
    //取消监听
    window.removeEventListener("resize", _this.resizeChart, false);
  },
  watch: {
    value: function(newVal, oldVal) {
      let _this = this;
      if (newVal !== oldVal) {
        _this.initMetricData(newVal.data);
      }
    }
  }
};
</script>

<style lang="less" scoped>
@import url("~@views/my-components/metric/linemetric.less");
</style>
