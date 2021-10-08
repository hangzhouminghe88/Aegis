<template>
  <div>
    <Row class="expand-row" v-if="type === 'sql_text'">
      <Col>
        <span class="expand-key">sql语句:</span>
        <span class="expand-value">{{ row.sqlText }}</span>
      </Col>
    </Row>
	  <template v-if="type === 'detail'">
		  <Row class="expand">
			<Col :span="12">
			  <span class="expand-key">el%:</span>
        <span class="expand-value">{{ row.elapPer }}</span>
			</Col>
			<Col :span="12">
			  <span class="expand-key">cp%:</span>
        <span class="expand-value">{{ row.cputPer }}</span>
			</Col>
		</Row>
		<Row class="expand">
			<Col :span="12">
			  <span class="expand-key">el%:</span>
        <span class="expand-value">{{ row.elapPer }}</span>
			</Col>
			<Col :span="12">
			  <span class="expand-key">cpu:</span>
        <span class="expand-value">{{ row.cpuTime }}</span>
			</Col>
		</Row>
		<Row class="expand">
			<Col :span="12">
			  <span class="expand-key">ps%:</span>
        <span class="expand-value">{{ row.parsePer }}</span>
			</Col>
			<Col :span="12">
			  <span class="expand-key">so%:</span>
        <span class="expand-value">{{ row.sortPer }}</span>
			</Col>
		</Row>
		<Row class="expand">
			<Col :span="12">
			  <span class="expand-key">row:</span>
        <span class="expand-value">{{ row.rowsProcessed }}</span>
			</Col>
			<Col :span="12">
			  <span class="expand-key">vc:</span>
        <span class="expand-value">{{ row.versionCount }}</span>
			</Col>
		</Row>
		<Row class="expand">
			<Col :span="12">
			  <span class="expand-key">app:</span>
        <span class="expand-value">{{ row.appName }}</span>
			</Col>
			<Col :span="12">
			  <span class="expand-key">mod:</span>
        <span class="expand-value">{{ row.service }}</span>
			</Col>
		</Row>
		</template>
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