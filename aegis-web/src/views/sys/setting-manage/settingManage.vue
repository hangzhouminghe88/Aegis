<style lang="less">
@import "./settingManage.less";
</style>
<template>
  <div>
    <Card>
      <Tabs v-model="tabName" :animated="false" style="overflow: visible">
        <TabPane label="短信配置" name="sms">
          <div style="position:relative">
            <Form 
              ref="smsForm"
              :model="sms" 
              :label-width="150"
              label-position="right"
              :rules="smsValidate"
              > 
              <FormItem label="服务提供商：" prop="serviceName">
                <Select v-model="sms.serviceName" placeholder="请选择" style="width: 200px">
                  <Option value="ALI_SMS">阿里云</Option>
                </Select>
              </FormItem>
              <FormItem label="accessKey：" prop="accessKey">
                <Input type="text" v-model="sms.accessKey" placeholder="请输入" style="width: 350px"/>
              </FormItem>
              <FormItem label="secretKey：" prop="secretKey">
                <Tooltip placement="right" content="无法编辑时请先点击查看图标">
                    <Input class="input-see" type="text" v-model="sms.secretKey" :readonly="!changedSmsSK" @on-click="seeSecret(sms.serviceName)" icon="ios-eye" placeholder="请输入" style="width: 350px"/>
                </Tooltip>
              </FormItem>
              <FormItem label="短信签名：" prop="signName">
                <Input type="text" v-model="sms.signName" placeholder="请输入短信签名，例如XBoot" style="width: 350px"/>
              </FormItem>
              <FormItem label="使用场景：" prop="type">
                <Select v-model="sms.type" @on-change="changeSmsType" placeholder="请选择" style="width: 350px">
                  <Option value="-1">通用</Option>
                  <Option value="0">注册</Option>
                  <Option value="1">登录</Option>
                  <Option value="2">修改绑定手机号</Option>
                  <Option value="3">修改密码</Option>
                  <Option value="4">找回密码</Option>
                </Select>
              </FormItem>
              <FormItem label="模版CODE：" prop="templateCode">
                <Input type="text" v-model="sms.templateCode" placeholder="请输入场景对应短信模版CODE，例如SMS_123456789" style="width: 350px"/>
              </FormItem>
              <FormItem>
                <Button type="primary" style="width: 100px;margin-right:5px" :loading="saveLoading" @click="saveEditSms">保存更改</Button>
              </FormItem>
            </Form>
            <Spin fix v-if="loading"></Spin>
          </div>
        </TabPane>
        <TabPane label="邮件配置" name="email">
          <div style="position:relative">
            <Form 
              ref="emailForm"
              :model="email" 
              :label-width="150"
              label-position="right"
              :rules="emailValidate"
              > 
              <FormItem label="邮箱服务器：" prop="host">
                <Input type="text" v-model="email.host" placeholder="请输入邮箱服务器Host，例如QQ邮箱为smtp.qq.com" style="width: 350px"/>
              </FormItem>
              <FormItem label="发送邮箱账号：" prop="username">
                <Input type="text" v-model="email.username" placeholder="请输入发送者Email账号" style="width: 350px"/>
              </FormItem>
              <FormItem label="邮箱授权码：" prop="password">
                <Tooltip placement="right" content="无法编辑时请先点击查看图标">
                    <Input class="input-see" type="text" v-model="email.password" :readonly="!changedEmailPass" @on-click="seeSecret('EMAIL_SETTING')" icon="ios-eye" placeholder="请输入邮箱授权码，进入邮箱-设置中可找到" style="width: 350px"/>
                </Tooltip>
              </FormItem>
              <FormItem>
                <Button type="primary" style="width: 100px;margin-right:5px" :loading="saveLoading" @click="saveEditEmail">保存更改</Button>
              </FormItem>
            </Form>
            </Form>
            <Spin fix v-if="loading"></Spin>
          </div>
        </TabPane>
      </Tabs>
    </Card>
  </div>
</template>

