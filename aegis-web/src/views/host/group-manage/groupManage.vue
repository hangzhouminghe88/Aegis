<style lang="less" scoped>
@import "../../../styles/table-common.less";
@import "./groupManage.less";
</style>
<template>
  <div class="search">
    <Card>
      <Row class="operation">
        <Button @click="add" type="primary" icon="md-add">添加主机群组</Button>
        <Button @click="delAll" icon="md-trash" v-permission="['ROLE_ADMIN', 'ROLE_TEST']">批量删除</Button>
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
          :page-size-opts="[10,20,50]"
          size="small"
          show-total
          show-elevator
          show-sizer
        ></Page>
      </Row>
    </Card>

    <!-- 编辑 -->
    <Modal :title="modalTitle" v-model="modalVisible" :mask-closable="false" :width="500">
      <Form ref="form" :model="form" :label-width="100" :rules="formValidate">

        <FormItem label="群组名称" prop="name">
          <Input v-model="form.name" />
        </FormItem>
        <FormItem label="类型信息" prop="type">
          <Select v-model="form.type" placeholder="请选择" clearable style="width: 200px">
            <Option :value="0">oracle</Option>
            <Option :value="1">mysql</Option>
          </Select>
        </FormItem>
        <FormItem label="业务名称" prop="name">
          <Input v-model="form.hostNames" />
        </FormItem>
        <FormItem label="群组描述" prop="description">
          <Input v-model="form.description" type="textarea"/>
        </FormItem>
        <FormItem label="监控状态" prop="state">
          <Select v-model="form.state" placeholder="请选择" clearable style="width: 200px">
            <Option value="open">开启</Option>
            <Option value="close">关闭</Option>
          </Select>
        </FormItem>
      </Form>
      <div slot="footer">
        <Button type="text" @click="cancel">取消</Button>
        <Button type="primary" :loading="submitLoading" @click="submit"  v-permission="['ROLE_ADMIN', 'ROLE_TEST']">提交</Button>
      </div>
    </Modal>

  </div>
</template>

<script>
import {
  getHostGroupList,
  addHostGroup,
  editHostGroup,
  deleteHostGroup
} from "@/api/index";
import util from "@/libs/util.js";


export default {
  name: "host-group-manage",
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
      modalVisible:false,
      detailModalTitle: "",
      detailModalVisible:false,
      form: {
        name: "",
        description: "",
        hostNames: "",
        state: 'open'
      },
      formValidate: {
        name: [{ required: true, message: "群组名称不能为空",trigger: "blur" }],
        type: [{ required: true, message: "类型信息不能为空",type:"number",trigger: "blur" }],
        hostNames: [{ required: true, message: "业务名称不能为空",type:"number",trigger: "blur" }],
        state: [{ required: true, message: "状态不能为空", trigger: "blur" }]
      },
      submitLoading: false,
      selectList: [],
      selectCount: 0,
      columns: [
        {
          type: "selection",
          width: 60,
          align: "center"
        },
        {
          title: "群组名称",
          key: "name",
          sortable: false
        },
          {
              title: "类型",
              key: "type",
              sortable: false,
              render: (h, params) => {
                  if(params.row.type==0)
                      return h("span","oracle")

                  return h("span","mysql")
              }
          },
          {
              title: "群组描述",
              key: "description",
              sortable: false
          },
          {
              title: "监控状态",
              key: "state",
              sortable: false,
              render: (h, params) => {
                return h('table-state', {
                  props: {
                    state: params.row.state,
                    content: params.row.state
                  }
                })
              }
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
                    size: "small"
                  },
                  style: {
                    marginRight: "5px"
                  },
                  directives: [
                    {
                      name: "permission",
                      value: ['ROLE_ADMIN', 'ROLE_TEST'],
                    },
                  ],
                  on: {
                    click: () => {
                      this.edit(params.row);
                    }
                  }
                },
                "编辑"
              ),
              h(
                "Button",
                {
                  props: {
                    type: "error",
                    size: "small"
                  },
                  on: {
                    click: () => {
                      this.remove(params.row);
                    }
                  },
                  directives: [
                    {
                      name: "permission",
                      value: ['ROLE_ADMIN', 'ROLE_TEST'],
                    },
                  ],
                },
                "删除"
              )
            ]);
          }
        }
      ],
      data: [],
      pageNumber: 1,
      pageSize: 10,
      total: 0,
      selectAllFlag: false,
      dataType: 0,
      groupList:[]
    };
  },
  methods: {
    init() {
      this.getHostGroupList();
    },
    changePage(v) {
      this.pageNumber = v;
      this.getHostGroupList();
      this.clearSelectAll();
    },
    changePageSize(v) {
      this.pageSize = v;
      this.getHostGroupList();
    },
    changeSort(e) {
      this.sortColumn = e.key;
      this.sortType = e.order;
      if (e.order == "normal") {
        this.sortType = "";
      }
      this.getHostGroupList();
    },
    getHostGroupList() {
      this.loading = true;
      let params = {
        pageNumber: this.pageNumber,
        pageSize: this.pageSize,
        sort: this.sortColumn,
        order: this.sort
      };
      getHostGroupList(params).then(res => {
        this.loading = false;
        if (res.success) {
          this.data = res.result.records;
          this.total = res.result.total;
        }
      });
    },
    cancel() {
      this.modalVisible = false;
    },
    submit() {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (this.modalType == 0) {
            // 添加
            this.submitLoading = true;

            addHostGroup(this.form).then(res => {
              this.submitLoading = false;
              if (res.success) {
                this.$Message.success("操作成功");
                this.getHostGroupList();
                this.modalVisible = false;
              }
            });
          } else {
            this.submitLoading = true;
            editHostGroup(this.form).then(res => {
              this.submitLoading = false;
              if (res.success) {
                this.$Message.success("操作成功");
                this.getHostGroupList();
                this.modalVisible = false;
              }
            });
          }
        }
      });
    },
    add() {
      this.modalType = 0;
      this.modalTitle = "添加群组信息";
      this.$refs.form.resetFields();
      this.form={
          type:0,
          state: 'open'
      }
      this.modalVisible = true;
    },
    edit(v) {
      this.modalType = 1;
      this.modalTitle = "编辑群组信息";
      this.$refs.form.resetFields();
      // 转换null为""
      for (let attr in v) {
        if (v[attr] == null) {
          v[attr] = "";
        }
      }
      let str = JSON.stringify(v);
      let info = JSON.parse(str);

      this.form = info;
      this.modalVisible = true;
    },
    remove(v) {
      this.$Modal.confirm({
        title: "确认删除",
        content: "您确认要删除群组 " + v.name + " ?",
        loading: true,
        onOk: () => {
          deleteHostGroup(v.id).then(res => {
            this.$Modal.remove();
            if (res.success) {
              this.$Message.success("删除成功");
              this.getHostGroupList();
            }
          });
        }
      });
    },

    clearSelectAll() {
      this.$refs.table.selectAll(false);
    },
    changeSelect(e) {
      this.selectList = e;
      this.selectCount = e.length;
    },
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
          this.selectList.forEach(function(e) {
            ids += e.id + ",";
          });
          ids = ids.substring(0, ids.length - 1);
          deleteHostGroup(ids).then(res => {
            this.$Modal.remove();
            if (res.success) {
              this.$Message.success("删除成功");
              this.clearSelectAll();
              this.getHostGroupList();
            }
          });
        }
      });
    },
  },
  mounted() {
    this.init();
  }
};
</script>
