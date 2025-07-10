<template>
  <div>
    <t-navbar title="运输管理系统" fixed />

    <div class="home-container">
      <div class="welcome-card">
        <h2>欢迎, {{ username }}</h2>
        <p>运输中介管理系统 - 订单统计信息</p>
      </div>

      <!-- 订单统计卡片 -->
      <div class="stats-card">
        <div class="stats-summary">
          <div class="stats-item">
            <div class="stats-value">{{ orderStore.orders.length }}</div>
            <div class="stats-label">订单总数</div>
          </div>
          <div class="stats-item">
            <div class="stats-value">¥{{ totalRevenue }}</div>
            <div class="stats-label">总收入</div>
          </div>
          <div class="stats-item">
            <div class="stats-value">¥{{ totalProfit }}</div>
            <div class="stats-label">总利润</div>
          </div>
          <div class="stats-item">
            <div class="stats-value">{{ outsourcedCount }}</div>
            <div class="stats-label">外包订单</div>
          </div>
        </div>
      </div>

      <!-- 状态统计 -->
      <div class="status-stats">
        <h3>订单状态统计</h3>
        <div class="status-grid">
          <div class="status-item" v-for="(count, status) in statusStats" :key="status">
            <div class="status-count">{{ count }}</div>
            <div class="status-name">{{ getStatusName(status) }}</div>
          </div>
        </div>
      </div>

      <!-- 饼图：外派/自有比例 -->
      <div class="chart-card">
        <div class="card-header">
          <h3>订单类型分布</h3>
        </div>
        <div class="card-body">
          <div id="pieChart" class="chart-container"></div>
        </div>
      </div>

      <!-- 图表选项卡 -->
      <div class="chart-card">
        <div class="card-header">
          <h3>账单金额趋势</h3>
        </div>
        <div class="card-tabs">
          <t-tabs v-model="activeTimeRange">
            <t-tab-panel value="week" label="周"></t-tab-panel>
            <t-tab-panel value="month" label="月"></t-tab-panel>
            <t-tab-panel value="halfYear" label="半年"></t-tab-panel>
          </t-tabs>
        </div>
        <div class="card-body">
          <div id="trendChart" class="chart-container"></div>
        </div>
      </div>

      <!-- 图表类型切换 -->
      <div class="chart-type-switch">
        <t-radio-group v-model="chartType">
          <t-radio value="line">折线图</t-radio>
          <t-radio value="bar">柱状图</t-radio>
        </t-radio-group>
      </div>
    </div>

    <!-- 悬浮按钮 -->
    <t-fab class="custom-fab" :icon="addIconFunc" @click="showAddOrderForm" />

    <!-- 运输订单表单 -->
    <transport-order-form
      v-model:visible="orderFormVisible"
      :order-data="currentOrderData"
      @submit="handleOrderSubmit"
      @close="handleFormClose"
    />
  </div>
</template>

<script setup>
import { ref, computed, h, onMounted, watch, onUnmounted, nextTick } from 'vue'
import { AddIcon } from 'tdesign-icons-vue-next'
import { Toast, Tabs, TabPanel, RadioGroup, Radio } from 'tdesign-mobile-vue'
import { useTransportOrderStore } from '../stores/transportOrderStore'
import TransportOrderForm from '../components/TransportOrderForm.vue'
import * as echarts from 'echarts/core'
import { 
  TitleComponent, 
  TooltipComponent, 
  LegendComponent,
  GridComponent
} from 'echarts/components'
import { PieChart, LineChart, BarChart } from 'echarts/charts'
import { CanvasRenderer } from 'echarts/renderers'

// 注册必需的组件
echarts.use([
  TitleComponent, 
  TooltipComponent, 
  LegendComponent,
  GridComponent,
  PieChart,
  LineChart,
  BarChart,
  CanvasRenderer
])

const orderStore = useTransportOrderStore()
const username = computed(() => localStorage.getItem('username') || '用户')

