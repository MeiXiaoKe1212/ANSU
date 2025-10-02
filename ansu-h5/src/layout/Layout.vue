<template>
  <div class="layout">
    <main class="main-content">
      <router-view />
    </main>
    
    <t-tab-bar v-model="activeTab" theme="tag" :split="false">
      <t-tab-bar-item v-for="item in navItems" :key="item.path" :value="item.path">
        <template #icon>
          <t-icon :name="item.tIcon" />
        </template>
        {{ item.label }}
      </t-tab-bar-item>
    </t-tab-bar>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Icon as TIcon } from 'tdesign-icons-vue-next'

const route = useRoute()
const router = useRouter()
const activeTab = ref(route.path)

const navItems = [
  { path: '/home', label: '主页', tIcon: 'home' },
  { path: '/list', label: '订单', tIcon: 'app' }
]

const currentRoute = computed(() => route.path)

// 路由导航
const navigateTo = (path) => {
  if (currentRoute.value !== path) {
    router.push(path)
  }
}

// 监听Tab Bar值变化
watch(activeTab, (newPath) => {
  if (currentRoute.value !== newPath) {
    navigateTo(newPath)
  }
})

// 监听路由变化，确保Tab Bar选中状态与路由同步
watch(() => route.path, (newPath) => {
  activeTab.value = newPath
})
</script>

<style scoped>
.layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.main-content {
  flex: 1;
  padding: 0;
  margin-bottom: 60px;
}
</style> 