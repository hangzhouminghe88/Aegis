<template>
  <Card>
    <header>
      <a class="back-title ivu-icon" @click="$emit('close')">
        <Icon type="ios-arrow-back"></Icon>
        <i style="font-style: normal">返回</i>
      </a>
      <form class="form margin-left-10">
        <a-form-item v-model="searchForm.name" label="类别" placeholder="请输入大类或者小类"></a-form-item>
        <a-form-item type="slot">
          <slot>
            <Button type="primary" icon="ios-search" class="margin-right-10" @click="search">搜索</Button>
            <Button class="margin-right-10" @click="reset">重置</Button>
            <Button icon="md-refresh" class="margin-right-10" @click="getAlertsResultList">刷新</Button>
            <Button type="error" icon="ios-trash-outline" @click="handleBatch">批量删除</Button>
          </slot>
        </a-form-item>
      </form>
    </header>
    <section>
      <Row class="margin-bottom-10">
        <Table :columns="columns"
               border
               :loading="loading"
               type='selection'
               @on-selection-change="handleSelect"
               :data="data"></Table>
      </Row>
      <Row type="flex" justify="end" class="page">
          <Page
          show-size
          show-total
          size="small"
          show-elevator
          :total="total"
          :current="pageIndex"
          @on-page-size-change="handleSizeChange"
          @on-change="handleCurrentChange"
          :page-size="pageSize"
          :page-size-opts="[10,20,50,100]"></Page>
      </Row>
    </section>
    <Modal  v-model="visiable"
            :mask-closable="false"
            :title="formType === 'add' ? '注册新故障' : '编辑故障处理结果'">
      <Form label-position="left" :label-width="80" ref="form" class="fixed-add" :model="form" :rules="rules">
        <FormItem label="大类" prop="category">
          <Input v-model="form.category"/>
        </FormItem>
        <FormItem label="小类" prop="subCategory">
          <Input v-model="form.subCategory"/>
        </FormItem>
        <FormItem label="描述" prop="description">
          <Input type="textarea" v-model="form.description"/>
        </FormItem>
      </Form >
      <template slot="footer">
        <Button @click="visiable = false">取消</Button>
        <Button type="primary" @click="confirm">确定</Button>
      </template>
    </Modal>
  </Card>
</template>

