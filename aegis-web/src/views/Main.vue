<style lang="less">
@import "./main.less";
</style>

<template>
  <div class="main" :class="{'main-hide-text': shrink}">
    <div
      class="sidebar-menu-con menu-bar"
      :style="{width: shrink?'60px':'220px', overflow: shrink ? 'visible' : 'auto'}"
    >
      <!--左侧菜单-->
      <shrinkable-menu
        :shrink="shrink"
        @on-change="handleSubmenuChange"
        :theme="menuTheme"
        :before-push="beforePush"
        :open-names="openedSubmenuArr"
        :menu-list="menuList"
      >
        <div slot="top" class="logo-con">
          <img v-show="!shrink" src="../assets/logo2.png" key="max-logo" :style="{height:'31px'}"/>
          <img v-show="shrink" src="../assets/logo-min.png" key="min-logo" />
        </div>
      </shrinkable-menu>
    </div>
    <div class="main-header-con" :style="{paddingLeft: shrink?'60px':'220px'}">
      <div class="main-header">
        <div class="navicon-con">
          <Button
            :style="{transform: 'rotateZ(' + (this.shrink ? '-90' : '0') + 'deg)', height: '48px'}"
            type="text"
            @click="toggleClick"
          >
            <Icon type="md-menu" size="32"></Icon>
          </Button>
        </div>
        <div class="header-middle-con">
          <div class="main-breadcrumb" v-if="navType==4">
            <breadcrumb-nav :currentPath="currentPath"></breadcrumb-nav>
          </div>
          <div class="main-nav" v-if="navType==3">
            <Dropdown transfer @on-click="selectNav">
              <div style="cursor: pointer">
                {{currNavTitle}}
                <Icon type="ios-arrow-down"></Icon>
              </div>
              <DropdownMenu slot="list">
                <DropdownItem
                  v-for="(item, i) in navList"
                  :key="i"
                  :name="item.name"
                  :selected="currNav==item.name"
                >
                  <div class="nav-item">
                    <Icon :type="item.icon" :size="16" style="margin: 0 10px 3px 0"></Icon>
                    {{item.title}}
                  </div>
                </DropdownItem>
              </DropdownMenu>
            </Dropdown>
          </div>
        </div>
        <div :class="{'header-avator-con':navType!=4, 'header-avator-con nav4':navType==4}">
          <Dropdown @on-click="selectNav" class="options" v-if="navType==4">
            <Icon type="ios-apps" :size="24" class="language"></Icon>
            <DropdownMenu slot="list">
              <DropdownItem
                v-for="(item, i) in navList"
                :key="i"
                :name="item.name"
                :selected="currNav==item.name"
              >
                <div>
                  <Icon :type="item.icon" :size="14" style="margin: 0 10px 2px 0"></Icon>
                  {{item.title}}
                </div>
              </DropdownItem>
            </DropdownMenu>
          </Dropdown>
          <full-screen v-model="isFullScreen" @on-change="fullscreenChange"></full-screen>
          <Dropdown @on-click="handleLanDropdown" class="options">
            <Icon type="md-globe" :size="24" class="language"></Icon>
            <DropdownMenu slot="list">
              <DropdownItem name="zh-CN">中文</DropdownItem>
              <DropdownItem name="en-US">English</DropdownItem>
            </DropdownMenu>
          </Dropdown>
          <lock-screen></lock-screen>
          <!--<message-tip v-model="mesCount"></message-tip>-->
          <div class="user-dropdown-menu-con">
            <Row type="flex" justify="end" align="middle" class="user-dropdown-innercon">
              <Dropdown transfer trigger="hover" @on-click="handleClickUserDropdown">
                <a href="javascript:void(0)">
                  <span class="main-user-name">{{ username }}</span>
                  <Icon type="md-arrow-dropdown" />
                  <Avatar :src="avatarPath" style="background: #619fe7;margin-left: 10px;"></Avatar>
                </a>
                <DropdownMenu slot="list">
                  <!--<DropdownItem name="ownSpace">{{ $t('userCenter') }}</DropdownItem>-->
                  <DropdownItem name="changePass">{{ $t('changePass') }}</DropdownItem>
                  <DropdownItem name="about">关于</DropdownItem>
                  <DropdownItem name="loginout" divided>{{ $t('logout') }}</DropdownItem>
                </DropdownMenu>
              </Dropdown>
            </Row>
          </div>
        </div>
      </div>
      <div class="tags-con">
        <tags-page-opened :pageTagsList="pageTagsList"></tags-page-opened>
      </div>
    </div>
    <!--主页面入口-->
    <div class="single-page-con" :style="{left: shrink?'60px':'220px'}">
      <div class="single-page" @scroll="onScroll">
        <router-view></router-view>
      </div>
      <!--滚动条-->
      <div class="scrollbar" v-show="showScrollbar" :style="{ top: scrollTop + 'px', height: scrollLength + 'px' }" @mousedown="onScrollBarMouseDown($event)"></div>
    </div>
    <!-- 全局加载动画 -->
    <circleLoading class="loading-position" v-show="loading" />
  </div>
