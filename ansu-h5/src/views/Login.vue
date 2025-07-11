<template>
  <div class="login-container">
    <div class="login-card">
      <h1 class="app-title">应用系统</h1>
      
      <div class="form-group">
        <label for="username">用户名</label>
        <input
          type="text"
          id="username"
          v-model="username"
          placeholder="用户名 或 用户名@租户编码"
          @keyup.enter="focusPassword"
        />
        <small class="help-text">支持格式：username 或 username@tenantcode</small>
      </div>
      
      <div class="form-group">
        <label for="password">密码</label>
        <input 
          type="password" 
          id="password" 
          v-model="password" 
          placeholder="请输入密码"
          @keyup.enter="handleLogin"
          ref="passwordInput"
        />
      </div>

      <div class="remember-me">
        <label class="checkbox-label">
          <input
            type="checkbox"
            v-model="rememberMe"
          />
          <span class="checkmark"></span>
          记住我
        </label>
      </div>

      <button
        class="login-button"
        :class="{ loading: isLoading }"
        @click="handleLogin"
        :disabled="isLoading"
      >
        {{ isLoading ? '登录中...' : '登录' }}
      </button>

      <div class="register-link">
        <span>还没有账号？</span>
        <a href="#" @click.prevent="goToRegister">立即注册</a>
      </div>

      <div class="demo-accounts">
        <h4>演示账号</h4>
        <div class="demo-item" @click="fillDemoAccount('demo1@DEMO001')">
          <span>演示公司A：demo1@DEMO001</span>
        </div>
        <div class="demo-item" @click="fillDemoAccount('demo2@DEMO002')">
          <span>演示公司B：demo2@DEMO002</span>
        </div>
        <small>密码均为：admin123</small>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/userStore'
import { Toast } from 'tdesign-mobile-vue'

const router = useRouter()
const userStore = useUserStore()

const username = ref('')
const password = ref('')
const rememberMe = ref(false)
const isLoading = ref(false)
const passwordInput = ref(null)

// 页面加载时检查是否有保存的登录信息
const loadSavedCredentials = () => {
  const savedUsername = localStorage.getItem('savedUsername')
  const savedRememberMe = localStorage.getItem('rememberMe') === 'true'

  if (savedUsername && savedRememberMe) {
    username.value = savedUsername
    rememberMe.value = true
  }
}

// 页面加载时调用
loadSavedCredentials()

const focusPassword = () => {
  passwordInput.value.focus()
}

const handleLogin = async () => {
  if (!username.value || !password.value) {
    Toast({
      message: '请输入用户名和密码',
      theme: 'warning'
    })
    return
  }

  isLoading.value = true

  try {
    const result = await userStore.login({
      username: username.value,
      password: password.value
    })

    if (result.success) {
      // 处理记住我功能
      if (rememberMe.value) {
        localStorage.setItem('savedUsername', username.value)
        localStorage.setItem('rememberMe', 'true')
      } else {
        localStorage.removeItem('savedUsername')
        localStorage.removeItem('rememberMe')
      }

      Toast({
        message: result.message,
        theme: 'success'
      })
      router.push('/home')
    } else {
      Toast({
        message: result.message,
        theme: 'error'
      })
    }
  } catch (error) {
    console.error('登录失败:', error)
    Toast({
      message: '登录失败，请稍后重试',
      theme: 'error'
    })
  } finally {
    isLoading.value = false
  }
}

const goToRegister = () => {
  router.push('/register')
}

const fillDemoAccount = (demoUsername) => {
  username.value = demoUsername
  password.value = 'admin123'
}
</script>

<style scoped>
.register-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #666;
}

.register-link a {
  color: #0052d9;
  text-decoration: none;
  margin-left: 5px;
}

.register-link a:hover {
  text-decoration: underline;
}

.forgot-password-link {
  text-align: center;
  margin-bottom: 15px;
  font-size: 14px;
}

.forgot-password-link a {
  color: #0052d9;
  text-decoration: none;
}

.forgot-password-link a:hover {
  text-decoration: underline;
}

.remember-me {
  margin-bottom: 20px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  cursor: pointer;
  font-size: 14px;
  color: #666;
}

.checkbox-label input[type="checkbox"] {
  margin-right: 8px;
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.checkmark {
  margin-left: 4px;
}

.help-text {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.demo-accounts {
  margin-top: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.demo-accounts h4 {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #333;
}

.demo-item {
  padding: 8px 12px;
  margin: 5px 0;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.demo-item:hover {
  background: #e3f2fd;
}

.demo-item span {
  font-size: 13px;
  color: #666;
}

.demo-accounts small {
  display: block;
  margin-top: 10px;
  font-size: 12px;
  color: #999;
}
</style>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.login-card {
  width: 90%;
  max-width: 400px;
  padding: 30px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.app-title {
  text-align: center;
  margin-bottom: 30px;
  font-size: 24px;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-size: 14px;
}

input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  outline: none;
  transition: border 0.3s;
}

input:focus {
  border-color: #333;
}

.login-button {
  width: 100%;
  padding: 12px;
  background-color: #333;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.login-button:hover {
  background-color: #555;
}

.login-button:disabled {
  background-color: #999;
  cursor: not-allowed;
}

.loading {
  opacity: 0.8;
}
</style> 