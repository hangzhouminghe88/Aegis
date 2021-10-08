<template>
  <Card>
    <template>
      <div class="page-toolbar">
        <a @click="$emit('close')" class="back-title margin-right-10">
          <Icon type="ios-arrow-back" style="margin: 0 0 2px 0" /><i style="font-style: normal">返回</i>
        </a>
        趋势图模板查询:
        <Input v-model="search" placeholder="趋势图模板查询:" clearable style="width: 200px" />
        <Button type="primary" class="search-btn" icon="ios-search" @click="handleSearch">搜索</Button>
        <Button @click="exportTable" icon="md-arrow-round-down">导出数据</Button>
      </div>
    </template>
    <template>
      <div class="page-table">
        <Table :columns="columns" :data="data" border ref="table"  :loading="loading"></Table>
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
import { getCurvDispbyhDetailList, getAllCurVesDetailList } from "@/api/index";
import table2excel from "@/libs/table2excel";
import { formatCount, exportData,formatDate } from "@/libs/date";
export default {
  name: "history",
  props: {
    param: {
      type: Array,
      default: () => {
        return [];
      }
    }
  },
  data() {
    return {
      search: "",
      pageNumber: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      data: [],
      sortColumn: "gmt_modified",
      sortType: "desc",
      columns: [
        {
          title: "更新时间",
          key: "gmtModified",
          resizable: true,
          width:200,
          ellipsis: true,
          render: (h, params) => {
            return h("span", params.row.gmtModified)
          }
        },
        {
          title: "实例",
          key: "service",
          resizable: true,
          width: 200,
          ellipsis: true
        },
        {
          title: "指标名",
          key: "name",
          resizable: true,
          width: 200,
          ellipsis: true
        },
        {
          title: "currval",
          key: "currval",
          resizable: true,
          render: (h, param) => {
            return h("span", formatCount(param.row.currval));
          }
        },
        {
          title: "baseval",
          key: "lastval",
          render: (h, param) => {
            return h("span", formatCount(param.row.lastval));
          }
        },
        {
          title: "lastday",
          key: "last1d",
          render: (h, param) => {
            return h("span", formatCount(param.row.last1d));
          }
        },
        {
          title: "ratio",
          key: "ratio"
        },
        {
          title: "score",
          key: "score"
        },
        {
          title: "times",
          key: "times"
        },
        {
          title: "super",
          key: "superby"
        },
        {
          title: "agf",
          key: "avgRatio"
        },
        {
          title: "7day",
          key: "last7d",
          render: (h, param) => {
            return h("span", formatCount(param.row.last7d));
          }
        }
      ]
    };
  },
  mounted() {
    const _this = this;
    _this.getCurVesDetailList();
  },
  methods: {
    changePage(v) {
      this.pageNumber = v;
      this.getCurVesDetailList();
    },
    changePageSize(v) {
      this.pageSize = v;
      this.getCurVesDetailList();
    },
    getCurVesDetailList() {
      const _this = this,
        params = {
          service: _this.param[0] ? _this.param[0].service : "",
          sname: _this.param[0] ? _this.param[0].sname : "",
          pageNumber: this.pageNumber,
          pageSize: this.pageSize,
          sort: this.sortColumn,
          order: this.sortType,
          name: _this.param[0] ? _this.param[0].name : "",
        };
      _this.loading = true;
      getCurvDispbyhDetailList(params).then(res => {
        _this.loading = false;
        if (res.success) {
          _this.data = res.result.records;
          _this.total = res.result.total;
        }
      });
    },
    exportTable() {
      const _this = this,
        params = {
          service: _this.param[0] ? _this.param[0].service : "",
          sname: _this.param[0] ? _this.param[0].name : "",
          sort: this.sortColumn,
          order: this.sortType
        };
      getAllCurVesDetailList(params)
        .then((res) => {
          let str = `更新时间,实例,指标名,currval,baseval,lastday,ratio,score,times,super,agf,7day\n`;
          if(res.result) {
            for(let item in res.result) {
              str += `${res.result[item].gmtModified},${res.result[item].service},${res.result[item].name},${res.result[item].currval},${res.result[item].lastval},${res.result[item].last1d},${res.result[item].ratio},${res.result[item].score},${res.result[item].times},${res.result[item].superby},${res.result[item].avgRatio},${res.result[item].last7d}\n`;
            }
          }
          exportData('text/cvs', str, _this.param[0]&& _this.param[0].service+'历史详情'+new Date().getTime()+`.csv`);
        })
      // table2excel.transform(
      //   _this.$refs.table,
      //   _this.$refs.table.$el,
      //   "指标详情"
      // );
    },
    handleSearch() {
      const _this = this;
      _this.pageNumber=1;
      _this.getCurVesDetailList()
    }
  }
};
</script>

<style lang="less" scoped>
@import url("~@views/oracle/indicator-manage/indicator-manage.less");
</style>
