<style lang="less" scoped>
@import "../../../styles/table-common.less";
@import "./hostManage.less";
</style>
<template>
  <div class="search">
    <template v-if="currView === 'index'">
      <Card>
        <Row class="operation">
          <Button @click="add" type="primary" icon="md-add">添加主机</Button>
          <Button
            @click="delAll"
            icon="md-trash"
            v-permission="['ROLE_ADMIN', 'ROLE_TEST']"
            >批量删除</Button
          >
          <Button @click="init" icon="md-refresh">刷新</Button>
        </Row>
        <Row>
          <Table
            :loading="loading"
            border
            :columns="columns"
            :data="data"
            ref="table"
            sortable="custom"
            @on-sort-change="changeSort"
            @on-selection-change="changeSelect"
          ></Table>
        </Row>
        <Row type="flex" justify="end" class="page">
          <Page
            :current="pageNumber"
            :total="total"
            :page-size="pageSize"
            @on-change="changePage"
            @on-page-size-change="changePageSize"
            :page-size-opts="[10, 20, 50]"
            size="small"
            show-total
            show-elevator
            show-sizer
          ></Page>
        </Row>
      </Card>
    </template>
    <template v-if="currView === 'template'">
      <host-template
        @close="currView = 'index'"
        :param="templateParam"
      ></host-template>
    </template>
    <template v-if="currView === 'metric'">
      <host-metric
        @close="currView = 'index'"
        :param="metricParam"
      ></host-metric>
    </template>
    <!-- 编辑 -->
    <Modal
      :title="modalTitle"
      v-model="modalVisible"
      :mask-closable="false"
      width="700"
    >
      <Form
        ref="form"
        :model="form"
        :label-width="100"
        :rules="formValidate"
        style="padding: 0px 20px; display: flex"
      >
        <div>
          <FormItem label="主机群组" prop="groupId">
            <Select
              v-model="form.groupId"
              placeholder="请选择"
              style="width: 200px"
            >
              <Option
                v-for="item in groupList"
                :value="item.id"
                :key="item.id"
                >{{ item.name }}</Option
              >
            </Select>
          </FormItem>
          <FormItem label="主机名称" prop="name">
            <Input v-model="form.name" />
          </FormItem>
          <FormItem label="操作系统" prop="osType">
            <Input v-model="form.osType" />
          </FormItem>
          <FormItem label="版本信息" prop="osVersion">
            <Input v-model="form.osVersion" />
          </FormItem>
          <FormItem label="实例名称" prop="service">
            <Input v-model="form.service" />
          </FormItem>
          <FormItem label="主备标志" prop="dbRole">
            <Select
              v-model="form.dbRole"
              placeholder="请选择"
              clearable
              style="width: 200px"
            >
              <Option :value="0">primary</Option>
              <Option :value="1">standby</Option>
            </Select>
          </FormItem>
          <FormItem label="数据库类型" prop="dbType">
            <Select
              v-model="form.dbType"
              placeholder="请选择"
              clearable
              style="width: 200px"
            >
              <Option :value="0">oracle</Option>
              <Option :value="1">mysql</Option>
            </Select>
          </FormItem>
        </div>
        <div>
          <FormItem label="IP地址" prop="ipAddr">
            <Input v-model="form.ipAddr" />
          </FormItem>
          <FormItem label="用户名" prop="userName">
            <Input v-model="form.userName" />
          </FormItem>
          <FormItem label="密码" prop="password">
            <Input v-model="form.password" type="password" password />
          </FormItem>
          <FormItem label="端口" prop="port">
            <Input v-model="form.port" type="text" />
          </FormItem>
          <FormItem label="连接身份" prop="identity">
            <Select v-model="form.identity" placeholder="请选择">
              <Option value="normal">Normal</Option>
              <Option value="sysdba">SYSDBA</Option>
              <Option value="sysoper">SYSOPER</Option>
            </Select>
          </FormItem>
          <FormItem label="监控级别" prop="monLevel">
            <Select
              v-model="form.monLevel"
              placeholder="请选择"
              clearable
              style="width: 200px"
            >
              <Option :value="0">重要</Option>
              <Option :value="1">普通</Option>
            </Select>
          </FormItem>
          <FormItem label="阈值定义" prop="threshold">
            <Input v-model="form.threshold" />
          </FormItem>
        </div>
      </Form>
      <div slot="footer">
        <Button type="text" @click="cancel">取消</Button>
        <Button
          type="primary"
          :loading="submitLoading"
          @click="submit"
          v-permission="['ROLE_ADMIN', 'ROLE_TEST']"
          >提交</Button
        >
      </div>
    </Modal>
  </div>
</template>

<script>
import {
  getHostList,
  addHost,
  editHost,
  deleteHost,
  getAllHostGroupList,
  getGroupName,
} from "@/api/index";
import util from "@/libs/util.js";
import HostTemplate from "./hostTemplate";
import HostMetric from "./HostMetric";
import { stopBubble } from "@/libs/date";
import { ipV4Validate } from "@/libs/validate";
import { mapActions } from "vuex";

