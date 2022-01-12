<template>
  <div class="result">
    <el-row style="height: 100%;width:100%" :gutter="8">
      <el-col style="height: 100%" :xs="8" :sm="8" :md="8" :lg="4" :xl="2">
        <vxe-table
            height="100%"
            :resizable="true"
            :show-header="false"
            :show-overflow="true"
            :show-header-overflow="true"
            :data="httpDiffResultKeys"
            :row-config="{isCurrent: true, isHover: true}"
            :scroll-y="{gt: 30}"
            @cell-click="handleSelect">
          <vxe-column field="key" title="key">
            <template #default="{row}">
              <span style="overflow: hidden;text-overflow: ellipsis;white-space: nowrap;cursor: pointer;">
                <span>{{ row.key }}</span>
                <span style="margin: 0 4px"></span>
                <span>[{{ row.totalCount }}</span>
                <span>({{ (row.passRate * 100).toFixed(2) }}%)]</span>
              </span>
            </template>
          </vxe-column>
        </vxe-table>
        <!--        <el-menu-->
        <!--            style="height: 100%;overflow: hidden"-->
        <!--            class="el-menu-vertical-demo"-->
        <!--            @select="handleSelect"-->
        <!--        >-->
        <!--          <el-scrollbar>-->
        <!--            <el-menu-item v-for="item in httpDiffResultKeys" :key="item.key" :index="item.key">-->
        <!--              <el-tooltip-->
        <!--                  class="box-item"-->
        <!--                  effect="light"-->
        <!--                  content="Top Left prompts info"-->
        <!--                  placement="top-start"-->
        <!--              >-->
        <!--                <div style="overflow: hidden;text-overflow: ellipsis;white-space: nowrap;">-->
        <!--                  <span>{{ item.key }}</span>-->
        <!--                  <span style="margin: 0 4px"></span>-->
        <!--                  <span>[{{ item.totalCount }}</span>-->
        <!--                  <span>({{ (item.passRate * 100).toFixed(2) }}%)]</span>-->
        <!--                </div>-->
        <!--              </el-tooltip>-->
        <!--            </el-menu-item>-->
        <!--          </el-scrollbar>-->
        <!--        </el-menu>-->
      </el-col>
      <el-col style="box-sizing: border-box;height: 100%" :xs="16" :sm="16" :md="16" :lg="20" :xl="22">
        <div style="box-sizing: border-box;display:flex;flex-direction:column;padding: 8px;height: 100%">
          <div>
            <el-row>
              <el-col :span="6">{{ selectedKey.key }}</el-col>
              <el-col :span="6">{{ selectedKey.totalCount }}</el-col>
              <el-col :span="6">{{ selectedKey.passCount }}</el-col>
              <el-col :span="6">{{ (selectedKey.passRate * 100).toFixed(2) }}%</el-col>
            </el-row>
          </div>
          <div style="flex-grow: 1">
            <vxe-table
                height="100%"
                :resizable="true"
                :stripe="true"
                :show-overflow="true"
                :show-header-overflow="true"
                :show-footer-overflow="true"
                :data="tableData"
                :scroll-y="{gt: 50}"
                :row-config="{isCurrent: true, isHover: true}"
                :seq-config="{seqMethod:({rowIndex})=>(page.currentPage-1)*page.pageSize+rowIndex+1}">
              <vxe-column type="seq" width="60"></vxe-column>
              <vxe-column field="id" title="ID" width="100"></vxe-column>
              <vxe-column field="key" title="key" width="120"></vxe-column>
              <vxe-column field="result" title="result" width="60"></vxe-column>
              <vxe-column field="candidate.method" title="method" width="70" align="center"></vxe-column>
              <vxe-column field="candidate.path" title="path" min-width="120"></vxe-column>
              <vxe-column field="candidate.httpStatus" title="responseStatus" width="120" align="center"></vxe-column>

              <!--              <vxe-column field="expectJsonPathValue" title="expectJsonPathValue">-->
              <!--                <template #default="{row}">-->
              <!--                  {{ JSON.stringify(row.expectJsonPathValue, null, 4) }}-->
              <!--                </template>-->
              <!--              </vxe-column>-->
              <!--              <vxe-column field="candidate" title="candidate">-->
              <!--                <template #default="{row}">-->
              <!--                  {{ JSON.stringify(row.candidate, null, 2) }}-->
              <!--                </template>-->
              <!--              </vxe-column>-->
              <!--              <vxe-column field="masters" title="masters">-->
              <!--                <template #default="{row}">-->
              <!--                  {{ JSON.stringify(row.masters, null, 2) }}-->
              <!--                </template>-->
              <!--              </vxe-column>-->

              <vxe-column field="version" title="version" width="100" align="center"></vxe-column>
              <vxe-column field="denoise" title="denoise" width="80" align="right"></vxe-column>
              <vxe-column title="查看" width="200" align="center" flex="right">
                <template #default="{row}">
                  <el-space>
                    <el-button type="text" @click="openCompareResultModel(row)">比对结果</el-button>
                    <el-button type="text" @click="openCompareDetailsModel(row)">比对详情</el-button>
                  </el-space>
                </template>
              </vxe-column>
            </vxe-table>
            <vxe-modal v-model="compareResultModel.visible" width="70vw" height="80%" min-height="400" show-zoom resize title="比对结果">
              <template #default>
                <div style="height: 100%;overflow: hidden">
                  <vuejsonpretty :data="compareResultModel.data"></vuejsonpretty>
                </div>
              </template>
            </vxe-modal>
            <vxe-modal v-model="compareDetailsModel.visible" width="70vw" height="80%" min-height="400" show-zoom
                       resize title="比对详情">
              <template #default>
                <div style="height: 100%;overflow: hidden">
                  <el-scrollbar>
                    <div style="display: flex;">
                      <div style="flex-grow: 1;min-width: 300px"
                           v-for="master in compareDetailsModel.data.masters"
                           :key="master.name">
                        <div>{{ compareDetailsModel.data.candidate.name }}<===>{{ master.name }}</div>
                        <jsondiffpatch :right="compareDetailsModel.data.candidate.responseBody"
                                       :left="master.responseBody"/>
                      </div>
                    </div>
                  </el-scrollbar>
                </div>
              </template>
            </vxe-modal>
          </div>
          <div>
            <vxe-pager
                perfect
                v-model:current-page="page.currentPage"
                v-model:page-size="page.pageSize"
                :total="page.total"
                :page-sizes="[10, 20, 100, {label: '大量数据', value: 1000}, {label: '全量数据', value: -1}]"
                :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
                @page-change="pageChange">
            </vxe-pager>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: "result"
}
</script>
<script setup>
import { ref, reactive, onMounted, computed } from "vue"

