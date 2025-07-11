<template>
  <div>
    <t-navbar title="个人中心" fixed />
    
    <div class="profile-container">
      <!-- 用户信息卡片 -->
      <div class="user-card">
        <div class="avatar">
          <t-icon name="user" size="48px" />
        </div>
        <div class="user-info">
          <h3>{{ userInfo.realName || userInfo.username || '用户' }}</h3>
          <p>{{ userInfo.email || '未设置邮箱' }}</p>
        </div>
      </div>
      
      <!-- 功能列表 -->
      <div class="menu-list">
        <div class="menu-item" @click="showUserInfo">
          <t-icon name="user-circle" />
          <span>个人信息</span>
          <t-icon name="chevron-right" />
        </div>
        
        <div class="menu-item" @click="handleLogout">
          <t-icon name="logout" />
          <span>退出登录</span>
          <t-icon name="chevron-right" />
        </div>
      </div>
    </div>
    
    <!-- 用户信息弹窗 -->
    <t-dialog
      v-model="userInfoVisible"
      title="个人信息"
      :confirm-btn="null"
      :cancel-btn="{ content: '关闭' }"
    >
      <div class="user-detail">
        <div class="detail-item">
          <label>用户名：</label>
          <span>{{ userInfo.username }}</span>
        </div>
        <div class="detail-item">
          <label>真实姓名：</label>
          <span>{{ userInfo.realName || '未设置' }}</span>
        </div>
        <div class="detail-item">
          <label>邮箱：</label>
          <span>{{ userInfo.email || '未设置' }}</span>
        </div>
        <div class="detail-item">
          <label>手机号：</label>
          <span>{{ userInfo.phone || '未设置' }}</span>
        </div>
        <div class="detail-item">
          <label>注册时间：</label>
          <span>{{ formatDate(userInfo.createTime) }}</span>
        </div>
      </div>
    </t-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/userStore'
import { Toast, Dialog } from 'tdesign-mobile-vue'

const router = useRouter()
const userStore = useUserStore()

const userInfoVisible = ref(false)

// 用户信息
const userInfo = computed(() => userStore.user || {})

// 显示用户信息
const showUserInfo = () => {
  userInfoVisible.value = true
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '未知'
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 退出登录
const handleLogout = () => {
  Dialog({
    title: '确认退出',
    content: '确定要退出登录吗？',
    confirmBtn: { content: '确定', theme: 'danger' },
    cancelBtn: { content: '取消' },
    onConfirm: () => {
      userStore.logout()
      Toast({
        message: '已退出登录',
        theme: 'success'
      })
      router.push('/login')
    }
  })
}

// 页面加载时获取用户信息
onMounted(() => {
  if (userStore.isLoggedIn && !userStore.user) {
    userStore.fetchUserInfo()
  }
})
</script>

<style scoped>
.profile-container {
  padding: 15px;
  padding-top: 56px; /* 为固定导航栏留出空间 */
  padding-bottom: 80px; /* 为底部导航留出空间 */
}

.user-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.user-info h3 {
  margin: 0 0 5px 0;
  font-size: 18px;
  color: #333;
}

.user-info p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

.menu-list {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.2s;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-item:hover {
  background-color: #f8f8f8;
}

.menu-item span {
  flex: 1;
  margin-left: 12px;
  font-size: 16px;
  color: #333;
}

.user-detail {
  padding: 10px 0;
}

.detail-item {
  display: flex;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.detail-item:last-child {
  border-bottom: none;
}

.detail-item label {
  width: 80px;
  font-weight: 500;
  color: #666;
}

.detail-item span {
  flex: 1;
  color: #333;
}
</style>