<script>
  import AFromItem from '@views/my-components/form/FormItem';
  import { getAlertsResultList, alertsResultEdit, alertsResultSave, alertsDelete } from '@/api/index';
  export default {
    name: "AlertsResults",
    components: {
      "a-form-item": AFromItem,
    },
    //接收父组件传的属性
    props: {
      param: {
        type: Object,
        default: () => {
          return {}
        }
      }
    },
    data() {
      const _this = this;
      return {
        visiable: false,
        formType: '',
        form: {
          category: '',
          subCategory: '',
          description: ''
        },
        rules: {
          category: [
            {required: true, message: '阈值最少需要4个字符', trigger: 'blur', min:  4}
          ],
          subCategory: [
            {required: true, message: '阈值最少需要4个字符', trigger: 'blur', min:  4}
          ]
        },
        //搜索model
        searchForm: {
          name: '',
        },
        //当前页
        pageIndex: 1,
        //当前页码
        pageSize: 10,
        //总条数
        total: 0,
        //是否有遮罩效果
        loading: false,
        //表格数据
        data: [],
        //多选列表
        selectList: [],
        //更新的id
        editId: null,
        //表格列
        columns: [
          {
            type: 'selection',
            width: 60,
            align: 'center'
          },
          {
            title: 'id',
            key: 'id'
          },
          {
            title: 'gmt_create',
            key: 'gmtCreate'
          },
          {
            title: 'alert_id',
            key: 'alertId'
          },
          {
            title: 'kind',
            key: 'kind'
          },
          {
            title: 'sub_kind',
            key: 'subKind'
          },
          {
            title: 'fixed_by',
            key: 'fixedBy'
          },
          {
            title: 'fixed_info',
            key: 'fixedInfo'
          },
          {
            title: 'operate',
            width: 300,
            render: (h, params) => {
              return h('span',{
                style: {
                  textAlign: 'center'
                }
              }, [
                h('Button', {
                   props: {
                     size: 'small',
                     type: 'primary',
                     icon: 'ios-add'
                   },
                  on: {
                     click: () => {
                       _this.formType = 'add';
                       _this.editId = null;
                       _this.form = {
                         category: '',
                         subCategory: '',
                         description: ''
                       }
                       _this.visiable = true;
                     }
                  },
                  class: 'margin-right-10'
                }, '新增'),
                h('Button', {
                  props: {
                    size: 'small',
                    type: 'warning',
                    icon: 'ios-create-outline'
                  },
                  on: {
                    click: () => {
                      _this.formType = 'edit';
                      _this.form = {
                        category: params.row.kind,
                        subCategory: params.row.subKind,
                        description: params.row.fixedInfo
                      }
                      _this.editId = params.row.id;
                      _this.visiable = true;
                    }
                  },
                  class: 'margin-right-10'
                }, "修改"),
                h('Button', {
                  props: {
                    size: 'small',
                    type: 'error',
                    icon: 'ios-trash-outline'
                  },
                  on: {
                    click: () => {
                      _this.deleteAlertResult([params.row]);
                    }
                  }
                }, "删除")
              ])
            }
          }
        ],
      }
    },
    mounted() {
      const _this = this;
      _this.getAlertsResultList();
    },
    methods: {
      //翻页触发事件
      handleCurrentChange(index) {
        const _this = this;
        _this.pageIndex = 1;
        _this.getAlertsResultList();
      },
      //改变页码触发事件
      handleSizeChange(size) {
        const _this = this;
        _this.pageSize = size;
        _this.getAlertsResultList();
      },
      getAlertsResultList() {
        const _this = this,
          params = {
            alertId: _this.param.alertId,
            pageNumber: _this.pageIndex,
            pageSize: _this.pageSize,
            sort: 'gmt_create',
            order: 'asc',
            kind: _this.searchForm.name.trim()
          };
        _this.loading = true;
        getAlertsResultList(params).then((res) => {
          if(res.success) {
            _this.data = res.result.records;
            _this.total = res.result.total;
          }
          _this.loading =  false;
        }).catch(() => _this.loading = false);
      },
      //确定触发
      confirm() {
        const _this = this;
        let params = {
          kind: _this.form.category,
          subKind: _this.form.subCategory,
          fixedInfo: _this.form.description,
          alertId: _this.param.id,
          fixedBy: JSON.parse(localStorage.getItem("userInfo")).username ? JSON.parse(localStorage.getItem("userInfo")).username : ''
        };
        if(_this.formType === 'add') {
          _this.$refs.form.validate(valid => {
            if(valid) {
              alertsResultSave(params).then(res => {
                if(res.success === true) {
                  _this.$Message.warning("添加成功!");
                  _this.getAlertsResultList();
                }
              });
              _this.visiable = false;
            }
          })
        }else{
          _this.$refs.form.validate(valid => {
            if(valid) {
              alertsResultEdit({...params, id: _this.editId}).then(res => {
                if(res.success === true) {
                  _this.$Message.warning("修改成功!");
                  _this.getAlertsResultList();
                }
                _this.visiable = false;
              });
            }
          })
        }
      },
      deleteAlertResult(param) {
        const _this = this;
        _this.$Modal.confirm({
          title: '删除',
          content: `确认要删除以下${param.length}条此故障结果吗?`,
          type: 'warning',
          onOk: () => {
            alertsDelete({
              ids: param.map(item => {
                return item.id
              }).join(',')
            }).then(() => {
              _this.getAlertsResultList();
            });
          },
          onClose: () => {

          }
        })
      },
      handleSelect(selection) {
        const _this = this;
        _this.selectList = selection;
      },
      //批量删除
      handleBatch() {
        const _this = this;
        if(_this.selectList.length <=0) {
          _this.$Message.warning('请选择要删除的故障!');
          return;
        }
        _this.deleteAlertResult(_this.selectList)
      },
      //重置
      reset() {
        const _this = this;
        _this.searchForm.name = '';
        _this.pageIndex = 1;
        _this.getAlertsResultList();
      },
      //搜索
      search() {
        const _this = this;
        _this.pageIndex = 1;
        _this.getAlertsResultList();
      }
    }
  }
</script>

<style scoped>

</style>
