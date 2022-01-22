<template>
  <div class="vuejsonpretty">
    <div style="display: flex;padding:0 0 8px 0">
      <div>
        <el-space>
          <span>path:{{ path }}</span>
          <span>method:{{ method }}</span>
          <span>httpStatus:{{ httpStatus }}</span>
          <el-checkbox v-model="showRequestBody" label="请求体" size="small"></el-checkbox>
          <el-checkbox v-model="showRequestHeaders" label="请求头" size="small"></el-checkbox>
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
                       :data="showData" v-model="selectedPaths"
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
    <div>
      <el-space>
        <span class="diff-new">新增值</span>
        <span class="diff-expect">期望值</span>
        <span class="diff-ignore">忽略值</span>
      </el-space>
    </div>
  </div>
</template>

<script>
import { defineComponent, defineProps, ref, computed, onMounted } from 'vue'

export default defineComponent({
  name: "vuejsonpretty",
})
</script>

<script setup>
const props = defineProps({
  data: {
    type: Object,
    default: null,
  },
})
const showRequestBody = ref(false)
const showRequestHeaders = ref(false)
const vuejsonpretty = ref()
const selectedPaths = ref([])


const path = computed(() => {
  return props.data.candidate.path
})
const method = computed(() => {
  return props.data.candidate.method
})
const httpStatus = computed(() => {
  return props.data.candidate.httpStatus
})
const showData = computed(() => {
  const data = {
    responseBody: props.data.candidate.responseBody,
    responseHeaders: props.data.candidate.responseHeaders
  }
  if (showRequestBody.value) {
    data.queryParams = props.data.candidate.queryParams
    data.requestBody = props.data.candidate.requestBody
    data.formData = props.data.candidate.formData
  }
  if (showRequestHeaders.value) {
    data.requestHeaders = props.data.candidate.requestHeaders
  }
  return data
})

const expectKeys = computed(() => {
  return Object.keys(props.data.expectJsonPathValue)
})
const ignoreKeys = computed(() => {
  return Object.keys(props.data.ignoreJsonPathValue)
})
const actualKeys = computed(() => {
  return Object.keys(props.data.actualJsonPathValue)
})


onMounted(() => {
  console.log("vuejsonpretty onMounted")
})

const jsonpath = window.jsonpath

const clearSelected = () => {
  selectedPaths.value = []
}

const divider = `<div class="el-divider el-divider--vertical" style="--el-border-style:solid;"></div>`
const valueFormatter = (data, key, path, defaultFormatResult) => {
  let p = "$"
  const paths = path.split(".")
  for (let i = 1, len = paths.length; i < len; i++) {
    if (paths[i].endsWith("]")) {//数组
      const index = paths[i].indexOf("[")
      p += `['${paths[i].substring(0, index)}']${paths[i].substring(index)}`
    } else {
      p += `['${paths[i]}']`
    }
  }
  // console.log({p, path, data, key, defaultFormatResult})
  if (!ignoreKeys.value.includes(p) && !expectKeys.value.includes(p)) {
    let value = `<span>${defaultFormatResult}</span>`
    value += `<span style="margin-left: 8px" class="diff-new">NEW</span>`
    return value
  }
  if (ignoreKeys.value.includes(p)) {
    const ignores = props.data.ignoreJsonPathValue[p]
    let value = `<span >${defaultFormatResult}</span>`
    value += divider
    value += `<span class="diff-ignore">${typeof ignores[0] === 'string' ? `"${ignores[0]}"` : ignores[0]}</span>`
    value += divider
    value += `<span class="diff-ignore">${typeof ignores[1] === 'string' ? `"${ignores[1]}"` : ignores[1]}</span>`
    return value
  }
  if (expectKeys.value.includes(p)) {
    const expect = props.data.expectJsonPathValue[p]
    let value = `<span >${defaultFormatResult}</span>`
    if (expect !== data) {
      value += divider
      value += `<span class="diff-expect">${typeof expect === 'string' ? `"${expect}"` : expect}</span>`
    }
    return value
  }

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

.vuejsonpretty::v-deep .vjs-tree__node .vjs-value.vjs-value__null{
  color: grey;
}

</style>

<style>
.diff-expect {
  box-sizing: border-box;
  padding: 2px;
  color: white;
  background-color: red;
  white-space: normal;
  border-radius: 5px;
}

.diff-ignore {
  color: rgba(19, 206, 102, 0.35);
  white-space: normal;
}

.diff-new {
  box-sizing: border-box;
  padding: 2px;
  color: white;
  background-color: #67c23a;
  white-space: normal;
  border-radius: 5px;
}
</style>