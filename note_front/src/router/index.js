import {createRouter,createWebHistory} from 'vue-router';

//导入组件
import LoginVue from '@/views/Login.vue'
import LayoutVue from '@/views/Layout.vue';

//导入子路由组件
import NoteCategoryVue from '@/views/NoteCategory.vue'
import NoteManageVue from '@/views/NoteManage.vue'
import UserAvatarVue from '@/views/UserAvatar.vue'
import UserInfoVue from '@/views/UserInfo.vue'
import UserResetPasswordVue from '@/views/UserResetPassword.vue'
import UserManageVue from "@/views/UserManage.vue";
import {useTokenStore} from "@/stores/token";
import NoteRecycleBin from "@/views/NoteRecycleBin.vue";

//定义路由关系
const routes = [
    {path:'/login',component:LoginVue},
    {path:'/',component:LayoutVue,
        //重定向 为首页页面默认展示的子路由页面
        redirect: '/article/manage',
        //子路由
        children: [
            { path: '/article/category', component: NoteCategoryVue },
            { path: '/article/manage', component: NoteManageVue },
            { path: '/article/recycle', component: NoteRecycleBin },
            { path: '/user/info', component: UserInfoVue },
            { path: '/user/avatar', component: UserAvatarVue },
            { path: '/user/password', component: UserResetPasswordVue },
            { path: '/user/manage', component: UserManageVue}
        ]
    }
]

//创建路由器
const router = createRouter({
    history:createWebHistory(),
    routes:routes
})

// 路由守卫：检查是否需要登录
router.beforeEach((to, from, next) => {
    const tokenStore = useTokenStore()
    if (to.meta.requiresAuth && !tokenStore.token) {
        next('/login') // 未登录时跳转到登录页
    } else {
        next() // 已登录时继续导航
    }
})
//导出路由
export default router