// 图表相关
const activeTimeRange = ref('week')
const chartType = ref('line')
let pieChartInstance = null
let trendChartInstance = null

// 订单表单控制
const orderFormVisible = ref(false)
const currentOrderData = ref({})

// 订单统计数据
const totalRevenue = computed(() => {
  return orderStore.orders
    .reduce((sum, order) => sum + (order.actualPrice || 0), 0)
    .toFixed(2)
})

const totalProfit = computed(() => {
  return orderStore.orders
    .reduce((sum, order) => sum + (order.profit || 0), 0)
    .toFixed(2)
})

const outsourcedCount = computed(() => {
  return orderStore.orders.filter(order => order.isOutsourced === 1).length
})

const selfOwnedCount = computed(() => {
  return orderStore.orders.filter(order => order.isOutsourced === 0).length
})

// 状态统计
const statusStats = computed(() => {
  const stats = {}
  orderStore.orders.forEach(order => {
    const status = order.transportStatus
    stats[status] = (stats[status] || 0) + 1
  })
  return stats
})

// 获取状态中文名称
const getStatusName = (status) => {
  const statusMap = {
    'CREATED': '已创建',
    'DEPARTED': '已出发',
    'TRANSPORTING': '运输中',
    'EXCEPTION': '异常',
    'DELIVERED': '已送达'
  }
  return statusMap[status] || status
}

// 初始化饼图
const initPieChart = () => {
  if (!document.getElementById('pieChart')) return
  
  pieChartInstance = echarts.init(document.getElementById('pieChart'))
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'horizontal',
      bottom: 0
    },
    series: [
      {
        name: '账单类型',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '18',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { value: outsourcedCount.value, name: '外包订单' },
          { value: selfOwnedCount.value, name: '自有订单' }
        ]
      }
    ]
  }
  
  pieChartInstance.setOption(option)
}

// 获取趋势图数据
const getTrendData = () => {
  // 根据选择的时间范围生成日期
  let dates = []
  let now = new Date()
  
  if (activeTimeRange.value === 'week') {
    // 生成过去7天的日期
    for (let i = 6; i >= 0; i--) {
      const date = new Date(now)
      date.setDate(now.getDate() - i)
      dates.push(formatDate(date))
    }
  } else if (activeTimeRange.value === 'month') {
    // 生成过去30天的日期
    for (let i = 29; i >= 0; i--) {
      const date = new Date(now)
      date.setDate(now.getDate() - i)
      dates.push(formatDate(date))
    }
  } else if (activeTimeRange.value === 'halfYear') {
    // 生成过去6个月的月份
    for (let i = 5; i >= 0; i--) {
      const date = new Date(now)
      date.setMonth(now.getMonth() - i)
      dates.push(formatMonth(date))
    }
  }
  
  // 按日期分组账单并计算总金额
  const amountByDate = {}
  dates.forEach(date => {
    amountByDate[date] = 0
  })
  
  orderStore.orders.forEach(order => {
    if (!order.createTime) return

    const orderDate = new Date(order.createTime)
    let dateKey

    if (activeTimeRange.value === 'halfYear') {
      dateKey = formatMonth(orderDate)
    } else {
      dateKey = formatDate(orderDate)
    }

    if (amountByDate[dateKey] !== undefined) {
      amountByDate[dateKey] += (order.actualPrice || order.quotedPrice || 0)
    }
  })
  
  return {
    dates,
    amounts: dates.map(date => amountByDate[date])
  }
}

// 初始化趋势图
const initTrendChart = () => {
  if (!document.getElementById('trendChart')) return
  
  trendChartInstance = echarts.init(document.getElementById('trendChart'))
  updateTrendChart()
}

