<template>
  <div class="forgot-password-container">
    <div class="forgot-password-card">
      <h1 class="app-title">找回密码</h1>
      <p class="description">请输入您的邮箱地址，我们将发送密码重置链接到您的邮箱</p>
      
      <div class="form-group">
        <label for="email">邮箱地址 *</label>
        <input 
          type="email" 
          id="email" 
          v-model="email" 
          placeholder="请输入注册时使用的邮箱地址"
          :disabled="isLoading"
        />
      </div>
      
      <button 
        class="submit-button" 
        :class="{ loading: isLoading }" 
        @click="handleSubmit"
        :disabled="isLoading || !isEmailValid"
      >
        {{ isLoading ? '发送中...' : '发送重置链接' }}
      </button>
      
      <div class="back-link">
        <a href="#" @click.prevent="goToLogin">返回登录</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { forgotPassword } from '../api/auth'
import { Toast } from 'tdesign-mobile-vue'

const router = useRouter()

const email = ref('')
const isLoading = ref(false)

// 邮箱格式验证
const isEmailValid = computed(() => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return email.value && emailRegex.test(email.value)
})

// 处理提交
const handleSubmit = async () => {
  if (!isEmailValid.value) {
    Toast({
      message: '请输入有效的邮箱地址',
      theme: 'warning'
    })
    return
  }
  
  isLoading.value = true
  
  try {
    const response = await forgotPassword({ email: email.value })
    
    if (response.code === 200) {
      Toast({
        message: response.message || '重置链接已发送到您的邮箱',
        theme: 'success'
      })
      
      // 3秒后跳转到登录页
      setTimeout(() => {
        router.push('/login')
      }, 3000)
    } else {
      Toast({
        message: response.message || '发送失败，请稍后重试',
        theme: 'error'
      })
    }
  } catch (error) {
    console.error('发送重置邮件失败:', error)
    Toast({
      message: '发送失败，请稍后重试',
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
.forgot-password-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.forgot-password-card {
  background: white;
  border-radius: 12px;
  padding: 40px 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 400px;
}

.app-title {
  text-align: center;
  margin-bottom: 15px;
  color: #333;
  font-size: 24px;
  font-weight: 600;
}

.description {
  text-align: center;
  margin-bottom: 30px;
  color: #666;
  font-size: 14px;
  line-height: 1.5;
}

.form-group {
  margin-bottom: 25px;
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

.form-group input:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.submit-button {
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

.submit-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.submit-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.submit-button.loading {
  opacity: 0.8;
}

.back-link {
  text-align: center;
  font-size: 14px;
}

.back-link a {
  color: #0052d9;
  text-decoration: none;
}

.back-link a:hover {
  text-decoration: underline;
}
</style>
