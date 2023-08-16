<script setup>

</script>

<template>
  <div class="layout">
    <el-container class="container">
      <el-aside class="aside">Aside</el-aside>
      <el-main class="main">Main</el-main>
    </el-container>
  </div>
</template>

<style scoped>
.layout{
  height: 100vh
}

.container{
  height: 100vh
}

.aside{
  width: 65%;
  background-color: red
}

.main{
  background-color: dodgerblue;
  width: 35%
}
</style>
