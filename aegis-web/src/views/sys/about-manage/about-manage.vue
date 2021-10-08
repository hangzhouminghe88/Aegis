<style lang="less" scoped>
@import "~@views/sys/about-manage/aboutManage.less";
</style>
<template>
    <div class="container">
        <div class="host_model">{{info && info.model}}</div>
        <div class="buttons" @click="modalVisible = true"><a class="button reload" ></a> <a class="button" style="margin-right: 30px; color: rgb(60, 115, 185);">上传许可证</a></div>
        <div class="basic">
            <div class="basic_license_info">
                <div class="basic_license_left">
                    <div class="basic_license_icon enterprise" style="width: 141px; height: 115px; margin: 0px auto;"></div>
                    <div class="basic_license_title">
                        <h4 class="basic_license_name">{{productType}} v3.6
                            <span class="basic_license_status valid" v-if="info && !info.expired">有效</span>
                            <span class="basic_license_status expired" v-if="info && info.expired">已过期</span>
                            <span class="basic_license_status valid" v-if="info && info.isDedup==1">重删</span>
                        </h4>
                    </div>
                    <div class="basic_license_details">
                        <div class="basic_license_detail">
                            <span class="basic_license_detail-num">{{info ? info.instanceCount :0}}</span>
                            <span class="basic_license_detail-title">实例数量</span>
                        </div>
                    </div>
                </div>
                <div class="basic_license_right">
                    <div class="basic_license_right-inner">
                        <div class="detail_items">
                            <div class="detail_item">
                                <span class="detail_item-name">授权用户:</span>
                                <span class="detail_item-value">{{info && info.customerName}}</span>
                            </div>
                            <div class="detail_item">
                                <span class="detail_item-name">签发时间:</span>
                                <span class="detail_item-value">{{info && info.signDate|format}}</span>
                            </div>
                            <div class="detail_item">
                                <span class="detail_item-name">请求码:</span>
                                <span class="detail_item-value license_request">
                                    {{requestCode}}
                                </span>
                                <a style="cursor: pointer; margin-right: 10px;">
                                    <img src="~assets/copy.svg" class="copy" @click="copy"></a> 
																		<a :href="`/api/license/downloadRequestCode`">
                                    <img src="~assets/download.svg" class="download">
                                </a>
                            </div>
                            <div class="detail_item">
                                <span class="detail_item-name">到期时间:</span>
                                <span class="detail_item-value" v-if="info && info.type!=1">{{info && info.expireDate|format}}</span>
                                <span class="detail_item-value" v-if="info && info.type==1">永久有效</span>
                            </div>
                            <div class="detail_item">
                                <span class="detail_item-name">服务期限:</span>
                                <span class="detail_item-value">{{info && info.serviceDate|format}}</span>
                            </div>
                        </div>
                        <p>如您需要升级到其它版本或更新许可证，请将您的请求码和升级需求发送电子邮件至
                            <a href="mailto:support@mingheinfo.com" style="color: rgb(60, 115, 185); text-decoration: none;">support@mingheinfo.com</a>，我们将尽快与您联系！</p>
                    </div>
                </div>
            </div>
            <div class="basic_license_introduction">
                <div class="license_intro_item">
                    <span class="check_icon"></span>
                    <span class="license_intro_item-text">可使用明和AEGIS平台软件的全部功能</span>
                </div>
                <div class="license_intro_item">
                    <span class="check_icon"></span>
                    <span class="license_intro_item-text">付费授权使用</span>
                </div>
                <div class="license_intro_item">
                    <span class="check_icon"></span>
                    <span class="license_intro_item-text">在许可证授权期限内使用</span>
                </div>
                <div class="license_intro_item">
                    <span class="check_icon"></span>
                    <span class="license_intro_item-text">可管理的有限客户端数量</span>
                </div>
                <div class="license_intro_item">
                    <span class="check_icon"></span>
                    <span class="license_intro_item-text">在售后服务期内可获得官方售后技术支持服务</span>
                </div>
                <div class="license_intro_item">
                    <span class="check_icon"></span>
                    <span class="license_intro_item-text">该版本适于企业生产环境部署</span>
                </div>
            </div>
        </div>
        <div class="modules">
            <div class="module disaster_backup" style="margin-right: 20px;" v-if="info &&  info.fileRepCount>0">
                <span class="module_count">许可数量：<b>{{info.fileRepCount}}</b> 条</span>
                <div class="module_title">
                    <h4 class="module_title-name">文件复制
                        <div class="module_title-status valid" v-if="!info.expired">有效</div>
                        <div class="module_title-status expired" v-if="info.expired">已过期</div>
                    </h4>
                    <div class="module_title-intro">文件复制策略条数限制</div>
                </div>
            </div>
            <div class="module disaster_backup" style="margin-right: 20px;" v-if="info && info.oracleRepCount>0">
                <span class="module_count">许可数量：<b>{{info.oracleRepCount}}</b> 条</span>
                <div class="module_title">
                    <h4 class="module_title-name">Oracle复制
                        <div class="module_title-status valid" v-if="!info.expired">有效</div>
                        <div class="module_title-status expired" v-if="info.expired">已过期</div>
                    </h4>
                    <div class="module_title-intro">Oracle复制策略条数限制</div>
                </div>
            </div>
            <div class="module disaster_backup" style="margin-right: 20px;" v-if="info && info.sqlserverRepCount>0">
                <span class="module_count">许可数量：<b>{{info.sqlserverRepCount}}</b> 条</span>
                <div class="module_title">
                    <h4 class="module_title-name">SqlServer复制
                        <div class="module_title-status valid" v-if="!info.expired">有效</div>
                        <div class="module_title-status expired" v-if="info.expired">已过期</div>
                    </h4>
                    <div class="module_title-intro">SqlServer复制策略条数限制</div>
                </div>
            </div>
            <div class="module disaster_backup" v-if="info && info.vmwareRepCount>0">
                <span class="module_count">许可数量：<b>{{info.vmwareRepCount}}</b> 条</span>
                <div class="module_title">
                    <h4 class="module_title-name">Vmware复制
                        <div class="module_title-status valid" v-if="!info.expired">有效</div>
                        <div class="module_title-status expired" v-if="info.expired">已过期</div>
                    </h4>
                    <div class="module_title-intro">Vmware复制策略条数限制</div>
                </div>
            </div>
        </div>
        <Modal
                v-model="modalVisible"
                title="上传许可证"
                @on-ok="ok"
                @on-cancel="cancel" class-name="vertical-center-modal" width="600">
            <Upload
                    :headers="accessToken"
                    :on-success="uploadSuccess"
                    type="drag"
                    action="/api/license/upload">
                <div style="padding: 50px 0">
                    <Icon type="ios-cloud-upload" size="52" style="color: #3399ff"></Icon>
                    <p>选择或拖放文件到这里</p>
                </div>
            </Upload>
        </Modal>
    </div>


