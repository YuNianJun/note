<script setup>
import { ref, onMounted } from 'vue';
import { useUserInfoStore } from '@/stores/userInfo.js';
import { userInfoUpdateService } from '@/api/user.js';
import { ElMessage } from 'element-plus';
import { userInfoGetService } from '@/api/user.js';
const userInfoStore = useUserInfoStore();
const userInfo = ref({ ...userInfoStore.info }); // 初始化 userInfo

// 确保数据加载完成
onMounted(async () => {
  if (!userInfoStore.info || Object.keys(userInfoStore.info).length === 0) {
    const result = await userInfoGetService();
    if (result.code === 200) {
      userInfoStore.setInfo(result.data);
      userInfo.value = { ...result.data };
    } else {
      ElMessage.error('获取用户信息失败');
    }
  }
});
const rules = {
  phone: [
    { required: true, message: '请输入用户手机号', trigger: 'blur' },
    {
      pattern: /^\S{2,10}$/,
      message: '手机号必须要由11位纯数字构成',
      trigger: 'blur'
    }
  ],
  email: [
    { required: true, message: '请输入用户邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
};

const updateUserInfo = async () => {
  let result = await userInfoUpdateService(userInfo.value);
  if (result.code === 200) {
    ElMessage.success(result.message ? result.message : '修改成功');
    userInfoStore.setInfo(userInfo.value);
  } else {
    ElMessage.error('修改失败');
  }
};
</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>基本资料</span>
      </div>
    </template>
    <el-row>
      <el-col :span="12">
        <el-form :model="userInfo" :rules="rules" label-width="100px" size="large">
          <el-form-item label="登录名称">
            <el-input v-model="userInfo.username" disabled></el-input>
          </el-form-item>
          <el-form-item label="用户手机号" prop="nickname">
            <el-input v-model="userInfo.phone"></el-input>
          </el-form-item>
          <el-form-item label="用户邮箱" prop="email">
            <el-input v-model="userInfo.email"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="updateUserInfo">提交修改</el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
  </el-card>
</template>
