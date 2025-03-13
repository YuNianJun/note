<script setup>
import { User, Lock } from '@element-plus/icons-vue'
import { ref } from 'vue'
//控制注册与登录表单的显示， 默认显示注册
const isRegister = ref(false)
const registerData = ref({
  username:'',
  password:'',
  rePassword:''
})
//二次校验密码的函数
const checkRePassword = (rule,value,callback) => {
  if(value == ''){
    callback(new Error('请再次确认密码'))
  } else if( value !== registerData.value.password){
    callback('二次确认密码不相同请重新输入')
  } else{
    callback()
  }
}

//定义表单校验规则
const rules = ref({
  username:[
    {required:true,massage:'请输入用户名',trigger:'blur'},
    {min:5,max:16,message:'请输入长度5~16非空字符',trigger:'blur'}
  ],
  password:[
    {required:true,massage:'请输入密码',trigger:'blur'},
    {min:5,max:16,message:'请输入长度5~16非空字符',trigger:'blur'}
  ],
  rePassword:[{validator:checkRePassword,trigger:'blur'}] //校验二次输入密码是否相同
})

import { ElMessage } from 'element-plus'
//调用后台接口完成注册
import {userRegisterService} from '@/api/user.js'
const register = async () => {
  //registerData是一个响应式对象，调用时现需要加上.value
  let result = await userRegisterService(registerData.value);
  if(result.code == 200) {
    //成功
    ElMessage.success('注册成功!')
  }else{
    //失败
    ElMessage.error('注册成功!')
  }
}
//提供调用登录接口的函数
export const userLoginService = (loginData) => {
  //借助UrlSearchParams完成传递
  const params = new URLSearchParams()
  for (let key in loginData){
    params.append(key,loginData[key]);
  }
  return request.post('/user/login',params);
}


//登录函数
import {userLoginService} from '@/api/user.js'
import {request} from "axios";
const login = async () =>{
  //调用接口完成登录
  let result = await userLoginService(registerData.value);
  if(result.code == 200){
    ElMessage.success('注册成功!')
  }else{
    ElMessage.error('注册失败!')
  }
}
//定义函数，清空数据模型
const clearRegisterData = () =>{
  registerData.value = {
    username:'',
    password:'',
    rePassword:''
  }
}
</script>

<template>
  <el-row class="login-page">
    <el-col :span="12" class="bg"></el-col>
    <el-col :span="6" :offset="3" class="form">
      <!-- 注册表单 -->
      <el-form ref="form" size="large" autocomplete="off" v-if="isRegister" :model="registerData">
        <el-form-item>
          <h1>注册</h1>
        </el-form-item>
        <el-form-item>
          <el-input :prefix-icon="User" placeholder="请输入用户名" v-model="registerData.username"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input :prefix-icon="Lock" type="password" placeholder="请输入密码" v-model="registerData.password"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input :prefix-icon="Lock" type="password" placeholder="请输入再次密码" v-model="registerData.rePassword"></el-input>
        </el-form-item>
        <!-- 注册按钮 -->
        <el-form-item>
          <el-button class="button" type="primary" auto-insert-space @click="register">
            注册
          </el-button>
        </el-form-item>
        <el-form-item class="flex">
          <el-link type="info" :underline="false" @click="isRegister = false" ;clearRegisterData()>
            ← 返回
          </el-link>
        </el-form-item>
      </el-form>
      <!-- 登录表单 -->
      <el-form ref="form" size="large" autocomplete="off" v-else>
        <el-form-item>
          <h1>登录</h1>
        </el-form-item>
        <el-form-item>
          <el-input :prefix-icon="User" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input name="password" :prefix-icon="Lock" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item class="flex">
          <div class="flex">
            <el-checkbox>记住我</el-checkbox>
            <el-link type="primary" :underline="false">忘记密码？</el-link>
          </div>
        </el-form-item>
        <!-- 登录按钮 -->
        <el-form-item>
          <el-button class="button" type="primary" auto-insert-space @click="login">登录</el-button>
        </el-form-item>
        <el-form-item class="flex">
          <el-link type="info" :underline="false" @click="isRegister = true">
            注册 →
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