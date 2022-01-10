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
      <vue-json-pretty ref="vuejsonpretty" style="height: 100%" :data="data" v-model="selectedPaths"
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
import { defineComponent, ref, computed } from 'vue'

const VueJsonPretty = window.VueJsonPretty.default
export default defineComponent({
  name: "vuejsonpretty",
  components: {'vue-json-pretty': VueJsonPretty}
})
</script>

<script setup>
const vuejsonpretty = ref()
const selectedPaths = ref([])
const data = ref(JSON.parse("[{\"path\":\"/test\",\"responseBody\":null,\"requestHeaders\":{\"content-length\":[\"54\"],\"Accept\":[\"*/*\"],\"Connection\":[\"keep-alive\"],\"User-Agent\":[\"PostmanRuntime/7.28.4\"],\"Host\":[\"localhost:8080\"],\"Postman-Token\":[\"fa9f9c94-7a92-4abb-b1a5-85e464c51d85\"],\"Accept-Encoding\":[\"gzip, deflate, br\"],\"Content-Type\":[\"application/json\"]},\"responseHeaders\":{},\"method\":\"POST\",\"queryParams\":{\"username\":[\"cjx91311\"]},\"requestBody\":{\"arr\":[1,2,3,4],\"double\":2.3,\"int\":1},\"httpStatus\":\"INTERNAL_SERVER_ERROR\",\"name\":\"master1\",\"formData\":{}},{\"path\":\"/test\",\"responseBody\":null,\"requestHeaders\":{\"content-length\":[\"54\"],\"Accept\":[\"*/*\"],\"Connection\":[\"keep-alive\"],\"User-Agent\":[\"PostmanRuntime/7.28.4\"],\"Host\":[\"localhost:8080\"],\"Postman-Token\":[\"fa9f9c94-7a92-4abb-b1a5-85e464c51d85\"],\"Accept-Encoding\":[\"gzip, deflate, br\"],\"Content-Type\":[\"application/json\"]},\"responseHeaders\":{},\"method\":\"POST\",\"queryParams\":{\"username\":[\"cjx91311\"]},\"requestBody\":{\"arr\":[1,2,3,4],\"double\":2.3,\"int\":1},\"httpStatus\":\"INTERNAL_SERVER_ERROR\",\"name\":\"master2\",\"formData\":{}},{\"path\":\"/test\",\"responseBody\":null,\"requestHeaders\":{\"content-length\":[\"54\"],\"Accept\":[\"*/*\"],\"Connection\":[\"keep-alive\"],\"User-Agent\":[\"PostmanRuntime/7.28.4\"],\"Host\":[\"localhost:8080\"],\"Postman-Token\":[\"fa9f9c94-7a92-4abb-b1a5-85e464c51d85\"],\"Accept-Encoding\":[\"gzip, deflate, br\"],\"Content-Type\":[\"application/json\"]},\"responseHeaders\":{},\"method\":\"POST\",\"queryParams\":{\"username\":[\"cjx91311\"]},\"requestBody\":{\"arr\":[1,2,3,4],\"double\":2.3,\"int\":1},\"httpStatus\":\"INTERNAL_SERVER_ERROR\",\"name\":\"master3\",\"formData\":{}}]"))

const clearSelected = ()=>{
  selectedPaths.value= []
}
const valueFormatter = (data, key, path, defaultFormatResult) => {
  return `
  <span>${defaultFormatResult}</span>
  <div class="el-divider el-divider--vertical" style="--el-border-style:solid;"></div>
  <a href="/#/">test</a>
  `
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