<script>
import {
  seeSecretSet,
  checkOssSet,
  getOssSet,
  editOssSet,
  getSmsSet,
  editSmsSet,
  getSmsTemplateCode,
  getEmailSet,
  editEmailSet,
  getVaptchaSet,
  editVaptchaSet,
  getOtherSet,
  editOtherSet
} from "@/api/index";
export default {
  name: "setting-manage",
  data() {
    return {
      tabName: "sms",
      loading: false, // 表单加载状态
      saveLoading: false,
      oss: {
        serviceName: ""
      },
      sms: {
        serviceName: "ALI_SMS"
      },
      changedSmsSK: false, // 是否修改短信的secrectKey
      email: {},
      changedEmailPass: false, // 是否修改邮件密码
      errorMsg: "",
      errorMsg2: "",
      vaptcha: {},
      changedVaptchaSK: false, // 是否修改Vaptcha的secrectKey
      other: {},
      smsValidate: {
        // 表单验证规则
        serviceName: [{ required: true, message: "请选择", trigger: "blur" }],
        accessKey: [{ required: true, message: "不能为空", trigger: "blur" }],
        secretKey: [{ required: true, message: "不能为空", trigger: "blur" }],
        signName: [{ required: true, message: "不能为空", trigger: "blur" }],
        type: [{ required: true, message: "不能为空", trigger: "blur" }],
        templateCode: [{ required: true, message: "不能为空", trigger: "blur" }]
      },
      emailValidate: {
        // 表单验证规则
        host: [{ required: true, message: "不能为空", trigger: "blur" }],
        username: [{ required: true, message: "不能为空", trigger: "blur" }],
        password: [{ required: true, message: "不能为空", trigger: "blur" }]
      }
    };
  },
  methods: {
    init() {
      this.initSmsSet();
      this.initEmailSet();
    },
    initSmsSet() {
      this.loading = true;
      getSmsSet(this.sms.serviceName).then(res => {
        this.loading = false;
        if (res.result) {
          // 转换null为""
          for (let attr in res.result) {
            if (res.result[attr] === null) {
              res.result[attr] = "";
            }
          }
          this.sms = res.result;
          this.sms.type = "-1";
          this.changeSmsType(this.sms.type);
        } else {
          this.changedSmsSK = true;
        }
      });
    },
    changeSmsType(v) {
      getSmsTemplateCode(v).then(res => {
        if (res.success) {
          this.sms.templateCode = res.result;
        }
      });
    },
    initEmailSet() {
      this.loading = true;
      getEmailSet().then(res => {
        this.loading = false;
        if (res.result) {
          // 转换null为""
          for (let attr in res.result) {
            if (res.result[attr] === null) {
              res.result[attr] = "";
            }
          }
          this.email = res.result;
        } else {
          this.changedEmailPass = true;
        }
      });
    },
    seeSecret(name) {
      if (!name) {
        return;
      }
      seeSecretSet(name).then(res => {
        if (res.success) {
          if (this.tabName == "oss") {
            this.oss.secretKey = res.result;
            this.changedOssSK = true;
          } else if (this.tabName == "sms") {
            this.sms.secretKey = res.result;
            this.changedSmsSK = true;
          } else if (this.tabName == "email") {
            this.email.password = res.result;
            this.changedEmailPass = true;
          } else if (this.tabName == "vaptcha") {
            this.vaptcha.secretKey = res.result;
            this.changedVaptchaSK = true;
          }
        }
      });
    },
    saveEditSms() {
      this.$refs.smsForm.validate(valid => {
        if (valid) {
          this.saveLoading = true;
          this.sms.changed = this.changedSmsSK;
          editSmsSet(this.sms).then(res => {
            this.saveLoading = false;
            if (res.success) {
              this.$Message.success("保存成功");
            }
          });
        }
      });
    },
    saveEditEmail() {
      this.$refs.emailForm.validate(valid => {
        if (valid) {
          this.saveLoading = true;
          this.email.changed = this.changedEmailPass;
          editEmailSet(this.email).then(res => {
            this.saveLoading = false;
            if (res.success) {
              this.$Message.success("保存成功");
            }
          });
        }
      });
    },
  },
  mounted() {
    let name = this.$route.query.name;
    if (name) {
      this.tabName = name;
    }
    this.init();
  }
};
</script>