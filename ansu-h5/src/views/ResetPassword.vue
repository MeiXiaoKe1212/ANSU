<template>
  <div class="reset-password-container">
    <div class="reset-password-card">
      <h1 class="app-title">重置密码</h1>
      <p class="description">请输入您的新密码</p>
      
      <div class="form-group">
        <label for="newPassword">新密码 *</label>
        <input 
          type="password" 
          id="newPassword" 
          v-model="formData.newPassword" 
          placeholder="请输入新密码（6-20个字符）"
          :disabled="isLoading"
        />
      </div>
      
      <div class="form-group">
        <label for="confirmPassword">确认密码 *</label>
        <input 
          type="password" 
          id="confirmPassword" 
          v-model="formData.confirmPassword" 
          placeholder="请再次输入新密码"
          :disabled="isLoading"
        />
      </div>
      
      <button 
        class="submit-button" 
        :class="{ loading: isLoading }" 
        @click="handleSubmit"
        :disabled="isLoading || !isFormValid"
      >
        {{ isLoading ? '重置中...' : '重置密码' }}
      </button>
      
      <div class="back-link">
        <a href="#" @click.prevent="goToLogin">返回登录</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { resetPassword } from '../api/auth'
import { Toast } from 'tdesign-mobile-vue'

const router = useRouter()
const route = useRoute()

const formData = ref({
  token: '',
  newPassword: '',
  confirmPassword: ''
})
const isLoading = ref(false)

// 表单验证
const isFormValid = computed(() => {
  return formData.value.newPassword && 
         formData.value.confirmPassword &&
         formData.value.newPassword === formData.value.confirmPassword &&
         formData.value.newPassword.length >= 6 &&
         formData.value.newPassword.length <= 20
})

// 页面加载时获取token
onMounted(() => {
  const token = route.query.token
  if (!token) {
    Toast({
      message: '无效的重置链接',
      theme: 'error'
    })
    router.push('/login')
    return
  }
  formData.value.token = token
})

// 处理提交
const handleSubmit = async () => {
  if (!isFormValid.value) {
    Toast({
      message: '请检查输入信息',
      theme: 'warning'
    })
    return
  }
  
  if (formData.value.newPassword !== formData.value.confirmPassword) {
    Toast({
      message: '两次输入的密码不一致',
      theme: 'warning'
    })
    return
  }
  
  isLoading.value = true
  
  try {
    const response = await resetPassword(formData.value)
    
    if (response.code === 200) {
      Toast({
        message: '密码重置成功，请使用新密码登录',
        theme: 'success'
      })
      
      // 2秒后跳转到登录页
      setTimeout(() => {
        router.push('/login')
      }, 2000)
    } else {
      Toast({
        message: response.message || '重置失败，请稍后重试',
        theme: 'error'
      })
    }
  } catch (error) {
    console.error('重置密码失败:', error)
    Toast({
      message: '重置失败，请稍后重试',
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
.reset-password-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.reset-password-card {
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
