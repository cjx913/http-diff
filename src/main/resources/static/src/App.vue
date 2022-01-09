<template>
  <div class="app">
    <div>
      <el-menu
          :default-active="activeIndex2"
          class="el-menu-demo"
          mode="horizontal"
          background-color="#545c64"
          text-color="#fff"
          active-text-color="#ffd04b"
          :router="true"
      >
        <div style="padding: 8px;margin-right: 16px">
          <span style="font-size: 40px;line-height: 40px;color: white">HttpDiff</span>
        </div>
        <el-menu-item index="/result">回放记录</el-menu-item>
        <el-menu-item index="/testcase">回放用例</el-menu-item>
        <el-menu-item index="/about">关于</el-menu-item>
        <!--        <el-sub-menu index="2">-->
        <!--          <template #title>Workspace</template>-->
        <!--          <el-menu-item index="2-1">item one</el-menu-item>-->
        <!--          <el-menu-item index="2-2">item two</el-menu-item>-->
        <!--          <el-menu-item index="2-3">item three</el-menu-item>-->
        <!--          <el-sub-menu index="2-4">-->
        <!--            <template #title>item four</template>-->
        <!--            <el-menu-item index="2-4-1">item one</el-menu-item>-->
        <!--            <el-menu-item index="2-4-2">item two</el-menu-item>-->
        <!--            <el-menu-item index="2-4-3">item three</el-menu-item>-->
        <!--          </el-sub-menu>-->
        <!--        </el-sub-menu>-->
        <!--        <el-menu-item index="3" disabled>Info</el-menu-item>-->

      </el-menu>
    </div>
    <div style="flex-grow: 1;overflow: auto">
      <el-scrollbar>
        <router-view>
        </router-view>
      </el-scrollbar>
    </div>
    <div style="text-align: center;color: #bbbbbb">
      <p style="margin: 8px 0">copy right @ 2022</p>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'

const activeIndex2 = ref('1')
const handleSelect = (key, keyPath) => {
  console.log(key, keyPath)
}

</script>

<style>
.el-scrollbar__view{
  box-sizing: border-box;
  width: 100%;
  height: 100%;
}
</style>
<style scoped>
.app {
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
}
</style>