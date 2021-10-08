<template>
  <div>
    <Row class="expand-row">
      <Col :span="12">
        <span class="expand-key">段名:</span>
        <span class="expand-value">{{ row.segmentName }}</span>
      </Col>
    </Row>
  </div>
</template>

<script>
export default {
  name: "ExpandRow",
  props: {
    row: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  methods: {}
};
</script>

<style lang="less" scoped>
   .expand-key{
		 padding-right: 20px;
	 }
</style>