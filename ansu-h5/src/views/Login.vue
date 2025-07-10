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
          placeholder="请输入用户名"
          @keyup.enter="focusPassword"
        />
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
      
      <button 
        class="login-button" 
        :class="{ loading: isLoading }" 
        @click="handleLogin"
        :disabled="isLoading"
      >
        {{ isLoading ? '登录中...' : '登录' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref('')
const password = ref('')
const isLoading = ref(false)
const passwordInput = ref(null)

const focusPassword = () => {
  passwordInput.value.focus()
}

const handleLogin = () => {
  if (!username.value || !password.value) {
    alert('请输入用户名和密码')
    return
  }
  
  isLoading.value = true
  
  // 模拟登录请求
  setTimeout(() => {
    // 假设登录成功
    const token = 'dummy_token_' + Date.now()
    localStorage.setItem('token', token)
    localStorage.setItem('username', username.value)
    
    isLoading.value = false
    router.push('/home')
  }, 1000)
}
</script>

<style scoped>
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