<template>
  <div>
    <t-navbar title="编辑资料" fixed>
      <template #left>
        <t-icon name="chevron-left" @click="handleBack" />
      </template>
      <template #right>
        <span @click="handleSave" class="save-btn">保存</span>
      </template>
    </t-navbar>
    
    <div class="edit-profile-container">
      <div class="form-section">
        <div class="form-group">
          <label>用户名</label>
          <input 
            type="text" 
            :value="userInfo.username" 
            disabled
            class="disabled-input"
          />
          <small class="help-text">用户名不可修改</small>
        </div>
        
        <div class="form-group">
          <label>真实姓名</label>
          <input 
            type="text" 
            v-model="formData.realName" 
            placeholder="请输入真实姓名"
            :disabled="isLoading"
          />
        </div>
        
        <div class="form-group">
          <label>邮箱地址</label>
          <input 
            type="email" 
            v-model="formData.email" 
            placeholder="请输入邮箱地址"
            :disabled="isLoading"
            @blur="validateEmail"
          />
          <div v-if="emailError" class="error-message">{{ emailError }}</div>
        </div>
        
        <div class="form-group">
          <label>手机号码</label>
          <input 
            type="tel" 
            v-model="formData.phone" 
            placeholder="请输入手机号码"
            :disabled="isLoading"
            @blur="validatePhone"
          />
          <div v-if="phoneError" class="error-message">{{ phoneError }}</div>
        </div>
        
        <div class="form-group">
          <label>头像链接</label>
          <input 
            type="url" 
            v-model="formData.avatar" 
            placeholder="请输入头像图片链接"
            :disabled="isLoading"
          />
          <small class="help-text">请输入有效的图片链接地址</small>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/userStore'
import { updateProfile, checkEmail } from '../api/auth'
import { Toast } from 'tdesign-mobile-vue'

const router = useRouter()
const userStore = useUserStore()

const formData = ref({
  realName: '',
  email: '',
  phone: '',
  avatar: ''
})

const isLoading = ref(false)
const emailError = ref('')
const phoneError = ref('')

// 用户信息
const userInfo = computed(() => userStore.user || {})

// 页面加载时初始化表单数据
onMounted(() => {
  if (userStore.user) {
    formData.value = {
      realName: userStore.user.realName || '',
      email: userStore.user.email || '',
      phone: userStore.user.phone || '',
      avatar: userStore.user.avatar || ''
    }
  }
})

// 验证邮箱
const validateEmail = async () => {
  emailError.value = ''
  
  if (!formData.value.email) {
    return
  }
  
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(formData.value.email)) {
    emailError.value = '邮箱格式不正确'
    return
  }
  
  // 如果邮箱没有变化，不需要检查
  if (formData.value.email === userInfo.value.email) {
    return
  }
  
  try {
    const response = await checkEmail(formData.value.email)
    if (response.code === 200 && !response.data) {
      emailError.value = '邮箱已被其他用户使用'
    }
  } catch (error) {
    console.error('检查邮箱失败:', error)
  }
}

// 验证手机号
const validatePhone = () => {
  phoneError.value = ''
  
  if (!formData.value.phone) {
    return
  }
  
  const phoneRegex = /^1[3-9]\d{9}$/
  if (!phoneRegex.test(formData.value.phone)) {
    phoneError.value = '手机号格式不正确'
  }
}

// 返回上一页
const handleBack = () => {
  router.back()
}

// 保存修改
const handleSave = async () => {
  // 验证表单
  if (emailError.value || phoneError.value) {
    Toast({
      message: '请修正表单错误后再保存',
      theme: 'warning'
    })
    return
  }
  
  isLoading.value = true
  
  try {
    const response = await updateProfile(formData.value)
    
    if (response.code === 200) {
      // 更新本地用户信息
      userStore.user = { ...userStore.user, ...response.data }
      
      Toast({
        message: '资料更新成功',
        theme: 'success'
      })
      
      // 延迟返回上一页
      setTimeout(() => {
        router.back()
      }, 1500)
    } else {
      Toast({
        message: response.message || '更新失败',
        theme: 'error'
      })
    }
  } catch (error) {
    console.error('更新资料失败:', error)
    Toast({
      message: '更新失败，请稍后重试',
      theme: 'error'
    })
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.edit-profile-container {
  padding: 15px;
  padding-top: 56px; /* 为固定导航栏留出空间 */
  padding-bottom: 20px;
}

.save-btn {
  color: #0052d9;
  font-weight: 500;
  cursor: pointer;
}

.form-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 20px;
}

.form-group:last-child {
  margin-bottom: 0;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
  font-size: 14px;
}

.form-group input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s ease;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #0052d9;
}

.form-group input:disabled {
  background-color: #f5f5f5;
  color: #999;
  cursor: not-allowed;
}

.disabled-input {
  background-color: #f5f5f5 !important;
  color: #999 !important;
}

.help-text {
  display: block;
  margin-top: 5px;
  color: #666;
  font-size: 12px;
}

.error-message {
  color: #e34850;
  font-size: 12px;
  margin-top: 5px;
}
</style>