// 更新趋势图
const updateTrendChart = () => {
  if (!trendChartInstance) return
  
  const { dates, amounts } = getTrendData()
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}: ¥{c}'
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: {
        interval: activeTimeRange.value === 'month' ? 3 : 0,
        rotate: activeTimeRange.value === 'month' ? 45 : 0
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '¥{value}'
      }
    },
    series: [
      {
        name: '账单金额',
        type: chartType.value,
        data: amounts,
        itemStyle: {
          color: '#0052d9'
        },
        areaStyle: chartType.value === 'line' ? {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(0,82,217,0.5)' },
              { offset: 1, color: 'rgba(0,82,217,0.1)' }
            ]
          }
        } : undefined
      }
    ]
  }
  
  trendChartInstance.setOption(option)
}

// 格式化日期为 MM-DD 格式
const formatDate = (date) => {
  return `${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 格式化日期为 YYYY-MM 格式
const formatMonth = (date) => {
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`
}

// 监听时间范围和图表类型变化
watch([activeTimeRange, chartType], () => {
  updateTrendChart()
})

// 监听窗口大小变化，调整图表大小
const handleResize = () => {
  pieChartInstance?.resize()
  trendChartInstance?.resize()
}

// 悬浮按钮图标
const addIconFunc = () => h(AddIcon, { size: '24px' })

// 显示添加订单表单
const showAddOrderForm = () => {
  currentOrderData.value = {}
  orderFormVisible.value = true
}

// 处理订单提交
const handleOrderSubmit = async (formData) => {
  try {
    await orderStore.createOrder(formData)
    Toast({ message: '订单创建成功', theme: 'success' })

    // 更新图表
    initPieChart()
    updateTrendChart()
  } catch (error) {
    Toast({ message: error.message || '创建失败', theme: 'error' })
  }
}

// 处理表单关闭
const handleFormClose = () => {
  currentOrderData.value = {}
}

// 组件挂载后初始化图表
onMounted(() => {
  // 使用nextTick确保DOM已经渲染完成
  nextTick(() => {
    initPieChart()
    initTrendChart()
  })

  window.addEventListener('resize', handleResize)
})

// 组件卸载前移除事件监听
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  pieChartInstance?.dispose()
  trendChartInstance?.dispose()
})
</script>

<style scoped>
.home-container {
  padding: 15px;
  padding-top: 56px; /* 为固定导航栏留出空间 */
  padding-bottom: 80px; /* 为悬浮按钮留出空间 */
}

.welcome-card {
  background-color: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.welcome-card h2 {
  margin-top: 0;
  margin-bottom: 10px;
  font-size: 20px;
  color: #333;
}

.welcome-card p {
  margin: 0;
  color: #666;
}

.stats-card {
  background-color: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.stats-summary {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.stats-item {
  text-align: center;
  padding: 12px;
  border-radius: 8px;
  background-color: #f5f7fa;
}

.stats-value {
  font-size: 20px;
  font-weight: bold;
  color: #0052d9;
  margin-bottom: 4px;
}

.stats-label {
  font-size: 14px;
  color: #666;
}

.chart-card {
  background-color: #fff;
  border-radius: 12px;
  margin-bottom: 16px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.card-header {
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.card-tabs {
  padding: 0 10px;
  border-bottom: 1px solid #eee;
}

.card-body {
  padding: 15px;
}

.chart-container {
  height: 300px;
  width: 100%;
}

.chart-type-switch {
  display: flex;
  justify-content: center;
  margin-bottom: 16px;
}

.status-stats {
  background-color: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.status-stats h3 {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #333;
}

.status-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(80px, 1fr));
  gap: 12px;
}

.status-item {
  text-align: center;
  padding: 12px 8px;
  border-radius: 8px;
  background-color: #f5f7fa;
  border: 1px solid #e7e7e7;
}

.status-count {
  font-size: 18px;
  font-weight: bold;
  color: #0052d9;
  margin-bottom: 4px;
}

.status-name {
  font-size: 12px;
  color: #666;
}

@media (min-width: 768px) {
  .stats-summary {
    grid-template-columns: repeat(4, 1fr);
  }
}

/* 自定义悬浮按钮位置 */
:deep(.custom-fab) {
  bottom: 100px !important; /* 调整按钮位置，使其往上移 */
}
</style> 