<template>
	<div class="state-outer">
		<span class="state-dot" :class="_class"></span>
		<span class="state-text" v-text="content && content"></span>
	</div>
</template>

<script>
export default {
	name: 'table-state',
	props: {
		state: {
			type: String,
			required: true,
			default: ''
		},
		content: String
	},
	computed: {
		_class() {
			let state = this.state.toLowerCase();
		  return {
					'open': state === 'open',
					'close': state === 'close',
					'no': state === 'no',
					'yes': state === 'yes'
			};
		}
	}
}
</script>
<style lang="less" scoped>
  @import url('./tablestate.less');
</style>