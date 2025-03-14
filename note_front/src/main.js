// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import './assets/main.scss'
import { createApp } from 'vue'
import ElementPlus, {ElMessage} from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import locale from 'element-plus/dist/locale/zh-cn'
import router from './router'
import {createPinia} from "pinia";
import {createPersistedState} from "pinia-persistedstate-plugin";
import {initRequest} from "@/utils/request";

const app = createApp(App)
const pinia = createPinia();
const persist = createPersistedState()
app.config.errorHandler = (err, _vm, _info) => {
    console.error('全局错误:', err);
    ElMessage.error('发生未知错误，请刷新页面或联系管理员');
};

// pinia持久化插件
pinia.use(persist)
app.use(pinia)
initRequest()
app.use(ElementPlus, { locale })
app.use(router)
app.mount('#app')

