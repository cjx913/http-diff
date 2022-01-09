<template>
  <div class="home">
    <el-button type="primary">{{ name }}</el-button>
    <jsondiffpatch :left="{a:'aa'}" :right="{a:'aaa'}"/>
  </div>
</template>

<script setup>
import jsondiffpatch from "../components/jsondiffpatch/index.vue"

const name = 'about'
</script>

<style scoped>
.home {
  box-sizing: border-box;
  width: 100%;
  height: 100%;
}
</style>