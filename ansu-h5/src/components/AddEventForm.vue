<template>
  <t-popup :visible="visible" @update:visible="updateVisible" placement="bottom" :close-on-overlay-click="false">
    <div class="event-form">
      <div class="form-header">
        <h3>添加运输事件</h3>
        <t-button variant="text" size="small" @click="closeForm">
          <t-icon name="close" />
        </t-button>
      </div>

      <div class="form-content">
        <t-form ref="form" :data="formData" @submit="handleSubmit">
          <t-form-item label="事件类型" name="eventType" :rules="[{ required: true, message: '请选择事件类型' }]">
            <div class="event-type-buttons">
              <t-button 
                size="small" 
                :theme="formData.eventType === 'DRIVER_REST' ? 'primary' : 'default'"
                @click="selectEventType('DRIVER_REST', '司机休息')"
              >
                司机休息
              </t-button>
              <t-button 
                size="small" 
                :theme="formData.eventType === 'ACCIDENT' ? 'primary' : 'default'"
                @click="selectEventType('ACCIDENT', '交通事故')"
              >
                交通事故
              </t-button>
              <t-button 
                size="small" 
                :theme="formData.eventType === 'BREAKDOWN' ? 'primary' : 'default'"
                @click="selectEventType('BREAKDOWN', '车辆故障')"
              >
                车辆故障
              </t-button>
              <t-button 
                size="small" 
                :theme="formData.eventType === 'DELAY' ? 'primary' : 'default'"
                @click="selectEventType('DELAY', '运输延误')"
              >
                运输延误
              </t-button>
              <t-button 
                size="small" 
                :theme="formData.eventType === 'WEATHER' ? 'primary' : 'default'"
                @click="selectEventType('WEATHER', '天气影响')"
              >
                天气影响
              </t-button>
              <t-button 
                size="small" 
                :theme="formData.eventType === 'OTHER' ? 'primary' : 'default'"
                @click="selectEventType('OTHER', '其他事件')"
              >
                其他
              </t-button>
            </div>
          </t-form-item>
          
          <t-form-item label="事件标题" name="eventTitle" :rules="[{ required: true, message: '请输入事件标题' }]">
            <t-input v-model="formData.eventTitle" placeholder="请输入事件标题" />
          </t-form-item>
          
          <t-form-item label="事件描述" name="eventDescription">
            <t-textarea v-model="formData.eventDescription" placeholder="请详细描述事件情况" :maxlength="500" />
          </t-form-item>
          
          <t-form-item label="发生时间" name="eventTime" :rules="[{ required: true, message: '请选择发生时间' }]">
            <t-input
              v-model="formData.eventTime"
              placeholder="请选择发生时间 (YYYY-MM-DD HH:MM)"
              @click="showDateTimePicker"
              readonly
            />
          </t-form-item>
          
          <t-form-item label="发生地点" name="location">
            <t-input v-model="formData.location" placeholder="请输入发生地点（可选）" />
          </t-form-item>
          
          <t-form-item label="影响程度" name="impactLevel" :rules="[{ required: true, message: '请选择影响程度' }]">
            <div class="impact-level-buttons">
              <t-button 
                size="small" 
                :theme="formData.impactLevel === 'LOW' ? 'success' : 'default'"
                @click="formData.impactLevel = 'LOW'"
              >
                轻微
              </t-button>
              <t-button 
                size="small" 
                :theme="formData.impactLevel === 'MEDIUM' ? 'warning' : 'default'"
                @click="formData.impactLevel = 'MEDIUM'"
              >
                中等
              </t-button>
              <t-button 
                size="small" 
                :theme="formData.impactLevel === 'HIGH' ? 'danger' : 'default'"
                @click="formData.impactLevel = 'HIGH'"
              >
                严重
              </t-button>
            </div>
          </t-form-item>

          <div class="form-actions">
            <t-button theme="default" variant="outline" @click="closeForm">取消</t-button>
            <t-button theme="primary" type="submit" :loading="submitting">
              添加事件
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

// 显示日期时间选择器
const showDateTimePicker = () => {
  // 创建一个隐藏的input元素来触发日期时间选择器
  const input = document.createElement('input')
  input.type = 'datetime-local'
  input.style.position = 'absolute'
  input.style.left = '-9999px'

  // 如果已有值，设置为当前值
  if (formData.eventTime) {
    // 将显示格式转换为datetime-local格式
    const date = new Date(formData.eventTime.replace(' ', 'T'))
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    input.value = `${year}-${month}-${day}T${hours}:${minutes}`
  }

  input.addEventListener('change', (e) => {
    // 转换为显示格式
    const date = new Date(e.target.value)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    formData.eventTime = `${year}-${month}-${day} ${hours}:${minutes}`
    document.body.removeChild(input)
  })

  document.body.appendChild(input)
  input.click()
}

// 表单数据
const formData = reactive({
  orderId: '',
  eventType: '',
  eventTitle: '',
  eventDescription: '',
  eventTime: '',
  location: '',
  impactLevel: 'LOW'
})

// 更新visible状态
const updateVisible = (value) => {
  emit('update:visible', value)
}

// 选择事件类型
const selectEventType = (type, title) => {
  formData.eventType = type
  formData.eventTitle = title
}

// 监听visible变化，重置表单
watch(() => props.visible, (newVal) => {
  if (newVal) {
    resetForm()
    formData.orderId = props.orderId
    // 设置默认时间为当前时间
    const now = new Date()
    const year = now.getFullYear()
    const month = String(now.getMonth() + 1).padStart(2, '0')
    const day = String(now.getDate()).padStart(2, '0')
    const hours = String(now.getHours()).padStart(2, '0')
    const minutes = String(now.getMinutes()).padStart(2, '0')
    formData.eventTime = `${year}-${month}-${day} ${hours}:${minutes}`
  }
})

const resetForm = () => {
  formData.orderId = ''
  formData.eventType = ''
  formData.eventTitle = ''
  formData.eventDescription = ''
  formData.eventTime = ''
  formData.location = ''
  formData.impactLevel = 'LOW'
}

const handleSubmit = async ({ validateResult }) => {
  if (validateResult === true) {
    submitting.value = true

    try {
      // 处理时间字段，转换为后端需要的格式
      const submitData = { ...formData }
      if (submitData.eventTime) {
        // 转换为 YYYY-MM-DD HH:mm:ss 格式（后端自定义反序列化器支持多种格式）
        const date = new Date(submitData.eventTime)
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        const hours = String(date.getHours()).padStart(2, '0')
        const minutes = String(date.getMinutes()).padStart(2, '0')
        const seconds = String(date.getSeconds()).padStart(2, '0')
        submitData.eventTime = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
      }

      console.log('提交事件数据:', submitData) // 调试日志

      emit('submit', submitData)
      Toast.success('事件添加成功')
      closeForm()
    } catch (error) {
      console.error('提交事件失败:', error)
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
.event-form {
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

.event-type-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.impact-level-buttons {
  display: flex;
  gap: 8px;
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
