<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useRouter } from 'vue-router';

import {
  userPageService,
  userUpdateStatusService,
  userInfoGetService,
  userCreateService,
  userInfoUpdateService,
} from '@/api/user.js';
const router = useRouter();

// 权限状态
const hasPermission = ref(false);
const currentUserPermission = ref(0);

// 用户数据
const userList = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const totalUsers = ref(0);
const loading = ref(false);

// 分类数据
const categoryDialogVisible = ref(false);
const currentCategories = ref([]);

const userFormRef = ref(null);
// 用户表单相关
const userModalVisible = ref(false); // 控制用户表单弹窗显示
const isEditMode = ref(false); // 标识是新增还是编辑
const formLoading = ref(false); // 表单提交时的加载状态
const userForm = reactive({
  id: null, // 用户 ID（编辑时使用）
  username: '',
  email: '',
  password: '', // 新增时使用
  phone: '',
  status: 1, // 账号状态，默认为正常
  permission: 0,// 用户权限，默认为 0（普通用户）
  userPic: null, // 用户头像
});
const rules = reactive({
  username: [
    { required: true, message: '用户名不能为空', trigger: 'blur' },
  ],
  email: [
    { required: true, message: '邮箱不能为空', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '密码不能为空', trigger: 'blur' },
  ],
});

// 初始化检查权限
const checkPermission = async () => {
  try {
    const res = await userInfoGetService();
    console.log('用户权限数据:', res);
    hasPermission.value = res.data.permission > 0;
    currentUserPermission.value = res.data.permission; // 获取当前用户权限级别
    console.log('hasPermission:', hasPermission.value);
  } catch (error) {
    if (error.response?.status === 403) {
      hasPermission.value = false;
    } else {
      ElMessage.error('权限验证失败');
      router.push('/');
    }
  }
};

// 获取用户列表
const fetchUsers = async () => {
  try {
    loading.value = true;
    const res = await userPageService({
      current: currentPage.value,
      size: pageSize.value,
    });
    userList.value = res.data.records;
    totalUsers.value = res.data.total;
    currentPage.value = res.data.current;
    pageSize.value = res.data.size;
  } catch (error) {
    ElMessage.error(error.message || '获取用户列表失败');
  } finally {
    loading.value = false;
  }
};

// 切换用户状态
const toggleUserStatus = async (user) => {
  try {
    if (user.permission >= currentUserPermission.value) {
      ElMessage.error('您无权封禁此用户');
      return;
    }
    await ElMessageBox.confirm(
        `确定要${user.status === 1 ? '封禁' : '解封'}用户 ${user.username} 吗？`,
        '操作确认',
        { type: 'warning' }
    );
    await userUpdateStatusService(user.id, user.status === 1 ? 0 : 1);
    ElMessage.success('操作成功');
    await fetchUsers();
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败');
    }
  }
};

// 格式化日期
const formatDate = (timestamp) => {
  if (!timestamp) {
    return '';
  }
  const date = new Date(timestamp);
  if (isNaN(date.getTime())) {
    console.error(`Invalid date format: ${timestamp}`);
    return '无效日期';
  }
  return date.toLocaleString();
};

// 显示创建用户表单
const showCreateModal = async () => {
  isEditMode.value = false;
  userForm.id = null; // 清空表单
  userForm.username = '';
  userForm.email = '';
  userForm.password = '';
  userForm.phone = '';
  userForm.status = 1;
  userModalVisible.value = true;
};

// 显示编辑用户表单
const showEditModal = async (user) => {
  isEditMode.value = true;
  userForm.id = user.id;
  userForm.username = user.username;
  userForm.phone = user.phone;
  userForm.email = user.email;
  userForm.status = user.status;
  userModalVisible.value = true;
};

// 关闭弹窗
const closeModal = () => {
  userModalVisible.value = false;
  // 重置表单
  userForm.id = null;
  userForm.username = '';
  userForm.email = '';
  userForm.phone = '';
  userForm.password = '';

  userForm.status = 1;
};

// 保存用户
const saveUser = async () => {
  try {
    const formRef = userFormRef.value; // 通过 .value 访问 ref 的值
    if (!formRef) return;

    const valid = await formRef.validate();
    if (valid) {
      formLoading.value = true; // 增加表单加载状态

      try {
        if (isEditMode.value) {
          // 编辑用户
          await userInfoUpdateService({
            id: userForm.id,
            username: userForm.username,
            email: userForm.email,
            phone: userForm.phone,
            status: userForm.status,
            permission: userForm.permission,
          });
          ElMessage.success('用户信息已更新');
        } else {
          // 新增用户
          await userCreateService({
            username: userForm.username,
            email: userForm.email,
            phone: userForm.phone,
            password: userForm.password,
            status: userForm.status,
            permission: userForm.permission,
          });
          ElMessage.success('用户已创建');
        }

        await fetchUsers(); // 刷新用户列表
        closeModal(); // 关闭弹窗
      } catch (error) {
        // 捕获后端 API 错误
        console.error('后端 API 错误:', error);
        ElMessage.error('未知错误，新增/修改用户失败');
      } finally {
        formLoading.value = false; // 结束加载状态
      }
    }
  } catch (error) {
    // 捕获表单验证错误
    console.error('表单验证错误:', error);
    ElMessage.error('表单验证失败');
  }
};

