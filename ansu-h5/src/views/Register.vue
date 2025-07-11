<template>
  <div class="register-container">
    <div class="register-card">
      <h1 class="app-title">用户注册</h1>
      
      <div class="form-group">
        <label for="tenantCode">租户编码</label>
        <input
          type="text"
          id="tenantCode"
          v-model="formData.tenantCode"
          placeholder="留空则使用默认租户"
        />
        <small class="help-text">可选，留空将注册到默认租户</small>
      </div>

      <div class="form-group">
        <label for="username">用户名 *</label>
        <input
          type="text"
          id="username"
          v-model="formData.username"
          placeholder="请输入用户名（3-20个字符）"
        />
        <div v-if="usernameError" class="error-message">{{ usernameError }}</div>
      </div>
      
      <div class="form-group">
        <label for="password">密码 *</label>
        <input 
          type="password" 
          id="password" 
          v-model="formData.password" 
          placeholder="请输入密码（6-20个字符）"
        />
      </div>
      
      <div class="form-group">
        <label for="confirmPassword">确认密码 *</label>
        <input 
          type="password" 
          id="confirmPassword" 
          v-model="formData.confirmPassword" 
          placeholder="请再次输入密码"
        />
      </div>
      
      <div class="form-group">
        <label for="email">邮箱</label>
        <input 
          type="email" 
          id="email" 
          v-model="formData.email" 
          placeholder="请输入邮箱地址"
          @blur="checkEmailAvailable"
        />
        <div v-if="emailError" class="error-message">{{ emailError }}</div>
      </div>
      
      <div class="form-group">
        <label for="phone">手机号</label>
        <input 
          type="tel" 
          id="phone" 
          v-model="formData.phone" 
          placeholder="请输入手机号"
        />
      </div>
      
      <div class="form-group">
        <label for="realName">真实姓名</label>
        <input 
          type="text" 
          id="realName" 
          v-model="formData.realName" 
          placeholder="请输入真实姓名"
        />
      </div>
      
      <button 
        class="register-button" 
        :class="{ loading: isLoading }" 
        @click="handleRegister"
        :disabled="isLoading || !isFormValid"
      >
        {{ isLoading ? '注册中...' : '注册' }}
      </button>
      
      <div class="login-link">
        <span>已有账号？</span>
        <a href="#" @click.prevent="goToLogin">立即登录</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/userStore'
import { checkUsername, checkEmail } from '../api/auth'
import { Toast } from 'tdesign-mobile-vue'

const router = useRouter()
const userStore = useUserStore()

// 表单数据
const formData = ref({
  tenantCode: '',
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: '',
  realName: ''
})

const isLoading = ref(false)
const usernameError = ref('')
const emailError = ref('')

// 表单验证
const isFormValid = computed(() => {
  return formData.value.username && 
         formData.value.password && 
         formData.value.confirmPassword &&
         formData.value.password === formData.value.confirmPassword &&
         !usernameError.value &&
         !emailError.value
})

// 检查用户名是否可用
const checkUsernameAvailable = async () => {
  if (!formData.value.username) {
    usernameError.value = ''
    return
  }
  
  if (formData.value.username.length < 3 || formData.value.username.length > 20) {
    usernameError.value = '用户名长度必须在3-20个字符之间'
    return
  }
  
  if (!/^[a-zA-Z0-9_]+$/.test(formData.value.username)) {
    usernameError.value = '用户名只能包含字母、数字和下划线'
    return
  }
  
  try {
    const response = await checkUsername(formData.value.username)
    if (response.code === 200) {
      if (!response.data) {
        usernameError.value = '用户名已存在'
      } else {
        usernameError.value = ''
      }
    }
  } catch (error) {
    console.error('检查用户名失败:', error)
  }
}

// 检查邮箱是否可用
const checkEmailAvailable = async () => {
  if (!formData.value.email) {
    emailError.value = ''
    return
  }
  
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(formData.value.email)) {
    emailError.value = '邮箱格式不正确'
    return
  }
  
  try {
    const response = await checkEmail(formData.value.email)
    if (response.code === 200) {
      if (!response.data) {
        emailError.value = '邮箱已被注册'
      } else {
        emailError.value = ''
      }
    }
  } catch (error) {
    console.error('检查邮箱失败:', error)
  }
}

// 处理注册
const handleRegister = async () => {
  // 基础验证
  if (!formData.value.username || !formData.value.password || !formData.value.confirmPassword) {
    Toast({
      message: '请填写必填项',
      theme: 'warning'
    })
    return
  }
  
  if (formData.value.password !== formData.value.confirmPassword) {
    Toast({
      message: '两次输入的密码不一致',
      theme: 'warning'
    })
    return
  }
  
  if (formData.value.password.length < 6 || formData.value.password.length > 20) {
    Toast({
      message: '密码长度必须在6-20个字符之间',
      theme: 'warning'
    })
    return
  }
  
  isLoading.value = true
  
  try {
    const result = await userStore.register(formData.value)
    
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
  background: white;
  border-radius: 12px;
  padding: 40px 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 400px;
}

.app-title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 24px;
  font-weight: 600;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-weight: 500;
  font-size: 14px;
}

.form-group input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s ease;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
}

.error-message {
  color: #e34850;
  font-size: 12px;
  margin-top: 5px;
}

.register-button {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 20px;
}

.register-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.register-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.register-button.loading {
  opacity: 0.8;
}

.login-link {
  text-align: center;
  font-size: 14px;
  color: #666;
}

.login-link a {
  color: #0052d9;
  text-decoration: none;
  margin-left: 5px;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>
