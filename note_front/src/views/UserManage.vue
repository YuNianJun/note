<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {useRouter} from 'vue-router'

import {
  userPageService,
  userUpdateStatusService,
  userInfoUpdateService,
  userInfoGetService
} from '@/api/user.js'
const router = useRouter()

// 权限状态
const hasPermission = ref(false)

// 用户数据
const userList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const totalUsers = ref(0)
const loading = ref(false)

// 分类数据
const categoryDialogVisible = ref(false)
const currentCategories = ref([])

// 初始化检查权限
const checkPermission = async () => {
  try {
    const res = await userInfoGetService()
    console.log('用户权限数据:', res)
    hasPermission.value = res.data.permission > 0
    console.log('hasPermission:', hasPermission.value)
  } catch (error) {
    // 添加错误类型判断
    if (error.response?.status === 403) {
      hasPermission.value = false
    } else {
      ElMessage.error('权限验证失败')
      router.push('/')
    }
  }
}
const handlePageChange = (newPage) => {
  currentPage.value = newPage
  fetchUsers()
}
// 获取用户列表
const fetchUsers = async () => {
  try {
    loading.value = true
    const res = await userPageService({
      current: currentPage.value,
      size: pageSize.value
    })

    // 根据返回数据结构调整
    userList.value = res.data.records
    totalUsers.value = res.data.total
    currentPage.value = res.data.current
    pageSize.value = res.data.size
  } catch (error) {
    ElMessage.error(error.message || '获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 切换用户状态
const toggleUserStatus = async (user) => {
  try {
    // 获取当前用户信息
    const currentUserRes = await userInfoGetService()
    const currentUser = currentUserRes.data

    // 权限验证
    if (user.permission >= currentUser.permission) {
      ElMessage.warning('不能操作同级或更高权限的用户')
      return
    }

    // 禁止自我封禁
    if (user.id === currentUser.id) {
      ElMessage.warning('不能对自己执行此操作')
      return
    }

    await ElMessageBox.confirm(
        `确定要${user.status === 1 ? '封禁' : '解封'}用户 ${user.username} 吗？`,
        '操作确认',
        { type: 'warning' }
    )

    await userUpdateStatusService(user.status === 1 ? 0 : 1)
    ElMessage.success('操作成功')
    await fetchUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  }
}


// 工具函数：格式化日期
const formatDate = (timestamp) => {
  return new Date(timestamp).toLocaleString()
}

// 初始化
onMounted(async () => {
  await checkPermission()
  if (hasPermission.value) {
    await fetchUsers()
  }
})
const refreshData = async () => {
  await checkPermission()
  if (hasPermission.value) {
    await fetchUsers()
  }
}
</script>


<template>
  <!-- 权限控制容器 -->
  <div v-if="hasPermission" class="admin-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" @click="refreshData">刷新数据</el-button>
        </div>
      </template>

      <!-- 用户列表表格 -->
      <el-table
          :data="userList"
          stripe
          style="width: 100%"
          v-loading="loading"
      >
        <el-table-column
            prop="username"
            label="用户名"
            width="180"
            :resizable="false"
            fixed="left"
        />
        <el-table-column
            prop="email"
            label="邮箱"
            width="220"
            :show-overflow-tooltip="true"
        />
        <el-table-column
            prop="createTime"
        label="注册时间"
        width="200"
        align="center"
        >
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
        </el-table-column>
        <el-table-column prop="status" label="账号状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '已封禁' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" align="center">
          <template #default="{ row }">
            <el-button
                size="small"
                @click="showUserCategories(row.id)"
            >
              查看分类
            </el-button>
            <el-button
                size="small"
                :type="row.status === 1 ? 'danger' : 'success'"
                @click="toggleUserStatus(row)"
            >
              {{ row.status === 1 ? '封禁' : '解封' }}
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

    <!-- 分类弹窗 -->
    <el-dialog
        v-model="categoryDialogVisible"
        title="用户笔记分类"
        width="60%"
    >
      <el-table :data="currentCategories">
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="note_count" label="笔记数量" width="120" />
        <el-table-column prop="created_at" label="创建时间" width="180">
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
.el-table .cell {
  white-space: nowrap;
}

.el-table th.el-table__cell > .cell {
  padding: 0 8px;
}
</style>