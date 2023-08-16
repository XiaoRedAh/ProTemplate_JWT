<script setup>
import {reactive} from "vue";
import {User,Lock} from '@element-plus/icons-vue';

const form = reactive({
  username: '',
  password: '',
  remember: false
})

</script>

<template>
  <div class="container">
    <!--标题-->
    <div style="margin-top: 200px">
      <div style="font-size: 25px;font-weight: bold">登录</div>
      <div style="font-size: 14px;color:grey">输入用户名和密码进行登录</div>
    </div>
    <!--输入框-->
    <div style="margin-top: 50px">
      <el-input v-model="form.username" type="text" placeholder="用户名/邮箱">
        <template #prefix>
          <el-icon><User /></el-icon>
        </template>
      </el-input>
      <el-input v-model="form.password" type="password" style="margin-top: 20px" placeholder="密码">
        <template #prefix>
          <el-icon><Lock /></el-icon>
        </template>
      </el-input>
    </div>
    <!--记住我，忘记密码-->
    <div style="margin-top: 10px">
      <el-row>
        <el-col :span="12" style="text-align: left">
          <el-checkbox v-model="form.remember" label="记住我" size="large" />
        </el-col>
        <el-col :span="12" style="text-align: right">
          <el-link>忘记密码？</el-link>
        </el-col>
      </el-row>
    </div>
    <!--登录/注册按钮-->
    <div style="margin-top: 40px">
      <el-button style="width: 270px" type="success" plain>登录</el-button>
    </div>
    <el-divider>
      <span style="color: grey;font-size: 13px">没有账号</span>
    </el-divider>
    <div>
      <el-button style="width: 270px" type="warning" plain>注册账号</el-button>
    </div>
  </div>
</template>

<style scoped>
.container{
  display: block;
  text-align: center;
  margin: 0 20px
}

</style>
