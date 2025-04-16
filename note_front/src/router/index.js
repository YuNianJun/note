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
import NoteSquare from "@/views/NoteSquare.vue";

//定义路由关系
const routes = [
    {path:'/login',component:LoginVue},
    {path:'/',component:LayoutVue,
        //重定向 为首页页面默认展示的子路由页面
        redirect: '/note/manage',
        //子路由
        children: [
            { path: '/note/category', component: NoteCategoryVue },
            { path: '/note/manage', component: NoteManageVue },
            { path: '/note/recycle', component: NoteRecycleBin },
            { path: '/note/square', component: NoteSquare},
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