<template>
  <div ref="pagination">
		<span>{{`共\r\t${total}\r\t条`}}</span>
      <Dropdown type="trigger" @on-click="handleSizeChange" stop-propagation >
         <Button  class="dropdown">
            {{pageSize}}
            <Icon type="ios-arrow-down"></Icon>
         </Button>
        <DropdownMenu slot="list">
            <DropdownItem v-for="(size, index) in sizes" 
				                  :key="index" 
													:class="{'active-text': size === pageSize}"
													:name="size">{{size}}</DropdownItem>
        </DropdownMenu>
    </Dropdown>
			<button class="mh-pagination__item btn" @click="prev" :class="{'disabled': 1 === currentPage}">
        上一页
			</button>
    <ul @click="togglePager" class="mh-pagination">
      <li
        class="mh-pagination__item"
        :class="{ active: currentPage === 1, disabled }"
        v-if="pageCount > 0"
      >{{1}}</li>
      <li
        class="el-icon more btn-quickprev mh-pagination__item"
        :class="[quickprevIconClass, { disabled }]"
        v-if="showPrevMore"
        @mouseenter="onMouseenter('left')"
        @mouseleave="quickprevIconClass = 'ivu-icon ivu-icon-ios-arrow-back'"
      ></li>
      <li
        v-for="(page, index) in pagers"
        :key="index"
        class="mh-pagination__item"
        :class="{ active: currentPage === page}"
      >{{page}}</li>
      <li
        class="el-icon more btn-quicknext mh-pagination__item"
        :class="[quicknextIconClass, { disabled }]"
        v-if="showNextMore"
        @mouseenter="onMouseenter('right')"
        @mouseleave="quicknextIconClass = 'ivu-icon ivu-icon-ios-arrow-forward'"
      ></li>
      <li
        :class="{ active: currentPage === pageCount, disabled }"
        v-if="pageCount > 1"
        class="mh-pagination__item"
      >{{pageCount}}</li>
    </ul>
		<button  class="mh-pagination__item btn" @click="next" :class="{'disabled': pageCount === currentPage || pageCount ===0}">
        下一页
		</button>
  </div>
</template>

