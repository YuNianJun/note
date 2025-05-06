import { defineStore } from 'pinia'
import { userInfoGetService } from '@/api/user.js';

export const useTokenStore = defineStore('token', {
    state: () => ({
        token: (userInfoGetService.info || {}).token || localStorage.getItem('token') || '',
        permission: parseInt((userInfoGetService.info || {}).permissions || localStorage.getItem('permissions')) || 0,
        userId: (userInfoGetService.info || {}).userId || localStorage.getItem('userId') || null
    }),
    actions: {
        setToken(token) {
            this.token = token;
            localStorage.setItem('token', token);
        },
        setPermission(permissions) {
            this.permission = permissions; // 更新 state
            localStorage.setItem('permissions', permissions);
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