</template>

<script>
import shrinkableMenu from "./main-components/shrinkable-menu/shrinkable-menu.vue";
import tagsPageOpened from "./main-components/tags-page-opened.vue";
import breadcrumbNav from "./main-components/breadcrumb-nav.vue";
import fullScreen from "./main-components/fullscreen.vue";
import lockScreen from "./main-components/lockscreen/lockscreen.vue";
import messageTip from "./main-components/message-tip.vue";
import circleLoading from "@/views/my-components/xboot/circle-loading.vue";
import ScrollBar from "@/views/my-components/scroll/ScrollBar";
import TableState from '@/views/my-components/state/TableState';
import {debounce} from '@/libs/date';
import Cookies from "js-cookie";
import util from "@/libs/util.js";
import Vue from  'vue';
Vue.component('table-state', TableState);

export default {
  mixins: [ScrollBar],
  components: {
    shrinkableMenu,
    tagsPageOpened,
    breadcrumbNav,
    fullScreen,
    lockScreen,
    messageTip,
    circleLoading
  },
  data() {
    return {
      sliceNum: 3,
      shrink: false,
      username: "",
      userId: "",
      isFullScreen: false,
      openedSubmenuArr: this.$store.state.app.openedSubmenuArr,
      firstThreeNav: [],
      lastNav: [],
      navType: 1,
      domainList:[],
      currentDomain:''
    };
  },
  computed: {
    loading() {
      return this.$store.state.app.loading;
    },
    navList() {
      return this.$store.state.app.navList;
    },
    currNav() {
      return this.$store.state.app.currNav;
    },
    currNavTitle() {
      return this.$store.state.app.currNavTitle;
    },
    menuList() {
      return this.$store.state.app.menuList;
    },
    pageTagsList() {
      return this.$store.state.app.pageOpenedList; // 打开的页面的页面对象
    },
    currentPath() {
      return this.$store.state.app.currentPath; // 当前面包屑数组
    },
    avatarPath() {
      return localStorage.avatorImgPath;
    },
    cachePage() {
      return this.$store.state.app.cachePage;
    },
    lang() {
      return this.$store.state.app.lang;
    },
    menuTheme() {
      return this.$store.state.app.menuTheme;
    },
    mesCount() {
      return this.$store.state.app.messageCount;
    }
  },
  stompClient: {
    monitorIntervalTime: 100,
    stompReconnect: true,
    timeout(orgCmd) {}
  },
  methods: {
    init() {
      let pathArr = util.setCurrentPath(this, this.$route.name);
      // this.$store.commit("updateMenulist");
      if (pathArr.length >= 2) {
        this.$store.commit("addOpenSubmenu", pathArr[1].name);
      }
      let userInfo = JSON.parse(Cookies.get("userInfo"));
      this.username = userInfo.username;
      this.userId = userInfo.id;
      this.checkTag(this.$route.name);
    },
    selectNav(name) {
      this.$store.commit("setCurrNav", name);
      this.setStore("currNav", name);
      // 清空所有已打开标签
      // this.$store.commit("clearAllTags");
      if (this.$route.name != "home_index") {
        this.$router.push({
          name: "home_index"
        });
      }
      util.initRouter(this);
    },
    toggleClick() {
      this.shrink = !this.shrink;
    },
    handleLanDropdown(name) {
      this.$i18n.locale = name;
      this.$store.commit("switchLang", name);
    },
    handleClickUserDropdown(name) {
      if (name == "ownSpace") {
        util.openNewPage(this, "ownspace_index");
        this.$router.push({
          name: "ownspace_index"
        });
      } else if (name == "ownSpaceOld") {
        util.openNewPage(this, "ownspace_old");
        this.$router.push({
          name: "ownspace_old"
        });
      } else if (name == "changePass") {
        util.openNewPage(this, "change_pass");
        this.$router.push({
          name: "change_pass"
        });
      } else if (name == "loginout") {
        // 退出登录
        this.$store.commit("logout", this);
        this.$store.commit("clearOpenedSubmenu");
        this.setStore("accessToken", "");
        this.$socket && this.$socket.onclose ? this.$socket.onclose() : this.$socket = null;
        // 强制刷新页面 重新加载router
        location.reload();
      } else if (name === 'about') {
        if(this.$route.name === 'about') return;
        this.$router.push({name: 'about'});
      }
    },
    checkTag(name) {
      let openpageHasTag = this.pageTagsList.some(item => {
        if (item.name == name) {
          return true;
        }
      });
      if (!openpageHasTag) {
        //  解决关闭当前标签后再点击回退按钮会退到当前页时没有标签的问题
        util.openNewPage(
          this,
          name,
          this.$route.params || {},
          this.$route.query || {}
        );
      }
    },
    handleSubmenuChange(val) {
      // console.log(val)
    },
    beforePush(name) {
      // console.log(name)
      return true;
    },
    fullscreenChange(isFullScreen) {
      // console.log(isFullScreen);
    },
    resize() {
      let currWidth = document.body.clientWidth;
      if (currWidth <= 1200 && currWidth > 900) {
        this.sliceNum = 2;
        this.shrink = true;
      } else if (currWidth <= 900) {
        this.sliceNum = 1;
        this.shrink = true;
      } else {
        this.sliceNum = 3;
        this.shrink = false;
      }
    }
  },
  watch: {
    //监听路由
    $route(to) {
      //设置当前页面的title
      this.$store.commit("setCurrentPageName", to.name);
      let pathArr = util.setCurrentPath(this, to.name);
      if (pathArr.length > 2) {
        this.$store.commit("addOpenSubmenu", pathArr[1].name);
      }
      this.checkTag(to.name);
      localStorage.currentPageName = to.name;
    },
    //设置本地语言
    lang() {
      util.setCurrentPath(this, this.$route.name); // 在切换语言时用于刷新面包屑
    }
  },
  mounted() {
    //初始化请求
    this.init();
    let that = this;
    this.resize();
    window.addEventListener("resize", debounce(that.resize, 1000), false);
    this.$socket.init('/call',(cv) => {
      this.$Notice.info({
        title: '提示',
        desc: JSON.parse(cv.data).msg
      })
    });
  },
  async created() {
    // 显示打开的页面的列表
    this.$store.commit("setOpenedList");
    this.scrollContainerSelector = '.single-page-con'
    this.scrollElementSelector = '.single-page';
    await this.getRequest('/host/getAll').then((res) => {
			if (res.success) {
				if(res.result.length <=0) {
					Message.info('请添加实例!');
				}
				let result = res.result.sort((a, b) => {
					return a.name > b.name ? 1 : -1
				});
            this.$store.dispatch('QUERYALLSERVICE', result);
            this.$store.dispatch('SETSELECTSERVICE', this.$store.state.service.selectService ? this.$store.state.service.selectService : result[0].name);
         }
			})
  }
};
</script>