import jsondiffpatch from "../../components/jsondiffpatch/index.vue"
import vuejsonpretty from "../../components/vuejsonpretty/index.vue"

const httpDiffResultKeys = ref([])
const currentKey = ref('')
const tableData = ref([])
const page = reactive({
  currentPage: 1,
  pageSize: 20,
  total: 0,
})
const compareResultModel = reactive({
  visible: false,
  data: {}
})
const compareDetailsModel = reactive({
  visible: false,
  data: {}
})

onMounted(() => {
  getHttpDiffResultKeys()
})
const getHttpDiffResultKeys = () => {
  fetch(`/httpDiffResult/keys`).then(resp => resp.json())
      .then(data => {
        httpDiffResultKeys.value = data
      })
}

const getHttpDiffResult = () => {
  if (!currentKey.value) return
  fetch(`/httpDiffResult?key=${currentKey.value}&current=${page.currentPage}&size=${page.pageSize}`)
      .then(resp => resp.json())
      .then(data => {
        tableData.value = data.records
        page.total = data.total
      })
}

const handleSelect = ({row}) => {
  currentKey.value = row.key
  page.currentPage = 1
  page.pageSize = 20
  getHttpDiffResult()
}

const selectedKey = computed(() => {
  if (httpDiffResultKeys.value.length < 1) return {key: '', totalCount: 0, passCount: 0, passRate: 1}
  const key = httpDiffResultKeys.value.find(item => item.key === currentKey.value)
  if (!key) return {key: '', totalCount: 0, passCount: 0, passRate: 1}
  return key
})

const pageChange = (type, currentPage, pageSize, $event) => {
  console.log({type})
  if (type.type === 'size') {
    page.currentPage = 1
  }
  getHttpDiffResult()
}

const openCompareResultModel = (data) => {
  compareResultModel.visible = true
  compareResultModel.data = data
}
const openCompareDetailsModel = (data) => {
  compareDetailsModel.visible = true
  compareDetailsModel.data = data
}

</script>

<style scoped>
.result {
  box-sizing: border-box;
  display: flex;
  height: 100%;
  width: 100%;
}

</style>