<template>
  <div class="register-container">
    <div class="register-card">
      <h1 class="app-title">用户注册</h1>
      
      <div class="form-group">
        <label for="username">用户名</label>
        <input 
          type="text" 
          id="username" 
          v-model="formData.username" 
          placeholder="请输入用户名（3-20个字符）"
          maxlength="20"
        />
      </div>
      
      <div class="form-group">
        <label for="realName">真实姓名</label>
        <input 
          type="text" 
          id="realName" 
          v-model="formData.realName" 
          placeholder="请输入真实姓名"
          maxlength="50"
        />
      </div>
      
      <div class="form-group">
        <label for="phone">手机号</label>
        <input 
          type="tel" 
          id="phone" 
          v-model="formData.phone" 
          placeholder="请输入手机号"
          maxlength="11"
        />
      </div>
      
      <div class="form-group">
        <label for="email">邮箱</label>
        <input 
          type="email" 
          id="email" 
          v-model="formData.email" 
          placeholder="请输入邮箱地址"
        />
      </div>
      
      <div class="form-group">
        <label for="password">密码</label>
        <input 
          type="password" 
          id="password" 
          v-model="formData.password" 
          placeholder="请输入密码（6-20个字符）"
          maxlength="20"
        />
      </div>
      
      <div class="form-group">
        <label for="confirmPassword">确认密码</label>
        <input 
          type="password" 
          id="confirmPassword" 
          v-model="formData.confirmPassword" 
          placeholder="请再次输入密码"
          maxlength="20"
        />
      </div>
      
      <button 
        class="register-button" 
        :class="{ loading: isLoading }" 
        @click="handleRegister"
        :disabled="isLoading"
      >
        {{ isLoading ? '注册中...' : '注册' }}
      </button>
      
      <div class="login-link">
        已有账号？<a href="#" @click="goToLogin">立即登录</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/userStore'
import { Toast } from 'tdesign-mobile-vue'

const router = useRouter()
const userStore = useUserStore()

const isLoading = ref(false)
const formData = reactive({
  username: '',
  realName: '',
  phone: '',
  email: '',
  password: '',
  confirmPassword: ''
})

// 表单验证
const validateForm = () => {
  if (!formData.username) {
    Toast({ message: '请输入用户名', theme: 'error' })
    return false
  }
  
  if (formData.username.length < 3 || formData.username.length > 20) {
    Toast({ message: '用户名长度必须在3-20个字符之间', theme: 'error' })
    return false
  }
  
  if (!formData.realName) {
    Toast({ message: '请输入真实姓名', theme: 'error' })
    return false
  }
  
  if (!formData.password) {
    Toast({ message: '请输入密码', theme: 'error' })
    return false
  }
  
  if (formData.password.length < 6 || formData.password.length > 20) {
    Toast({ message: '密码长度必须在6-20个字符之间', theme: 'error' })
    return false
  }
  
  if (formData.password !== formData.confirmPassword) {
    Toast({ message: '两次输入的密码不一致', theme: 'error' })
    return false
  }
  
  if (formData.phone && !/^1[3-9]\d{9}$/.test(formData.phone)) {
    Toast({ message: '手机号格式不正确', theme: 'error' })
    return false
  }
  
  if (formData.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
    Toast({ message: '邮箱格式不正确', theme: 'error' })
    return false
  }
  
  return true
}

const handleRegister = async () => {
  if (!validateForm()) {
    return
  }
  
  isLoading.value = true
  
  try {
    const result = await userStore.register(formData)
    
    if (result.success) {
      Toast({
        message: result.message,
        theme: 'success'
      })
      // 注册成功后跳转到登录页
      setTimeout(() => {
        router.push('/login')
      }, 1500)
    } else {
      Toast({
        message: result.message,
        theme: 'error'
      })
    }
  } catch (error) {
    console.error('注册失败:', error)
    Toast({
      message: '注册失败，请稍后重试',
      theme: 'error'
    })
  } finally {
    isLoading.value = false
  }
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-card {
  width: 100%;
  max-width: 400px;
  padding: 30px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.app-title {
  text-align: center;
  margin-bottom: 30px;
  font-size: 24px;
  color: #333;
  font-weight: 600;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-size: 14px;
  font-weight: 500;
}

input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
  outline: none;
  transition: all 0.3s;
  box-sizing: border-box;
}

input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.register-button {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 20px;
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.register-button:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.loading {
  opacity: 0.8;
}

.login-link {
  text-align: center;
  color: #666;
  font-size: 14px;
}

.login-link a {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>