// 初始化
onMounted(async () => {
  await checkPermission();
  if (hasPermission.value) {
    await fetchUsers();
  }
});

const refreshData = async () => {
  await checkPermission();
  if (hasPermission.value) {
    await fetchUsers();
  }
};
</script>
<template>
<div v-if="hasPermission" class="admin-container">
<el-card class="box-card">
  <template #header>
    <div class="card-header">
      <span>用户管理</span>
      <el-button type="primary" @click="showCreateModal">新增用户</el-button>
    </div>
  </template>

  <!-- 用户列表表格 -->
  <el-table
      :data="userList"
      stripe
      style="width: 100%"
      v-loading="loading"
  >
    <!-- 用户名列 -->
    <el-table-column
        prop="username"
        label="用户名"
        min-width="180"
        fixed
    />
    <el-table-column
        prop="permission"
        label="用户权限"
        min-width="120"
    >
      <template #default="{ row }">
        <el-tag :type="row.permission === 1 ? 'primary' : 'info'">
          {{ row.permission === 1 ? '管理员' : '普通用户' }}
        </el-tag>
      </template>
    </el-table-column>
    <!-- 邮箱列 -->
    <el-table-column
        prop="email"
        label="邮箱"
        min-width="220"
    />

    <!-- 注册时间列 -->
    <el-table-column
        prop="createTime"
        label="注册时间"
        min-width="200"
    >
      <template #default="{ row }">
        {{ formatDate(row.createTime) }}
      </template>
    </el-table-column>

    <!-- 账号状态列 -->
    <el-table-column
        prop="status"
        label="账号状态"
        min-width="120"
    >
      <template #default="{ row }">
        <el-tag :type="row.status === 1 ? 'success' : 'danger'">
          {{ row.status === 1 ? '正常' : '已封禁' }}
        </el-tag>
      </template>
    </el-table-column>

    <!-- 操作列 -->
    <el-table-column
        label="操作"
        min-width="260"
    >
      <template #default="{ row }">
        <el-button
            size="small"
            :type="row.status === 1 ? 'danger' : 'success'"
            @click="toggleUserStatus(row)"
        >
          {{ row.status === 1 ? '封禁' : '解封' }}
        </el-button>
        <el-button
            size="small"
            type="primary"
            @click="showEditModal(row)"
        >
          修改
        </el-button>
      </template>
    </el-table-column>
  </el-table>

  <!-- 分页组件 -->
  <div class="pagination">
    <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="totalUsers"
        layout="total, prev, pager, next, jumper"
        @current-change="handlePageChange"
    />
  </div>
</el-card>

<!-- 新增/编辑用户弹窗 -->
<el-dialog
    v-model="userModalVisible"
    :title="isEditMode ? '修改用户' : '新增用户'"
    width="40%"
>
  <el-form :model="userForm" label-width="100px" :rules="rules" ref="userFormRef">
    <el-form-item label="用户名" prop="username">
      <el-input v-model="userForm.username"></el-input>
    </el-form-item>
    <el-form-item label="邮箱" prop="email">
      <el-input v-model="userForm.email"></el-input>
    </el-form-item>
    <el-form-item label="手机号" prop="phone">
      <el-input v-model="userForm.phone"></el-input>
    </el-form-item>
    <el-form-item label="密码" prop="password" v-if="!isEditMode">
      <el-input type="password" v-model="userForm.password"></el-input>
    </el-form-item>
    <el-form-item label="用户权限">
      <el-radio-group v-model="userForm.permission">
        <el-radio :label="0">普通用户</el-radio>
        <el-radio :label="1">管理员</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="账号状态">
      <el-radio-group v-model="userForm.status">
        <el-radio :label="1">正常</el-radio>
        <el-radio :label="0">已封禁</el-radio>
      </el-radio-group>
    </el-form-item>
  </el-form>

  <template #footer>
    <el-button @click="closeModal">取消</el-button>
    <el-button type="primary" @click="saveUser" :loading="formLoading">保存</el-button>
  </template>
</el-dialog>

<!-- 分类弹窗 -->
<el-dialog
    v-model="categoryDialogVisible"
    title="用户笔记分类"
    width="60%"
>
  <el-table :data="currentCategories">
    <el-table-column prop="name" label="分类名称" />
    <el-table-column prop="note_count" label="笔记数量" width="120" />
    <el-table-column
        prop="createTime"
        label="创建时间"
        width="180"
    >
      <template #default="{ row }">
        {{ formatDate(row.createTime) }}
      </template>
    </el-table-column>
  </el-table>
</el-dialog>
</div>

<!-- 无权限提示 -->
<div v-else class="no-permission">
<el-alert
    title="权限不足"
    type="error"
    description="您没有权限访问此页面"
    show-icon
    :closable="false"
/>
</div>
</template>



<style scoped>
.admin-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.no-permission {
  max-width: 600px;
  margin: 100px auto;
}
</style>