<script>
import { stopBubble } from '@/libs/date';
export default {
  name: "Pagination",
  props: {
    currentPage: {
      type: Number,
      default: () => {
        return 1;
      }
    },
    disabled: {
      type: Boolean,
      default: false
    },
    sizes: {
      type: Array,
      default: () => {
        return [10, 20, 50, 100];
      }
    },
    pageSize: {
      type: Number,
      default: 10
    },
    total: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      pagerCount: 7,
      showPrevMore: false,
      showNextMore: false,
      quicknextIconClass: "ivu-icon ivu-icon-ios-arrow-forward",
			quickprevIconClass: "ivu-icon ivu-icon-ios-arrow-back",
			dropdownIconClass: 'ivu-icon ivu-icon-el-icon-caret-bottom'
    };
  },
  methods: {
    onMouseenter(direction) {
			//展示是否左滑或者右滑的图标若为
      if (this.disabled) return;
      if (direction === "left") {
        this.quickprevIconClass = "ivu-icon ivu-icon-ios-arrow-back";
      } else {
        this.quicknextIconClass = "ivu-icon ivu-icon-ios-arrow-forward";
      }
    },
    togglePager(event) {
      stopBubble(event);
			//切换页码点解页码按钮
      let target = event.target,
        _this = this;
        var dragDom = _this.$refs.pagination;
        //阻止所选dom内容被选中
        dragDom.onselectstart=dragDom.ondrag=function(){
          return false;
        }
      let offset = _this.pagerCount - 2;
      if (target.tagName === "UL" || this.disabled) {
        return;
      }
      //计算新页码
			let newPage = Number(event.target.textContent);
			//总页数
			const pageCount = _this.pageCount;
			//当前页
			const currentPage = _this.currentPage;
			//点击图标时左滑或右滑的页码数
			const pagerCountOffset = _this.pagerCount - 2;
			//设置新的当前页
      if (target.className.indexOf("more") !== -1) {
        if (target.className.indexOf("quickprev") !== -1) {
          newPage = currentPage - pagerCountOffset;
        } else if (target.className.indexOf("quicknext") !== -1) {
          newPage = currentPage + pagerCountOffset;
        }
      }
			/* istanbul ignore if */
      if (!isNaN(newPage)) {
					//如果新页码不是数字设置当前页为1；
        if (newPage < 1) {
          newPage = 1;
        }
	       //如果新页码大于总页码设置当前页为总页码；
        if (newPage > pageCount) {
          newPage = pageCount;
        }
      }
      //如果新页码不等于当前页则翻页
      if (newPage !== currentPage) {
        this.$emit("change", newPage);
			}
		},
		handleSizeChange(size) {
			//处理每页显示多少数据
			let _this = this;
			if(size === _this.pageSize) return;
			_this.$emit('size-change', size);
		},
		prev(event) {
      stopBubble(event);
      let _this = this, newPage = 0;
      var dragDom = _this.$refs.pagination;
        //阻止所选dom内容被选中
        dragDom.onselectstart=dragDom.ondrag=function(){
          return false;
        }
			//如果当前页不等于1则处理上一页翻页
		  if(_this.currentPage !== 1 && _this.pageCount !== 0) {
					newPage = _this.currentPage - 1;
				_this.$emit('change', newPage);
			}
		},
		next(event) {
      stopBubble(event);
      let _this = this, newPage = 0;
      var dragDom = _this.$refs.pagination;
        //阻止所选dom内容被选中
        dragDom.onselectstart=dragDom.ondrag=function(){
          return false;
        }
        //如果当前页不等于总页数则处理下一页翻页
		  if(_this.currentPage !== _this.pageCount && _this.pageCount !== 0) {
				newPage = _this.currentPage + 1;
				_this.$emit('change', newPage);
			}
		}
  },
  computed: {
    pageCount() {
      let _this = this;
      //计算总页数
      return Math.ceil(_this.total / _this.pageSize);
    },
    pagers() {
      let _this = this;
      const pagerCount = this.pagerCount;
      const halfPagerCount = (pagerCount - 1) / 2;

      const currentPage = Number(this.currentPage);
      const pageCount = Number(this.pageCount);

      let showPrevMore = false;
      let showNextMore = false;
      if (pageCount > pagerCount) {
        if (currentPage > pagerCount - halfPagerCount) {
          showPrevMore = true;
        }

        if (currentPage < pageCount - halfPagerCount) {
          showNextMore = true;
        }
      }
      const array = [];

      if (showPrevMore && !showNextMore) {
        const startPage = pageCount - (pagerCount - 2);
        for (let i = startPage; i < pageCount; i++) {
          array.push(i);
        }
      } else if (!showPrevMore && showNextMore) {
        for (let i = 2; i < pagerCount; i++) {
          array.push(i);
        }
      } else if (showPrevMore && showNextMore) {
        const offset = Math.floor(pagerCount / 2) - 1;
        for (let i = currentPage - offset; i <= currentPage + offset; i++) {
          array.push(i);
        }
      } else {
        for (let i = 2; i < pageCount; i++) {
          array.push(i);
        }
      }

      this.showPrevMore = showPrevMore;
      this.showNextMore = showNextMore;

      return array;
    }
  },
  watch: {
    showPrevMore(val) {
      if (!val) this.quickprevIconClass = "ivu-icon ivu-icon-ios-arrow-back";
    },

    showNextMore(val) {
      if (!val) this.quicknextIconClass = "ivu-icon ivu-icon-ios-arrow-forward";
    },
    'total': function(newVal, oldVal) {
      if(newVal !== oldVal){
        return newVal;
      }
    }
  }
};
</script>

<style lang="less" scoped>
  @import url("~@views/my-components/pagination/pagination.less");;
</style>