</template>

<script>
// import{
//     constAgentType
// } from "@/api/global";
import {
    getLicenseRequestCode,
        getLicenseInfo,
        getLicenseText
} from "@/api/index";

import {
		formatDate,
		copyText
} from "@/libs/date";

import Vue from 'vue';
export default {
  name: "about-manage",
  data() {
    return {
      loading: true,
      operationLoading: false,
      modalVisible: false,
      requestCode:"",
      productType:"",
      info:null,
      accessToken: {},
      submitLoading: false
    };
  },
  methods: {
    copy(){
				const _this = this;
				copyText(_this.requestCode);
        this.$Notice.success({
            title: "信息",
            desc: "请求码已复制到粘贴板",
            duration: 5
				});
    },
    init() {
        this.accessToken = {
            accessToken: this.getStore("accessToken")
        };

        this.loading = true;
        getLicenseRequestCode({}).then(res => {
            this.loading = false;
            if (res.success === true) {
                this.requestCode = res.result;
            }
            else{
                let message = res.msg;
                if(res.success==undefined)
                    message = res;
                this.$Notice.error({
                    title: "错误信息",
                    desc: message,
                    duration: 5
                });
            }
        });

        getLicenseInfo({}).then(res => {
            this.loading = false;
            if (res.success === true) {
                let data = res.result;

                if(data==null) {
                    return;
                }

                if (data.type == 1) {
                    this.productType = '付费版';
                    data.expired = false;
                }
                else {
                    this.productType = '试用版';
                    data.expired = this.isExpired(data.expireDate);
                }
                this.info = data;

            }
            else{
                let message = res.msg;
                if(res.success==undefined)
                    message = res;
                this.$Notice.error({
                    title: "错误信息",
                    desc: message,
                    duration: 5
                });
            }
        });
    },
      uploadSuccess(res, file) {
          if (res.success === true) {
              let data = res.result;

              if (data.type == 1) {
                  this.productType = '付费版';
                  data.expired = false;
              }
              else {
                  this.productType = '试用版';
                  data.expired = this.isExpired(data.expireDate);
              }

              this.info = data;

          } else {
              let message = res.msg;
              if(res.success==undefined)
                  message = res;
              this.$Notice.error({
                  title: "错误信息",
                  desc: message,
                  duration: 5
              });
          }
      },

      ok(){

      },

      cancel(){

      },
      isExpired:function (dt) {
          let expiredDate = new Date(dt);
          let now = new Date();
          if(now.getTime()>expiredDate.getTime())
              return true;

          return false;
      }

  },
  mounted() {
    this.init();
  },
    filters: {
        format: function(value) {
            if(value==undefined || value==null || value=='')
                return '';
            return value.replace("T"," ").substring(0,10);
        },
        getAgentName:function(value){
            for(let o in constAgentType)
            {
                if(constAgentType[o].value==value)
                    return constAgentType[o].name;
            }

            return '';
        },
        getAgentDesc:function(value){
            for(let o in constAgentType)
            {
                if(constAgentType[o].value==value)
                    return constAgentType[o].desc;
            }

            return '';
        }
    }
};
</script>
