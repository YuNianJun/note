import { defineStore } from 'pinia'

export const useTokenStore = defineStore('token', {
    state: () => ({
        token: localStorage.getItem('token') || ''
    }),
    actions: {
        setToken(token) {
            this.token = token
        },
        clearToken() {
            this.token = ''
            localStorage.removeItem('token');
        }
    }
})