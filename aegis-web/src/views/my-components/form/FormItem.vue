<template>
  <div class="form-item">
    <label v-if="label" v-text="label" class="margin-right-10"></label>
    <input :type="type" :placeholder="placeholder" v-if="['text', 'number', 'email', 'time', 'password'].includes(type)" :value="value" @input="handleInput" style="display:inline-block;" />
		 <textarea :type="type" v-if="type==='textarea'"
           rows="3"
           ref="input"
           :value = "value"
           @input="handleInput"
           :placeholder="placeholder"/>
    <slot></slot>
  </div>
</template>

<script>
export default {
  name: "FormItem",
  props: {
    value: {
      type: [String, Number, Boolean]
    },
    label: {
      type: String,
      default: ""
    },
    type: {
      type: String,
      default: "text"
		},
		placeholder: String,
  },
  methods: {
    //处理@input事件做到双向绑定
    handleInput(event) {
      this.$emit("input", event.target.value);
    }
  }
};
</script>

<style lang="less" scoped>
 @import url("~@views/my-components/form/formItem.less");
</style>