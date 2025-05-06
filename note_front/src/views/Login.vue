<script setup>
import {User, Lock} from '@element-plus/icons-vue'
import {ref} from 'vue'
import {ElMessage} from 'element-plus'
import {userRegisterService, userLoginService} from '@/api/user.js'
import {useTokenStore} from "@/stores/token";

//控制注册与登录表单的显示， 默认显示注册
const isRegister = ref(false)

//注册相关数据
const registerData = ref({
  username: '',
  password: '',
  rePassword: ''
})
// 登录相关数据
const loginData = ref({
  username: '',
  password: ''
})

//二次校验密码的函数
const checkRePassword = (rules, value, callback) => {
  if (value === '') {
    callback(new Error('请再次确认密码'))
  } else if (value !== registerData.value.password) {
    callback('二次确认密码不相同请重新输入')
  } else {
    callback()
  }
}

//定义表单校验规则
const rules = ref({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 16, message: '长度2~16个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 5, max: 16, message: '长度5~16个字符', trigger: 'blur' }
  ],
  rePassword: [{ validator: checkRePassword, trigger: 'blur' }]
})


//调用后台接口完成注册
const register = async () => {
  //registerData是一个响应式对象，调用时现需要加上.value
  try {
    const res = await userRegisterService(registerData.value)
    if (res.code === 200) {
      ElMessage.success('注册成功')
      isRegister.value = false
      clearData()
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '注册失败')
  }
}
import {useRouter} from "vue-router";
const router = useRouter()
//导入token
const tokenStore = useTokenStore()
//提供调用登录接口的函数
const login = async () => {
  try {
    const res = await userLoginService({
      username: loginData.value.username,
      password: loginData.value.password
    })
    if (res.code === 200) {
      ElMessage.success('登录成功')
      // 加登录成功后的处理
      tokenStore.setToken(res.data)
      tokenStore.setUserId(res.data.id)
      localStorage.setItem('token', res.data);
      localStorage.setItem('username', loginData.value.username)
      localStorage.setItem('id', res.data.id)
      console.log('跳转到主页')
      router.push('/')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '登录失败')
  }
}
//定义函数，清空数据模型
const clearData = () => {
  registerData.value = { username: '', password: '', rePassword: '' }
  loginData.value = { username: '', password: '' }
}
</script>

<template>
  <el-row class="login-page">
    <el-col :span="12" class="bg"></el-col>
    <el-col :span="6" :offset="3" class="form">
      <!-- 注册表单 -->
      <el-form
          v-if="isRegister"
          :model="registerData"
          :rules="rules"
          size="large"
          autocomplete="off"
      >
        <el-form-item>
          <h1>注册</h1>
        </el-form-item>
        <el-form-item prop="username">
          <el-input
              v-model="registerData.username"
              :prefix-icon="User"
              placeholder="请输入用户名"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
              v-model="registerData.password"
              :prefix-icon="Lock"
              type="password"
              placeholder="请输入密码"
          />
        </el-form-item>
        <el-form-item prop="rePassword">
          <el-input
              v-model="registerData.rePassword"
              :prefix-icon="Lock"
              type="password"
              placeholder="请再次输入密码"
          />
        </el-form-item>
        <el-form-item>
          <el-button class="button" type="primary" @click="register">
            注册
          </el-button>
        </el-form-item>
        <el-form-item class="flex">
          <el-link @click="isRegister = false; clearData()">
            ← 返回登录
          </el-link>
        </el-form-item>
      </el-form>

      <!-- 登录表单 -->
      <el-form
          v-else
          :model="loginData"
          size="large"
          autocomplete="off"
      >
        <el-form-item>
          <h1>登录</h1>
        </el-form-item>
        <el-form-item>
          <el-input
              v-model="loginData.username"
              :prefix-icon="User"
              placeholder="请输入用户名"
          />
        </el-form-item>
        <el-form-item>
          <el-input
              v-model="loginData.password"
              :prefix-icon="Lock"
              type="password"
              placeholder="请输入密码"
          />
        </el-form-item>
        <el-form-item class="flex">
          <div class="flex">
            <el-checkbox>记住我</el-checkbox>
            <el-link type="primary">忘记密码？</el-link>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button class="button" type="primary" @click="login">
            登录
          </el-button>
        </el-form-item>
        <el-form-item class="flex">
          <el-link @click="isRegister = true">
            注册新账号 →
          </el-link>
        </el-form-item>
      </el-form>
    </el-col>
  </el-row>
</template>

<style lang="scss" scoped>
/* 样式 */
.login-page {
  height: 100vh;
  background-color: #fff;

  .bg {
    background: no-repeat 60% center / 240px auto,
    url('@/assets/login_bg2.jpg') no-repeat center / cover;
    border-radius: 0 20px 20px 0;
  }

  .form {
    display: flex;
    flex-direction: column;
    justify-content: center;
    user-select: none;

    .title {
      margin: 0 auto;
    }

    .button {
      width: 100%;
    }

    .flex {
      width: 100%;
      display: flex;
      justify-content: space-between;
    }
  }
}
</style>