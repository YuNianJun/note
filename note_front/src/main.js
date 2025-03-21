import'./assets/main.scss'
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import locale from 'element-plus/dist/locale/zh-cn'

const app = createApp(App)

app.use(ElementPlus,{locale})
app.mount('#app')

