<template>
  <div>
    <Row class="expand-row">
      <Col>
        <span class="expand-key">序列名称:</span>
        <span class="expand-value">{{ row.sequence }}</span>
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
		},
		type: String
  },
  methods: {}
};
</script>

<style lang="less" scoped>
   .expand-key{
		 padding-right: 20px;
		 width: 8rem;
	 }

	 .expand{
		 padding-bottom: 20px;
	 }
</style>