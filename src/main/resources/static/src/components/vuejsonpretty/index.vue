<template>
  <div class="vuejsonpretty">
    <div style="display: flex;padding:0 0 8px 0">
      <div>
        <el-space>
          <span>字段数：</span>
          <span>相似率：</span>
        </el-space>
      </div>
      <div style="flex-grow: 1"></div>
      <div>
        <el-space>
          <el-button size="small" type="danger" @click="clearSelected">清空选择</el-button>
          <el-button size="small" type="primary">比对</el-button>
        </el-space>
      </div>
    </div>
    <div style="flex-grow: 1;overflow: auto;">
      <vue-json-pretty ref="vuejsonpretty" style="height: 100%"
                       :data="data.candidate" v-model="selectedPaths"
                       :show-length="true"
                       :virtual="true" :virtual-lines="30"
                       path="$"
                       selectable-type="multiple"
                       :show-select-controller="true"
                       :select-on-click-node="false"
                       :custom-value-formatter="valueFormatter"
      >
      </vue-json-pretty>
    </div>
  </div>
</template>

<script>
import { defineComponent, defineProps, ref, computed } from 'vue'

const VueJsonPretty = window.VueJsonPretty.default
export default defineComponent({
  name: "vuejsonpretty",
  components: {'vue-json-pretty': VueJsonPretty}
})
</script>

<script setup>
const props = defineProps({
  data: {
    type: Object,
    default: null,
  },
})
const vuejsonpretty = ref()
const selectedPaths = ref([])

const jsonpath = window.jsonpath

const clearSelected = () => {
  selectedPaths.value = []
}
const valueFormatter = (data, key, path, defaultFormatResult) => {
  let value = `<span>${defaultFormatResult}</span>`
  for (let i = 0, len = props.data.masters.length; i < len; i++) {
    if (path && path.indexOf('-') > -1) {
      return value
      // const params = path.split(".")
      // for (let j = 0,l=params.length; j < l; j++) {
      //   if (params.indexOf('-') > -1) {
      // }
    }

    const v = jsonpath.query(props.data.masters[i], path)[0];
    // console.log({data, key, path, defaultFormatResult,v})
    value += `
        <div class="el-divider el-divider--vertical" style="--el-border-style:solid;"></div>
        <span class="${data !== v ? 'diff-red' : ''}">${v}</span>
    `
  }
  return value
}
</script>

<style scoped>
.vuejsonpretty {
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
}

.vuejsonpretty::v-deep .vjs-tree__node.has-selector.is-highlight {
  color: grey;
}

.vuejsonpretty::v-deep .vjs-tree__node.has-selector.is-highlight .vjs-value {
  color: grey;
}

</style>

<style>
.diff-red{
  color: red;
}
</style>