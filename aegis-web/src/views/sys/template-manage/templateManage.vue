<style lang="less">
@import "../../../styles/table-common.less";
@import "./templateManage.less";
</style>
<template>
  <div class="search">
    <Card>
      <Row class="operation">
        <form class="margin-bottom-20">
          <a-form-item v-model="searchForm.name" label="名称" placeholder="请输入名称"></a-form-item>
          <a-form-item type="slot">
            <slot>
              <Button
                  @click="handleSearch"
                  type="primary"
                  icon="ios-search"
                  class="margin-right-10"
              >搜索
              </Button>
              <Button @click="handleReset" class="margin-right-10">重置</Button>
            </slot>
          </a-form-item>
        </form>
        <Button @click="add" type="primary" icon="md-add">添加</Button>
        <Button @click="delAll" icon="md-trash">批量删除</Button>
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
        <FormItem label="名称" prop="name">
          <Input v-model="form.name"/>
        </FormItem>
        <FormItem label="简称" prop="sname">
          <Input v-model="form.sname" />
        </FormItem>
        <FormItem label="类型" prop="kind">
          <Select v-model="form.kind" placeholder="请选择" clearable style="width: 200px">
            <Option value="oracle">oracle</Option>
            <Option value="mysql">mysql</Option>
          </Select>
        </FormItem>
        <FormItem label="来源" prop="type">
          <Input v-model="form.type" />
        </FormItem>
        <FormItem label="排序" prop="sortBy">
          <Input v-model="form.sortBy" />
        </FormItem>
        <FormItem label="标签" prop="tags">
          <Input v-model="form.tags"/>
        </FormItem>
        <FormItem label="级别" prop="level">
          <Input v-model="form.level"/>
        </FormItem>
      </Form>
      <div slot="footer">
        <Button type="text" @click="cancel">取消</Button>
        <Button type="primary" :loading="submitLoading" @click="submit">提交</Button>
      </div>
    </Modal>
  </div>
</template>

<script>
import {
  getTemplateList,
  addTemplate,
  editTemplate,
  deleteTemplate
} from "@/api/index";
import util from "@/libs/util.js";
import AFromItem from '@views/my-components/form/FormItem';

export default {
  name: "template-manage",
  components: {
    "a-form-item": AFromItem
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
      modalVisible:false,
      detailModalTitle: "",
      detailModalVisible:false,
      currentTemplate:null,
      searchForm: {
        name:''
      },
      form: {
        name: "",
        description: ""
      },
      formValidate: {
        name: [{ required: true, message: "名称不能为空",trigger: "blur" }],
        sname: [{ required: true, message: "简称不能为空",trigger: "blur" }],
        kind: [{ required: true, message: "类型不能为空",type:"string",trigger: "blur" }],
        type: [{ required: true, message: "来源不能为空",trigger: "blur" }],
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
          title: "名称",
          key: "name",
          sortable: false,
          width: 150,
          tooltip: true,
          ellipsis: true
        },
          {
              title: "简称",
              key: "sname",
              width: 150,
              sortable: false
          },
          {
              title: "类型",
              key: "kind",
              width: 150,
              sortable: false
          },
          {
              title: "来源",
              key: "type",
              width: 150,
              sortable: false
          },
          {
              title: "标签",
              key: "tags",
              sortable: false,
              width: 100
          },
          {
              title: "级别",
              key: "level",
              width: 100,
              sortable: false
          },
          {
              title: "排序",
              key: "sortBy",
              width: 100,
              sortable: false
          },
        {
          title: "操作",
          key: "action",
          align: "center",
          width: 200,
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
                  }
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
      dataType: 0
    };
  },
  methods: {
    init() {
      this.getTemplateList();
    },
    changePage(v) {
      this.pageNumber = v;
      this.getTemplateList();
      this.clearSelectAll();
    },
    changePageSize(v) {
      this.pageSize = v;
      this.getTemplateList();
    },
    changeSort(e) {
      this.sortColumn = e.key;
      this.sortType = e.order;
      if (e.order == "normal") {
        this.sortType = "";
      }
      this.getTemplateList();
    },
    getTemplateList() {
      this.loading = true;
      let params = {
        pageNumber: this.pageNumber,
        pageSize: this.pageSize,
        sort: this.sortColumn,
        order: this.sort,
        name: this.searchForm.name.trim()
      };
      getTemplateList(params).then(res => {
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

            addTemplate(this.form).then(res => {
              this.submitLoading = false;
              if (res.success) {
                this.$Message.success("操作成功");
                this.getTemplateList();
                this.modalVisible = false;
              }
            });
          } else {
            this.submitLoading = true;
            editTemplate(this.form).then(res => {
              this.submitLoading = false;
              if (res.success) {
                this.$Message.success("操作成功");
                this.getTemplateList();
                this.modalVisible = false;
              }
            });
          }
        }
      });
    },
    add() {
      this.modalType = 0;
      this.modalTitle = "添加模板信息";
      this.$refs.form.resetFields();
      this.form={
          sortBy:0,
          level:0
      }
      this.modalVisible = true;
    },
    edit(v) {
      this.modalType = 1;
      this.modalTitle = "编辑模板信息";
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
        content: "您确认要删除模板 " + v.name + " ?",
        loading: true,
        onOk: () => {
          deleteTemplate(v.id).then(res => {
            this.$Modal.remove();
            if (res.success) {
              this.$Message.success("删除成功");
              this.getTemplateList();
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
          deleteTemplate(ids).then(res => {
            this.$Modal.remove();
            if (res.success) {
              this.$Message.success("删除成功");
              this.clearSelectAll();
              this.getTemplateList();
            }
          });
        }
      });
    },
    //搜索
    handleSearch() {
      const _this = this;
      _this.getTemplateList();
    },
    handleReset() {
      const _this = this;
      this.searchForm.name = "";
      _this.getTemplateList();
    },
  },
  mounted() {
    this.init();
  }
};
</script>
