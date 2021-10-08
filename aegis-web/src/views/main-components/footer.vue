<template>
  <div class="foot">
    <Row type="flex" justify="space-around" class="help">
      <a class="item" href="http://www.mingheinfo.com" target="_blank">{{ $t('help') }}</a>
      <a class="item" href="http://www.mingheinfo.com" target="_blank">{{ $t('privacy') }}</a>
      <a class="item" href="http://www.mingheinfo.com" target="_blank">{{ $t('terms') }}</a>
    </Row>
    <Row type="flex" justify="center" class="copyright">
      Copyright © 2020 <a href="http://www.mingheinfo.com" target="_blank" style="margin:0 5px;">杭州明和科技股份有限公司</a> 版权所有
    </Row>
  </div>
</template>

<script>
export default {
  name: "footer"
};
</script>

<style lang="less">
.foot {
  position: fixed;
  bottom: 4vh;
  width: 368px;
  color: rgba(0, 0, 0, 0.45);
  font-size: 14px;
  .help {
    margin: 0 auto;
    margin-bottom: 1vh;
    width: 60%;
    .item {
      color: rgba(0, 0, 0, 0.45);
    }
    :hover {
      color: rgba(0, 0, 0, 0.65);
    }
  }
}
</style>
