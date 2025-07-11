<template>
  <div>
    <t-navbar title="主页" fixed />

    <div class="home-container">
      <!-- 用户欢迎区域 -->
      <div class="welcome-section">
        <div class="user-info">
          <h2>你好，{{ username }}！</h2>
          <p>{{ tenantInfo }}</p>
        </div>
        <div class="date-info">
          <t-icon name="time" size="20px" />
          <span>{{ currentDate }}</span>
        </div>
      </div>

      <!-- 订单统计 -->
      <div class="stats-section">
        <div class="stat-item" @click="goToOrders(0)">
          <div class="stat-number">{{ orderStats.total }}</div>
          <div class="stat-label">总订单</div>
        </div>
        <div class="stat-item" @click="goToOrders(1)">
          <div class="stat-number">{{ orderStats.pending }}</div>
          <div class="stat-label">待接单</div>
        </div>
        <div class="stat-item" @click="goToOrders(3)">
          <div class="stat-number">{{ orderStats.shipping }}</div>
          <div class="stat-label">运输中</div>
        </div>
        <div class="stat-item" @click="goToOrders(4)">
          <div class="stat-number">{{ orderStats.completed }}</div>
          <div class="stat-label">已完成</div>
        </div>
      </div>

      <!-- 快捷操作 -->
      <div class="quick-actions">
        <div class="action-item" @click="createOrder">
          <div class="action-icon">
            <t-icon name="add" size="24px" />
          </div>
          <span>创建订单</span>
        </div>
        <div class="action-item" @click="goToOrders(0)">
          <div class="action-icon">
            <t-icon name="view-list" size="24px" />
          </div>
          <span>订单列表</span>
        </div>
        <div class="action-item" @click="goToOrders(1)">
          <div class="action-icon">
            <t-icon name="time" size="24px" />
          </div>
          <span>待处理</span>
        </div>
        <div class="action-item" @click="goToProfile">
          <div class="action-icon">
            <t-icon name="user" size="24px" />
          </div>
          <span>个人中心</span>
        </div>
      </div>

      <!-- 最近订单 -->
      <div class="recent-orders" v-if="recentOrders.length > 0">
        <div class="section-header">
          <h3>最近订单</h3>
          <span @click="goToOrders(0)" class="view-all">查看全部</span>
        </div>
        <div class="order-list">
          <div
            v-for="order in recentOrders"
            :key="order.id"
            class="order-item"
            @click="viewOrderDetail(order)"
          >
            <div class="order-info">
              <div class="order-no">{{ order.orderNo }}</div>
              <div class="order-customer">{{ order.customerName }}</div>
            </div>
            <div class="order-status" :class="getStatusClass(order.status)">
              {{ getStatusText(order.status) }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/userStore'
import { getAllOrders } from '../api/order'

const router = useRouter()
const userStore = useUserStore()

const orders = ref([])
const orderStats = ref({
  total: 0,
  pending: 0,
  shipping: 0,
  completed: 0
})

const username = computed(() => {
  return userStore.user?.realName || userStore.user?.username || localStorage.getItem('username') || '用户'
})

const tenantInfo = computed(() => {
  const tenantId = userStore.tenantId
  if (tenantId === 1) return '默认租户'
  if (tenantId === 2) return '演示公司A'
  if (tenantId === 3) return '演示公司B'
  return `租户ID: ${tenantId}`
})

const currentDate = computed(() => {
  const now = new Date()
  return `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日`
})

const recentOrders = computed(() => {
  return orders.value.slice(0, 5) // 显示最近5个订单
})

// 获取订单数据
const fetchOrders = async () => {
  try {
    const response = await getAllOrders()
    if (response.code === 200) {
      orders.value = response.data || []
      calculateOrderStats()
    }
  } catch (error) {
    console.error('获取订单失败:', error)
  }
}

// 计算订单统计
const calculateOrderStats = () => {
  const stats = {
    total: orders.value.length,
    pending: 0,
    shipping: 0,
    completed: 0
  }

  orders.value.forEach(order => {
    switch (order.status) {
      case 1:
        stats.pending++
        break
      case 3:
        stats.shipping++
        break
      case 4:
        stats.completed++
        break
    }
  })

  orderStats.value = stats
}

// 导航方法
const goToOrders = (status) => {
  router.push({ path: '/orders', query: { status } })
}

const createOrder = () => {
  router.push('/create-order')
}

const goToProfile = () => {
  router.push('/profile')
}

const viewOrderDetail = (order) => {
  router.push(`/order-detail/${order.id}`)
}

// 获取状态样式类
const getStatusClass = (status) => {
  const statusClasses = {
    1: 'status-pending',
    2: 'status-accepted',
    3: 'status-shipping',
    4: 'status-completed',
    5: 'status-cancelled'
  }
  return statusClasses[status] || ''
}

// 获取状态文本
const getStatusText = (status) => {
  const statusTexts = {
    1: '待接单',
    2: '已接单',
    3: '运输中',
    4: '已完成',
    5: '已取消'
  }
  return statusTexts[status] || '未知状态'
}

onMounted(() => {
  fetchOrders()
})

</script>

<style scoped>
.home-container {
  padding: 15px;
  padding-top: 56px; /* 为固定导航栏留出空间 */
  padding-bottom: 80px; /* 为底部导航留出空间 */
  background: #f5f5f5;
  min-height: 100vh;
}

.welcome-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info h2 {
  margin: 0 0 5px 0;
  font-size: 20px;
  font-weight: 600;
}

.user-info p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.date-info {
  display: flex;
  align-items: center;
  font-size: 14px;
  opacity: 0.9;
}

.date-info span {
  margin-left: 5px;
}

.stats-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
  margin-bottom: 20px;
}

.stat-item {
  background: white;
  border-radius: 12px;
  padding: 15px 10px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.2s;
}

.stat-item:hover {
  transform: translateY(-2px);
}

.stat-number {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 12px;
  color: #666;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
  margin-bottom: 20px;
}

.action-item {
  background: white;
  border-radius: 12px;
  padding: 20px 10px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.2s;
}

.action-item:hover {
  transform: translateY(-2px);
}

.action-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 10px;
}

.action-item span {
  font-size: 12px;
  color: #333;
}

.recent-orders {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.section-header h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
  font-weight: 600;
}

.view-all {
  font-size: 14px;
  color: #0052d9;
  cursor: pointer;
}

.order-list {
  space-y: 10px;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
}

.order-item:last-child {
  border-bottom: none;
}

.order-info {
  flex: 1;
}

.order-no {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.order-customer {
  font-size: 12px;
  color: #666;
}

.order-status {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-pending { background: #fff3cd; color: #856404; }
.status-accepted { background: #d4edda; color: #155724; }
.status-shipping { background: #cce5ff; color: #004085; }
.status-completed { background: #d1ecf1; color: #0c5460; }
.status-cancelled { background: #f8d7da; color: #721c24; }

@media (max-width: 480px) {
  .stats-section {
    grid-template-columns: repeat(2, 1fr);
  }

  .quick-actions {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>