export default {
  name: "host-manage",
  components: {
    HostTemplate,
    HostMetric,
  },
  data() {
    return {
      openTip: false,
      openLevel: "0",
      loading: true,
      searchKey: "",
      sortColumn: "createTime",
      sortType: "desc",
      modalType: 0,
      modalTitle: "",
      modalVisible: false,
      detailModalTitle: "",
      detailModalVisible: false,
      currentHost: null,
      form: {
        name: "",
        description: "",
        userName: "",
        password: "",
        port: "",
        identity: "normal",
      },
      formValidate: {
        groupId: [
          {
            required: true,
            message: "请选择主机群组",
            type: "number",
            trigger: "change",
          },
        ],
        name: [
          { required: true, message: "主机名称不能为空", trigger: "blur" },
        ],
        service: [
          { required: true, message: "实例名称不能为空", trigger: "blur" },
        ],
        ipAddr: [
          { required: true, message: "IP地址不能为空", trigger: "blur" },
          {
            validator: ipV4Validate,
            message: "IP地址格式不正确",
            trigger: "blur",
          },
        ],
        userName: [
          {
            required: true,
            message: "用户名不能为空",
            type: "string",
            trigger: "blur",
          },
        ],
        password: [
          {
            required: true,
            message: "密码不能为空",
            type: "string",
            trigger: "blur",
          },
        ],
        port: [
          {
            required: true,
            message: "端口不能为空",
            type: "string",
            trigger: "blur",
          },
          {
            validator: function (rule, value, cb) {
              if (value && !/\d/.test(value)) {
                cb("端口格式不正确");
              } else {
                cb();
              }
            },
            message: "端口格式不正确",
            type: "string",
            trigger: "blur",
          },
        ],
        identity: [
          {
            required: true,
            message: "登录身份不能为空",
            type: "string",
            trigger: "blur",
          },
        ],
      },
      submitLoading: false,
      selectList: [],
      selectCount: 0,
      currView: "index",
      templateParam: {},
      metricParam: {},
      columns: [
        {
          type: "selection",
          width: 60,
          align: "center",
        },
        {
          title: "主机名称",
          key: "name",
          sortable: false,
          width: 150,
        },
        {
          title: "操作系统",
          key: "osType",
          sortable: false,
          width: 150,
        },
        {
          title: "IP地址",
          key: "ipAddr",
          sortable: false,
          tooltip: true,
          width: 200,
        },
        {
          title: "DB类型",
          key: "dbType",
          sortable: false,
          width: 150,
          render: (h, params) => {
            if (params.row.dbType == 0) return h("span", "oracle");

            return h("span", "mysql");
          },
        },
        {
          title: "DB角色",
          key: "dbRole",
          sortable: false,
          width: 150,
          render: (h, params) => {
            if (params.row.dbRole == 0) return h("span", "primary");

            return h("span", "standby");
          },
        },
        {
          title: "所属组",
          key: "groupName",
          width: 150,
        },
        {
          title: "负载",
          key: "upLoad",
          sortable: false,
          width: 100,
        },
        {
          title: "运行天数",
          key: "upDays",
          sortable: false,
          width: 100,
        },
        {
          title: "检查时间",
          key: "lastTime",
          sortable: false,
          width: 100,
        },
        {
          title: "操作",
          key: "action",
          align: "center",
          width: 300,
          render: (h, params) => {
            return h("div", [
              h(
                "Button",
                {
                  props: {
                    size: "small",
                  },
                  style: {
                    marginRight: "5px",
                  },
                  directives: [
                    {
                      name: "permission",
                      value: ["ROLE_ADMIN", "ROLE_TEST"],
                    },
                  ],
                  on: {
                    click: () => {
                      this.edit(params.row);
                    },
                  },
                },
                "编辑"
              ),
              h(
                "Button",
                {
                  props: {
                    type: "error",
                    size: "small",
                  },
                  directives: [
                    {
                      name: "permission",
                      value: ["ROLE_ADMIN", "ROLE_TEST"],
                    },
                  ],
                  style: {
                    marginRight: "5px",
                  },
                  on: {
                    click: () => {
                      this.remove(params.row);
                    },
                  },
                },
                "删除"
              ),
              h(
                "Button",
                {
                  props: {
                    type: "error",
                    size: "small",
                  },
                  style: {
                    marginRight: "5px",
                  },
                  on: {
                    click: () => {
                      this.templateParam = params.row;
                      this.currView = "template";
                    },
                  },
                },
                "模板"
              ),
              h(
                "Button",
                {
                  props: {
                    type: "error",
                    size: "small",
                  },
                  on: {
                    click: () => {
                      this.metricParam = params.row;
                      this.currView = "metric";
                    },
                  },
                },
                "趋势图"
              ),
            ]);
          },
        },
      ],
      data: [],
      pageNumber: 1,
      pageSize: 10,
      total: 0,
      selectAllFlag: false,
      dataType: 0,
      groupList: [],
    };
  },
  methods: {
    //初始化查找所有组，以及实例列表
    init() {
      this.getAllGroup();
      this.getHostList();
    },
    //得到所有实例
    getAllGroup() {
      getAllHostGroupList({}).then((res) => {
        this.loading = false;
        if (res.success) {
          this.groupList = res.result;
        }
      });
    },
    //改变当前页发出的请求
    changePage(v) {
      this.pageNumber = v;
      this.getHostList();
      this.clearSelectAll();
    },
    //改变页码发出请求
    changePageSize(v) {
      this.pageSize = v;
      this.getHostList();
    },
    //排序
    changeSort(e) {
      this.sortColumn = e.key;
      this.sortType = e.order;
      if (e.order == "normal") {
        this.sortType = "";
      }
      this.getHostList();
    },
    //获得数据库实例列表
    getHostList() {
      this.loading = true;
      let params = {
        pageNumber: this.pageNumber,
        pageSize: this.pageSize,
        sort: this.sortColumn,
        order: this.sort,
      };
      getHostList(params).then((res) => {
        this.loading = false;
        if (res.success) {
          this.setData(res);
        }
      });
    },
    cancel() {
      this.modalVisible = false;
    },
    //设置数据
    setData(res) {
      for (let i in res.result.records) {
        let params = {
          id: res.result.records[i].groupId,
        };
        getGroupName(params).then((resp) => {
          this.data = res.result.records.map((item) => {
            if (res.result.records[i].groupId === item.groupId && resp.result && resp.result.name) {
              item.groupName = resp.result.name;
            }
            return item;
          });
        });
      }
      this.total = res.result.total;
    },
    submit() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          if (this.modalType == 0) {
            // 添加
            this.submitLoading = true;

            addHost(this.form).then((res) => {
              this.submitLoading = false;
              if (res.success) {
                this.$Message.success("操作成功");
                this.getHostList();
                this.modalVisible = false;
                this.updateAllService();
              }
            });
          } else {
            this.submitLoading = true;
            editHost(this.form).then((res) => {
              this.submitLoading = false;
              if (res.success) {
                this.$Message.success("操作成功");
                this.getHostList();
                this.modalVisible = false;
              }
            });
          }
        }
      });
    },
    add(e) {
      //阻止事件冒泡
      stopBubble(e);
      this.modalType = 0;
      this.modalTitle = "添加主机信息";
      this.$refs.form.resetFields();
      this.form = {
        dbType: 0,
        dbRole: 0,
        monLevel: 0,
      };
      this.modalVisible = true;
    },
    //编辑
    edit(v) {
      this.modalType = 1;
      this.modalTitle = "编辑主机信息";
      this.$refs.form.resetFields();
      // 转换null为""
      for (let attr in v) {
        v[attr] = v[attr];
        if (v[attr] == null) {
          v[attr] = "";
        }
      }
      let str = JSON.stringify(v);

      let info = JSON.parse(str);
      this.form = info;
      this.modalVisible = true;
    },
    //更新所有实例
    updateAllService() {
       getRequest("/host/getAll").then((res) => {
                  if (res.success) {
                    if (res.result.length <= 0) {
                      Message.info("请添加实例!");
                    }
                    let result = res.result.sort((a, b) => {
                      return a.name > b.name ? 1 : -1;
                    });
                    this.$store.commit("QUERYALLSERVICE", result);
                  }
                });
    },
    //删除实例
    remove(v) {
      this.$Modal.confirm({
        title: "确认删除",
        content: "您确认要删除主机 " + v.name + " ?",
        loading: true,
        onOk: () => {
          deleteHost(v.id).then((res) => {
            this.$Modal.remove();
            if (res.success) {
              this.$Message.success("删除成功");
              this.getHostList();
              this.updateAllService();
            }
          });
        },
      });
    },
    //清除全选
    clearSelectAll() {
      this.$refs.table.selectAll(false);
    },
    //选择当前
    changeSelect(e) {
      this.selectList = e;
      this.selectCount = e.length;
    },
    //删除所有
    delAll() {
      if (this.selectCount <= 0) {
        this.$Message.warning("您还未选择要删除的数据");
        return;
      }
      this.$Modal.confirm({
        title: "确认删除",
        content: "您确认要删除所选的 " + this.selectCount + " 条数据?",
        loading: true,
        onOk: () => {
          let ids = "";
          this.selectList.forEach(function (e) {
            ids += e.id + ",";
          });
          ids = ids.substring(0, ids.length - 1);
          deleteHost(ids).then((res) => {
            this.$Modal.remove();
            if (res.success) {
              this.$Message.success("删除成功");
              this.clearSelectAll();
              this.getHostList();
              this.updateAllService();
            }
          });
        },
      });
    },
  },
  mounted() {
    //初始化请求
    this.init();
  },
};
</script>
