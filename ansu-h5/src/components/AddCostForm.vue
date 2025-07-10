<template>
  <t-popup :visible="visible" @update:visible="updateVisible" placement="bottom" :close-on-overlay-click="false">
    <div class="cost-form">
      <div class="form-header">
        <h3>添加成本</h3>
        <t-button variant="text" size="small" @click="closeForm">
          <t-icon name="close" />
        </t-button>
      </div>

      <div class="form-content">
        <t-form ref="form" :data="formData" @submit="handleSubmit">
          <t-form-item label="成本类型" name="costType" :rules="[{ required: true, message: '请选择成本类型' }]">
            <div class="cost-type-buttons">
              <t-button 
                size="small" 
                :theme="formData.costType === 'FUEL' ? 'primary' : 'default'"
                @click="formData.costType = 'FUEL'; formData.costName = '加油费'"
              >
                加油费
              </t-button>
              <t-button 
                size="small" 
                :theme="formData.costType === 'TOLL' ? 'primary' : 'default'"
                @click="formData.costType = 'TOLL'; formData.costName = '过路费'"
              >
                过路费
              </t-button>
              <t-button 
                size="small" 
                :theme="formData.costType === 'FINE' ? 'primary' : 'default'"
                @click="formData.costType = 'FINE'; formData.costName = '违章费'"
              >
                违章费
              </t-button>
              <t-button 
                size="small" 
                :theme="formData.costType === 'MAINTENANCE' ? 'primary' : 'default'"
                @click="formData.costType = 'MAINTENANCE'; formData.costName = '维修费'"
              >
                维修费
              </t-button>
              <t-button 
                size="small" 
                :theme="formData.costType === 'OUTSOURCE' ? 'primary' : 'default'"
                @click="formData.costType = 'OUTSOURCE'; formData.costName = '外包费'"
              >
                外包费
              </t-button>
              <t-button 
                size="small" 
                :theme="formData.costType === 'OTHER' ? 'primary' : 'default'"
                @click="formData.costType = 'OTHER'; formData.costName = '其他费用'"
              >
                其他
              </t-button>
            </div>
          </t-form-item>
          
          <t-form-item label="成本名称" name="costName" :rules="[{ required: true, message: '请输入成本名称' }]">
            <t-input v-model="formData.costName" placeholder="请输入成本名称" />
          </t-form-item>
          
          <t-form-item label="金额" name="amount" :rules="[{ required: true, message: '请输入金额' }]">
            <t-input v-model="formData.amount" type="number" placeholder="请输入金额" />
          </t-form-item>
          
          <t-form-item label="发生日期" name="costDate" :rules="[{ required: true, message: '请选择发生日期' }]">
            <t-input
              v-model="formData.costDate"
              placeholder="请选择发生日期 (YYYY-MM-DD)"
              @click="showDatePicker"
              readonly
            />
          </t-form-item>
          
          <t-form-item label="描述" name="description">
            <t-textarea v-model="formData.description" placeholder="请输入费用描述（可选）" :maxlength="200" />
          </t-form-item>

          <div class="form-actions">
            <t-button theme="default" variant="outline" @click="closeForm">取消</t-button>
            <t-button theme="primary" type="submit" :loading="submitting">
              添加
            </t-button>
          </div>
        </t-form>
      </div>
    </div>
  </t-popup>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { Toast } from 'tdesign-mobile-vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  orderId: {
    type: [String, Number],
    required: true
  }
})

const emit = defineEmits(['update:visible', 'submit', 'close'])

const form = ref(null)
const submitting = ref(false)

// 显示日期选择器
const showDatePicker = () => {
  // 创建一个隐藏的input元素来触发日期选择器
  const input = document.createElement('input')
  input.type = 'date'
  input.style.position = 'absolute'
  input.style.left = '-9999px'
  input.value = formData.costDate

  input.addEventListener('change', (e) => {
    formData.costDate = e.target.value
    document.body.removeChild(input)
  })

  document.body.appendChild(input)
  input.click()
}

// 表单数据
const formData = reactive({
  orderId: '',
  costType: '',
  costName: '',
  amount: '',
  costDate: '',
  description: ''
})

// 更新visible状态
const updateVisible = (value) => {
  emit('update:visible', value)
}

// 监听visible变化，重置表单
watch(() => props.visible, (newVal) => {
  if (newVal) {
    resetForm()
    formData.orderId = props.orderId
    // 设置默认日期为今天
    const today = new Date()
    const year = today.getFullYear()
    const month = String(today.getMonth() + 1).padStart(2, '0')
    const day = String(today.getDate()).padStart(2, '0')
    formData.costDate = `${year}-${month}-${day}`
  }
})

const resetForm = () => {
  formData.orderId = ''
  formData.costType = ''
  formData.costName = ''
  formData.amount = ''
  formData.costDate = ''
  formData.description = ''
}

const handleSubmit = async ({ validateResult }) => {
  if (validateResult === true) {
    submitting.value = true
    
    try {
      // 处理数字字段
      const submitData = { ...formData }
      submitData.amount = parseFloat(submitData.amount)
      
      emit('submit', submitData)
      Toast.success('成本添加成功')
      closeForm()
    } catch (error) {
      Toast.error(error.message || '添加失败')
    } finally {
      submitting.value = false
    }
  }
}

const closeForm = () => {
  emit('update:visible', false)
  emit('close')
}
</script>

<style scoped>
.cost-form {
  background: white;
  border-radius: 12px 12px 0 0;
  max-height: 80vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #eee;
  background: white;
  position: sticky;
  top: 0;
  z-index: 10;
}

.form-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.form-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.cost-type-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.form-actions {
  display: flex;
  gap: 12px;
  padding-top: 20px;
  border-top: 1px solid #eee;
  position: sticky;
  bottom: 0;
  background: white;
  margin: 0 -20px -20px -20px;
  padding: 20px;
}

.form-actions .t-button {
  flex: 1;
}
</style>
