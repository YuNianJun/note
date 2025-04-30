import { defineStore } from 'pinia'

export const useTokenStore = defineStore('token', {
    state: () => ({
        token: localStorage.getItem('token') || '',
        permission: parseInt(localStorage.getItem('permissions')) || 0,
        userId: localStorage.getItem('userId') || null
    }),
    actions: {
        setToken(token) {
            this.token = token;
            localStorage.setItem('token', token);
        },
        setPermission(permissions) {
            this.permission = permissions; // 更新 state
            localStorage.setItem('permissions', permissions.toString()); // 存储为字符串
        },
        setUserId(userId) {
            this.userId = userId;
            localStorage.setItem('userId', userId)
        },
        clearToken() {
            this.token = '';
            this.permission = 0;
            this.userId = null;
            localStorage.removeItem('token');
            localStorage.removeItem('permissions');
            localStorage.removeItem('userId');
        }
    }
})