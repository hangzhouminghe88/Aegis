<template>
  <span class="setting" data-setting="setting">
    <Button class="btn" type="default" icon="ios-settings" data-setting="setting"></Button>
    <span class="setting-content" v-show="showContent" data-setting="setting">
      <div v-for="item in columns" data-setting="setting" :key="item.key">
        <Checkbox :value="item.isSetting" @on-change="$emit('setting-change', item)" data-setting="setting"></Checkbox>
        <span data-setting="setting" class="setting-title">{{item.title}}</span>
      </div>
    </span>
  </span>
</template>

<script>
  /**
  *防抖方法
  */
  import {debounce} from "@/libs/date";

  export default {
    name: "SettingTableColumn",
    props: {
      columns: {
        type: Array,
        default: () => {
          return []
        }
      }
    },
    data() {
      return {
        showContent: false,
        settingDom: null
      }
    },
    mounted() {
      const _this = this;
      _this.settingDom = document.querySelector('.setting');
      //监听点击btn事件
      _this.settingDom.addEventListener('click', debounce(_this.show, 1000, true));
      //监听window点击事件
      document.addEventListener('click', debounce(_this.hidden, 1000, true))
    },
    methods: {
      /**
      *展示下拉选择框@param e EventElement
      */
      show(e) {
        e.stopPropagation();
        //点击button时显示隐藏
        if(e.target.className.indexOf('btn') >=0 || e.target.parentNode.className.indexOf('btn') >= 0) {
          this.showContent = !this.showContent;
        }
      },
      /**
      *隐藏下拉选择框
      **/
      hidden(e) {
        //当在setting内部点击时不隐藏
        if(e.target.dataset.setting || e.target.parentNode.dataset.setting || e.target.parentNode.parentNode.dataset.setting){
          return;
        }
        this.showContent = false;
      }
    },
    beforeDestroy() {
      const _this = this;
      _this.settingDom.removeEventListener('click', _this.show)
      document.removeEventListener('click', _this.hidden)
    }
  }
</script>

<style scoped lang="less">
  .setting{
    display: inline-block;
    position: relative;
    &-content {
      position: absolute;
      padding: 10px 20px;
      display: inline-block;
      width: 12rem;
      z-index: 999;
      background: #fff;
      left: -9rem;
      top: 39px;
      box-shadow: 1px 1px 1px #dfdfdf, -1px 1px 1px #dfdfdf;
    }
    &-title{
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      width: 100px;
      display: inline-block;
      vertical-align: middle;
    }
  }
</style>
