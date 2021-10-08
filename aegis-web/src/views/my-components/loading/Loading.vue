<template>
  <div class="loading-container" :class="{'cover': cover}">
		<div class="loading-box">
      <Spin fix></Spin>
    </div>
	</div>
</template>

<script>
export default {
	name: 'Loading',
	props: {
		cover: {
			type: Boolean,
			default: false
		}
	}
};
</script>

<style lang="less" scoped>
.loading-box{
  	display: inline-block;
    width: 30px;
    height: 30px;
    box-sizing: border-box;
    position:relative;
    //设置圆角再设置边框就会有弧线的效果
}
	.loading-container{
		width: 100%;
		text-align: center;
		vertical-align: middle;
		height:100%;
	}
	.cover{
		background: #fff;
		opacity: .5;
	}
</style>