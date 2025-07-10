import { createApp } from 'vue'
import './style.css'
import TDesign from 'tdesign-mobile-vue';
import App from './App.vue'
import router from './router'
// 引入组件库的少量全局样式变量
import 'tdesign-mobile-vue/es/style/index.css';
// 引入Pinia
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

// 创建Pinia实例
const pinia = createPinia()
// 使用持久化插件
pinia.use(piniaPluginPersistedstate)

const app = createApp(App)
app.use(router)
app.use(TDesign)
// 使用Pinia
app.use(pinia)
app.mount('#app')
