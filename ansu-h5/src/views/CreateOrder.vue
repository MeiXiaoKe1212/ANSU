<template>
  <div>
    <t-navbar :title="isEdit ? '编辑订单' : '创建订单'" fixed>
      <template #left>
        <t-icon name="chevron-left" @click="handleBack" />
      </template>
      <template #right>
        <span @click="handleSave" class="save-btn">保存</span>
      </template>
    </t-navbar>
    
    <div class="create-order-container">
      <form @submit.prevent="handleSave">
        <!-- 客户信息 -->
        <div class="form-section">
          <h4>客户信息</h4>
          <div class="form-group">
            <label>客户姓名 *</label>
            <input 
              type="text" 
              v-model="formData.customerName" 
              placeholder="请输入客户姓名"
              required
            />
          </div>
          <div class="form-group">
            <label>联系电话</label>
            <input 
              type="tel" 
              v-model="formData.customerPhone" 
              placeholder="请输入联系电话"
            />
          </div>
        </div>
        
        <!-- 地址信息 -->
        <div class="form-section">
          <h4>地址信息</h4>
          <div class="form-group">
            <label>取货地址 *</label>
            <textarea 
              v-model="formData.pickupAddress" 
              placeholder="请输入详细的取货地址"
              rows="3"
              required
            ></textarea>
          </div>
          <div class="form-group">
            <label>送货地址 *</label>
            <textarea 
              v-model="formData.deliveryAddress" 
              placeholder="请输入详细的送货地址"
              rows="3"
              required
            ></textarea>
          </div>
        </div>
        
        <!-- 货物信息 -->
        <div class="form-section">
          <h4>货物信息</h4>
          <div class="form-group">
            <label>货物名称</label>
            <input 
              type="text" 
              v-model="formData.goodsName" 
              placeholder="请输入货物名称"
            />
          </div>
          <div class="form-row">
            <div class="form-group half">
              <label>重量 (kg)</label>
              <input 
                type="number" 
                v-model="formData.goodsWeight" 
                placeholder="重量"
                step="0.1"
                min="0"
              />
            </div>
            <div class="form-group half">
              <label>体积 (m³)</label>
              <input 
                type="number" 
                v-model="formData.goodsVolume" 
                placeholder="体积"
                step="0.1"
                min="0"
              />
            </div>
          </div>
        </div>
        
        <!-- 费用信息 -->
        <div class="form-section">
          <h4>费用信息</h4>
          <div class="form-group">
            <label>运输费用 (元) *</label>
            <input 
              type="number" 
              v-model="formData.transportFee" 
              placeholder="请输入运输费用"
              step="0.01"
              min="0"
              required
            />
          </div>
        </div>
        
        <!-- 备注信息 -->
        <div class="form-section">
          <h4>备注信息</h4>
          <div class="form-group">
            <label>备注</label>
            <textarea 
              v-model="formData.remark" 
              placeholder="请输入备注信息（可选）"
              rows="3"
            ></textarea>
          </div>
        </div>
        
        <!-- 提交按钮 -->
        <div class="submit-section">
          <t-button 
            theme="primary" 
            block 
            :loading="isLoading"
            @click="handleSave"
          >
            {{ isLoading ? '保存中...' : (isEdit ? '更新订单' : '创建订单') }}
          </t-button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { createOrder, updateOrder, getOrderById } from '../api/order'
import { Toast } from 'tdesign-mobile-vue'

const router = useRouter()
const route = useRoute()

const isEdit = computed(() => !!route.params.id)
const isLoading = ref(false)

const formData = ref({
  orderNo: '',
  customerName: '',
  customerPhone: '',
  pickupAddress: '',
  deliveryAddress: '',
  goodsName: '',
  goodsWeight: '',
  goodsVolume: '',
  transportFee: '',
  remark: ''
})

// 生成订单号
const generateOrderNo = () => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const random = String(Math.floor(Math.random() * 1000)).padStart(3, '0')
  return `ORD${year}${month}${day}${random}`
}

// 获取订单详情（编辑模式）
const fetchOrderDetail = async () => {
  if (!isEdit.value) return
  
  try {
    const response = await getOrderById(route.params.id)
    if (response.code === 200) {
      const order = response.data
      formData.value = {
        orderNo: order.orderNo,
        customerName: order.customerName,
        customerPhone: order.customerPhone || '',
        pickupAddress: order.pickupAddress,
        deliveryAddress: order.deliveryAddress,
        goodsName: order.goodsName || '',
        goodsWeight: order.goodsWeight || '',
        goodsVolume: order.goodsVolume || '',
        transportFee: order.transportFee,
        remark: order.remark || ''
      }
    }
  } catch (error) {
    console.error('获取订单详情失败:', error)
    Toast({
      message: '获取订单详情失败',
      theme: 'error'
    })
    router.back()
  }
}

// 验证表单
const validateForm = () => {
  if (!formData.value.customerName.trim()) {
    Toast({
      message: '请输入客户姓名',
      theme: 'warning'
    })
    return false
  }
  
  if (!formData.value.pickupAddress.trim()) {
    Toast({
      message: '请输入取货地址',
      theme: 'warning'
    })
    return false
  }
  
  if (!formData.value.deliveryAddress.trim()) {
    Toast({
      message: '请输入送货地址',
      theme: 'warning'
    })
    return false
  }
  
  if (!formData.value.transportFee || formData.value.transportFee <= 0) {
    Toast({
      message: '请输入有效的运输费用',
      theme: 'warning'
    })
    return false
  }
  
  return true
}

// 保存订单
const handleSave = async () => {
  if (!validateForm()) return
  
  isLoading.value = true
  
  try {
    const orderData = {
      ...formData.value,
      goodsWeight: formData.value.goodsWeight ? parseFloat(formData.value.goodsWeight) : null,
      goodsVolume: formData.value.goodsVolume ? parseFloat(formData.value.goodsVolume) : null,
      transportFee: parseFloat(formData.value.transportFee)
    }
    
    let response
    if (isEdit.value) {
      response = await updateOrder(route.params.id, orderData)
    } else {
      // 新建订单时生成订单号
      orderData.orderNo = generateOrderNo()
      response = await createOrder(orderData)
    }
    
    if (response.code === 200) {
      Toast({
        message: isEdit.value ? '订单更新成功' : '订单创建成功',
        theme: 'success'
      })
      
      // 跳转到订单详情页
      if (isEdit.value) {
        router.back()
      } else {
        router.replace(`/order-detail/${response.data.id}`)
      }
    }
  } catch (error) {
    console.error('保存订单失败:', error)
    Toast({
      message: '保存失败，请稍后重试',
      theme: 'error'
    })
  } finally {
    isLoading.value = false
  }
}

// 返回上一页
const handleBack = () => {
  router.back()
}

onMounted(() => {
  if (isEdit.value) {
    fetchOrderDetail()
  }
})
</script>

<style scoped>
.create-order-container {
  padding: 15px;
  padding-top: 71px; /* 为固定导航栏留出空间 */
  padding-bottom: 20px;
  background: #f5f5f5;
  min-height: 100vh;
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
  margin-bottom: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.form-section h4 {
  margin: 0 0 20px 0;
  font-size: 16px;
  color: #333;
  font-weight: 600;
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

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s ease;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #0052d9;
}

.form-group textarea {
  resize: vertical;
  min-height: 80px;
}

.form-row {
  display: flex;
  gap: 15px;
}

.form-group.half {
  flex: 1;
}

.submit-section {
  margin-top: 30px;
}
</style>
