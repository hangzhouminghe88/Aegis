<template>
  <div>
    <Row class="header">
      <img src="../../assets/logo.png" width="250px" />
      <div class="description">{{ $t('maxdata') }}</div>
    </Row>
  </div>
</template>

<script>
export default {
  name: "header"
};
</script>

<style lang="less">
.header {
  margin-top: 10vh;
  margin-bottom: 6vh;
  text-align: center;

  .description {
    font-size: 14px;
    color: rgba(0, 0, 0, 0.45);
    margin-top: 1vh;
  }
}
</style>
