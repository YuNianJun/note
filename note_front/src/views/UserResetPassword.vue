<script setup>
import { ref } from 'vue'
const password = ref({
  "oldPassword":"",
  "password":"",
  "re_pwd":""
})
const rules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    {
      pattern: /^\S{5,12}$/,
      message: '密码必须是5-12位的非空字符串',
      trigger: 'blur'
    }
  ],
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    {
      pattern: /^\S{5,12}$/,
      message: '密码必须是5-12位的非空字符串',
      trigger: 'blur'
    }
  ],
  rePassword: [
    { required: true, message: '请输入确认新密码', trigger: 'blur' },
    {
      pattern: /^\S{5,12}$/,
      message: '密码必须是5-12位的非空字符串',
      trigger: 'blur'
    }
  ]

}
//清空模型数据
const clearData = () => {
  password.value = {
    "oldPassword":"",
    "password":"",
    "re_pwd":""
  }
}

//导入路由
import {useRouter} from 'vue-router'
const router = useRouter();
//导入element提示框组件
import { ElMessage,ElMessageBox } from 'element-plus';
//导入userPasswordUpdateService函数
import {userPasswordUpdateService} from '@/api/user.js'
import { useUserInfoStore } from '@/stores/userInfo.js';
const userInfoStore = useUserInfoStore();
const userPasswordUpdate = async() => {
  ElMessageBox.confirm(
      '确认是否重置密码，重置之后将退出登录',
      '提示',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
  )
      .then(async () => {
        //用户点击了确认
        const params = {
          ...password.value,
          id: userInfoStore.info.id,
        };
        let result = await userPasswordUpdateService(params);
        if(result.code ===200){
          //成功
          //跳转到登录页
          router.push('/login')
          ElMessage.success(result.message ? result.message : '重置成功');
          ElMessage.success('退出登录')
        }else{
          ElMessage.error('操作失败')
        }
      })
      .catch(() => {
        //用户点击了取消
        ElMessage({
          type: 'info',
          message: '取消重置',
        })
      })
}
</script>
<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>重置密码</span>
      </div>
    </template>
    <el-row>
      <el-col :span="12">
        <el-form :model="password" :rules="rules" label-width="100px" size="large">
          <el-form-item label="原密码" prop="oldPassword">
            <el-input type="password" placeholder="请输入原密码" show-password v-model="password.oldPassword"></el-input>
          </el-form-item>
          <el-form-item label="新密码" prop="password">
            <el-input type="password" placeholder="请输入新密码" show-password v-model="password.password"></el-input>
          </el-form-item>
          <el-form-item label="确认新密码" prop="re_pwd">
            <el-input type="password" placeholder="请输入确认密码" show-password v-model="password.re_pwd"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="userPasswordUpdate()">修改密码</el-button>
            <el-button type="primary" @click="clearData()">重置</el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
  </el-card>
</template>