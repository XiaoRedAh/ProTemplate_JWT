<script setup>

import {logout} from "@/net/index.js";
import router from "@/router/index.js";

const userLogout = ()=>{
  logout(()=>router.push('/'))
}
</script>

<template>
  <div>
    欢迎
  </div>
  <div>
    <el-button @click="userLogout" type="danger">退出登录</el-button>
  </div>
</template>


<style scoped>

</style>
