<script setup>
import {User, Lock, Message, EditPen} from "@element-plus/icons-vue";
import router from "@/router";
import {reactive, ref} from "vue";
import {ElMessage} from "element-plus";
import {post} from "@/net";

const form = reactive({
  email: "",
  code: "",
  username: "",
  password: "",
  password_repeat: ""
})

//校验“用户名输入框”不能有特殊字符
const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入用户名'));
  } else {
    //用正则表达式判断更方便。（以下正则表达式含义：包含中文英文的用户名，不能有特殊字符）
    if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
      callback(new Error('用户名不能包含特殊字符，只能是中文/英文'));
    } else {
      callback();
    }
  }
};

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'));
  } else if (value !== form.password) {
    callback(new Error('两次输入密码不一致!'));
  } else {
    callback();
  }
};
//制定校验规则
const rules = {
  username: [
    {validator: validateUsername, trigger: ['blur', 'change']},
    {min: 2, max: 8, message: '用户名长度必须在2~8个字符之间', trigger: ['blur', 'change']}
  ],
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 6, max: 16, message: '密码长度必须在6~16个字符之间', trigger: ['blur', 'change']}
  ],
  password_repeat: [
    {validator: validatePassword, trigger: ['blur', 'change']},
  ],
  email: [
    {required: true, message: '请输入邮箱地址', trigger: 'blur'},
    {type: 'email', message: '请输入合法的电子邮箱地址', trigger: ['blur', 'change']}
  ],
  code: [
    {required: true, message: '请输入获取的验证码', trigger: 'blur'}
  ]
}
//定义对整个表单进行响应的变量
const formRef = ref()
//判断邮箱地址是否有效（默认无效），有效才能激发“获取验证码”按钮
const isEmailValid = ref(false)
//发送验证码的冷却时间
const coldTime = ref(0)

const onValidate = (prop, isValid) => {
  //如果更新的属性是email
  if (prop === 'email')
    isEmailValid.value = isValid//isVaild返回的是该属性是否校验通过
}

//绑定给注册按钮的
const register = () => {

}

//向后端发送对应路径的post请求（发送验证码请求），携带1个参数
const validateEmail = () => {

}

</script>

<template>
  <div style="text-align: center;margin: 0 20px">
    <!--标题-->
    <div style="margin-top: 150px">
      <div style="font-size: 30px;font-weight: bold">注册新用户</div>
      <div style="font-size: 14px;color:grey">欢迎注册，请在下方填写相关信息</div>
    </div>
    <!--表单-->
    <div style="margin-top: 50px">
      <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" :maxlength="8" type="text" placeholder="用户名">
            <!--给输入框引入一个图标-->
            <template #prefix>
              <el-icon>
                <User/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" :maxlength="16" type="password" style="margin-top: 10px" placeholder="密码">
            <template #prefix>
              <el-icon>
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password_repeat">
          <el-input v-model="form.password_repeat" :maxlength="16" type="password" style="margin-top: 10px"
                    placeholder="再次输入密码">
            <template #prefix>
              <el-icon>
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="form.email" type="email" style="margin-top: 10px" placeholder="电子邮箱地址">
            <template #prefix>
              <el-icon>
                <Message/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="code">
          <div style="margin-top: 10px">
            <el-row :gutter="10" style="width: 100%">
              <el-col :span="18">
                <el-input v-model="form.code" type="text" placeholder="请输入验证码">
                  <template #prefix>
                    <el-icon>
                      <EditPen/>
                    </el-icon>
                  </template>
                </el-input>
              </el-col>
              <el-col :span="6">
                <el-button type="success" :maxlength="6" @click="validateEmail"
                           :disabled="!isEmailValid || coldTime > 0">
                  {{ coldTime > 0 ? '请稍后' + coldTime + '秒' : '获取验证码' }}
                </el-button>
              </el-col>
            </el-row>
          </div>
        </el-form-item>
      </el-form>
    </div>
    <!--注册按钮-->
    <div style="margin-top: 60px">
      <el-button style="width: 270px" type="warning" plain @click="register">注册</el-button>
    </div>
    <div style="margin-top: 20px">
      <span style="font-size: 14px;line-height: 15px;color: grey">已有帐号？</span>
      <el-link type="primary" style="translate: 0 -2px" @click="router.push('/')">立即登录</el-link>
    </div>
  </div>
</template>

<style scoped>

</style>
