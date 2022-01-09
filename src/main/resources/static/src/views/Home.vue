<template>
  <div class="home">
    <el-button type="primary">{{ name }}</el-button>
  </div>
</template>

<script lang="ts" setup>
const name = 'home'
</script>

<style scoped>
.home {
  box-sizing: border-box;
  padding: 8px;
  width: 100%;
  height: 100%;
}
</style>