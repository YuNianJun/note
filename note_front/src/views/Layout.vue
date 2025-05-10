<script setup>
import {
  Management,
  Promotion,
  UserFilled,
  User,
  Avatar,
  Crop,
  EditPen,
  SwitchButton,
  DeleteFilled,
  CaretBottom,
  ChatRound
} from '@element-plus/icons-vue'
import avatar from '@/assets/default.png'
import {useRouter} from 'vue-router'
const router = useRouter();
import {ElMessage,ElMessageBox} from 'element-plus'
import { useTokenStore } from '@/stores/token.js'

const tokenStore = useTokenStore()
//导入接口函数
import {userInfoGetService, userLogoutService} from '@/api/user.js'
//导入pinia
import {useUserInfoStore} from '@/stores/userInfo.js'
import {computed} from "vue";
const userInfoStore = useUserInfoStore();
const username = computed(() => localStorage.getItem('username'));
// 获取个人信息
const getUserInf = async () => {
  const username = localStorage.getItem('username')
  if (!username) {
    // 如果 username 不存在，跳转到登录页
    await router.push('/login')
    ElMessage.warning('username 不存在,请先登录')
    return
  }

  try {
    let result = await userInfoGetService()
    if (result.code === 200) {
      console.log('获取result用户信息成功', result)
      userInfoStore.$patch({ info: result.data })
      localStorage.setItem('token', result.data);
      localStorage.setItem('username', result.data.username)
      localStorage.setItem('userId', result.data.id)
      localStorage.setItem('permission', result.data.permission)
      console.log('获取localStorage用户信息成功', localStorage)
    } else {
      alert('获取信息失败')
    }
  } catch (error) {
    if (error.response && error.response.status === 401) {
      // token 过期或无效，跳转到登录页
      await router.push('/login')
      ElMessage.warning('token 过期或无效,请先登录')
    } else {
      console.error('获取信息失败', error)
    }
  }
}

getUserInf()
const handleCommand = (command) => {
  if (command === 'logout') {
    // 退出登录确认弹窗
    ElMessageBox.confirm(
        '是否退出登录',
        '提示',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
        }
    )
        .then(async () => {
          // 用户点击了确认
          // 清空 Pinia 中的 token 和个人信息
          userInfoStore.removeInfo()
          tokenStore.clearToken();
          await userLogoutService();
          // 清空 localStorage 中的用户信息
          localStorage.removeItem('username')
          localStorage.removeItem('token')

          // 跳转到登录页
          await router.push('/login')
          ElMessage.success('成功退出登录')
        })
        .catch(() => {
          console.log('用户点击了取消')
        })
  } else {
    // 其他命令，跳转到对应路由
    router.push('/user/' + command)
  }
}

</script>

<template>
  <el-container class="layout-container">
    <!-- 左侧菜单 -->
    <el-aside width="200px">
      <div class="el-aside__logo"></div>
      <el-menu active-text-color="#ffd04b" background-color="#232323"  text-color="#fff"
               router>
        <el-menu-item index="/note/category">
          <el-icon>
            <Management />
          </el-icon>
          <span>笔记分类</span>
        </el-menu-item>
        <el-menu-item index="/note/manage">
          <el-icon>
            <Promotion />
          </el-icon>
          <span>笔记管理</span>
        </el-menu-item>
        <el-menu-item index="/note/square">
          <el-icon>
            <ChatRound />
          </el-icon>
          <span>笔记广场</span>
        </el-menu-item>
        <el-menu-item index="/note/recycle">
          <el-icon>
            <DeleteFilled />
          </el-icon>
          <span>回收站</span>
        </el-menu-item>
        <el-menu-item index="/user/manage" v-if="userInfoStore.info.permission >= 1">
          <el-icon>
            <Avatar />
          </el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-sub-menu >
          <template #title>
            <el-icon>
              <UserFilled />
            </el-icon>
            <span>个人中心</span>
          </template>
          <el-menu-item index="/user/info">
            <el-icon>
              <User />
            </el-icon>
            <span>基本资料</span>
          </el-menu-item>
          <el-menu-item index="/user/avatar">
            <el-icon>
              <Crop />
            </el-icon>
            <span>更换头像</span>
          </el-menu-item>
          <el-menu-item index="/user/password">
            <el-icon>
              <EditPen />
            </el-icon>
            <span>重置密码</span>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    <!-- 右侧主区域 -->
    <el-container>
      <!-- 头部区域 -->
      <el-header>

        <div>用户：<strong>{{ username }}</strong></div>

        <el-dropdown placement="bottom-end" @command="handleCommand">
                    <span class="el-dropdown__box">
                        <el-avatar :src="userInfoStore.info.userPic ? userInfoStore.info.userPic : avatar" />
                        <el-icon>
                            <CaretBottom />
                        </el-icon>
                    </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="info" :icon="User">基本资料</el-dropdown-item>
              <el-dropdown-item command="avatar" :icon="Crop">更换头像</el-dropdown-item>
              <el-dropdown-item command="password" :icon="EditPen">重置密码</el-dropdown-item>
              <el-dropdown-item command="logout" :icon="SwitchButton">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <!-- 中间区域 -->
      <el-main>
        <router-view/>
      </el-main>
      <!-- 底部区域 -->
      <el-footer>夜雨 ©2025</el-footer>
    </el-container>
  </el-container>
</template>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;

  .el-aside {
    background-color: #232323;

    &__logo {
      height: 120px;
      background: url('@/assets/logo.png') no-repeat center / 200% auto; /* 放大图片 */
      background-position: 50% 50%;
    }

    .el-menu {
      border-right: none;
    }
  }

  .el-header {
    background-color: #fff;
    display: flex;
    align-items: center;
    justify-content: space-between;

    .el-dropdown__box {
      display: flex;
      align-items: center;

      .el-icon {
        color: #999;
        margin-left: 10px;
      }

      &:active,
      &:focus {
        outline: none;
      }
    }
  }

  .el-footer {
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    color: #666;
  }
}
</style>