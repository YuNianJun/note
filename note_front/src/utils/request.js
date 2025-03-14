import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { useTokenStore } from '@/stores/token'

const instance = axios.create({
    baseURL: '/api'
})
export const initRequest = () => {
// 请求拦截器
    instance.interceptors.request.use((config) => {
        const tokenStore = useTokenStore()
        const token = tokenStore.token || localStorage.getItem('token')
        if (token) {
            config.headers.Authorization = token;
        }
        return config
    })

// 响应拦截器
    instance.interceptors.response.use(
        (response) => response.data,
        (error) => {
            if (error.response?.status === 401) {
                const tokenStore = useTokenStore()
                tokenStore.clearToken()
                localStorage.removeItem('token')
                router.push('/login')
                ElMessage.warning('会话已过期，请重新登录')
            }
            return Promise.reject(error)
        }
    )
}
export default instance