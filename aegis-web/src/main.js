// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import '@babel/polyfill'
import Vue from 'vue'
import ViewUI from '@/iview/index.js'
// import 'view-design/dist/styles/iview.css';
import App from './App';
import { router } from './router/index'
import store from './store'
import i18n from '@/locale'
import Icon from 'vue-awesome/components/Icon'
import "@styles/common.less"
// 按需引入awesome图标
import 'vue-awesome/icons/brands/qq'
import 'vue-awesome/icons/brands/weixin'
import 'vue-awesome/icons/brands/weibo'
import 'vue-awesome/icons/brands/github'
import { getRequest, postRequest, putRequest, deleteRequest, uploadFileRequest } from '@/libs/axios'
import { setStore, getStore, removeStore } from '@/libs/storage'
import util from '@/libs/util'
import dictUtil from '@/libs/dictUtil'
import hasPermission from '@/libs/hasPermission'
import hasRole from '@/libs/hasRole'
import VueLazyload from 'vue-lazyload'
import Socket from "@/libs/socket.js";
Vue.config.productionTip = false
Vue.use(VueLazyload, {
    error: require('./assets/img-error.png'),
    loading: require('./assets/loading2.gif')
})
Vue.use(ViewUI);
// require styles
Vue.component('icon', Icon);
Vue.use(hasPermission);
Vue.use(hasRole);
// 挂载全局使用的方法
Vue.prototype.getRequest = getRequest;
//全局post请求
Vue.prototype.postRequest = postRequest;
//全局put请求
Vue.prototype.putRequest = putRequest;
//全局delete请求
Vue.prototype.deleteRequest = deleteRequest;
//全局上传下载uploadFileRequest请求
Vue.prototype.uploadFileRequest = uploadFileRequest;
Vue.prototype.setStore = setStore;
Vue.prototype.getStore = getStore;
Vue.prototype.removeStore = removeStore;
//全局websocket请求
Vue.prototype.$socket = new Socket();
Vue.prototype.localeOkText = "确定";
Vue.prototype.localeCancelText = "取消";
/* eslint-disable no-new */
new Vue({
    el: '#app',
    router,
    store,
    i18n,
    render: h => h(App),
    data: {
        currentPageName: ''
    },
    mounted() {
        // 初始化菜单
        util.initRouter(this);
        // 初始化全局数据字典
        dictUtil.initDictData(this);
        this.currentPageName = this.$route.name;
        // 显示打开的页面的列表
        this.$store.commit('setOpenedList');
        this.$store.commit('initCachepage');
    }
})
