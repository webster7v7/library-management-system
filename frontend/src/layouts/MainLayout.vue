<template>
  <div class="flex min-h-screen bg-gray-100 relative">
    <!-- Mobile Sidebar Overlay -->
    <div
      v-if="isMobileSidebarOpen"
      @click="closeMobileSidebar"
      class="fixed inset-0 bg-black/50 z-40 md:hidden"
      transition
    ></div>

    <!-- Mobile Header -->
    <header class="md:hidden fixed top-0 left-0 right-0 bg-blue-800 text-white p-4 z-30 flex items-center justify-between">
      <div class="flex items-center">
        <button
          @click="openMobileSidebar"
          class="p-2 hover:bg-blue-700 rounded transition"
        >
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
          </svg>
        </button>
        <h1 class="text-lg font-bold ml-3">图书馆管理系统</h1>
      </div>
      <button
        @click="handleLogout"
        class="p-2 hover:bg-blue-700 rounded transition"
      >
        <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
        </svg>
      </button>
    </header>

    <!-- Sidebar -->
    <aside
      :class="[
        'bg-blue-800 text-white transition-all duration-300 ease-in-out z-50',
        isMobileSidebarOpen ? 'translate-x-0' : '-translate-x-full',
        'md:translate-x-0 md:w-64 md:fixed md:h-screen'
      ]"
      class="fixed left-0 top-0 bottom-0 w-64 overflow-y-auto"
    >
      <div class="p-6">
        <h1 class="text-xl font-bold">图书馆管理系统</h1>
      </div>
      <nav class="mt-6">
        <router-link
          to="/"
          @click="closeMobileSidebar"
          class="flex items-center px-6 py-3 text-blue-100 hover:bg-blue-700 transition"
          :class="{ 'bg-blue-900': $route.path === '/' }"
        >
          <svg class="w-5 h-5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
          </svg>
          仪表盘
        </router-link>
        <router-link
          to="/books"
          @click="closeMobileSidebar"
          class="flex items-center px-6 py-3 text-blue-100 hover:bg-blue-700 transition"
          :class="{ 'bg-blue-900': $route.path === '/books' }"
        >
          <svg class="w-5 h-5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253" />
          </svg>
          图书管理
        </router-link>
        <router-link
          to="/borrow"
          @click="closeMobileSidebar"
          class="flex items-center px-6 py-3 text-blue-100 hover:bg-blue-700 transition"
          :class="{ 'bg-blue-900': $route.path === '/borrow' }"
        >
          <svg class="w-5 h-5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
          </svg>
          我的借阅
        </router-link>
        <router-link
          to="/history"
          @click="closeMobileSidebar"
          class="flex items-center px-6 py-3 text-blue-100 hover:bg-blue-700 transition"
          :class="{ 'bg-blue-900': $route.path === '/history' }"
        >
          <svg class="w-5 h-5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          借阅历史
        </router-link>
      </nav>
      <div class="hidden md:block absolute bottom-0 w-64 p-6">
        <button
          @click="handleLogout"
          class="w-full bg-red-500 text-white py-2 rounded hover:bg-red-600 transition flex items-center justify-center"
        >
          <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
          </svg>
          退出登录
        </button>
      </div>
    </aside>

    <!-- Main Content -->
    <main class="flex-1 md:ml-64 pt-16 md:pt-0 p-4 md:p-8 transition-all">
      <div class="mb-6 md:mb-8">
        <h2 class="text-xl md:text-2xl font-bold text-gray-800">{{ pageTitle }}</h2>
      </div>
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const isMobileSidebarOpen = ref(false)

const pageTitle = computed(() => {
  const titleMap: Record<string, string> = {
    '/': '仪表盘',
    '/books': '图书管理',
    '/borrow': '我的借阅',
    '/history': '借阅历史'
  }
  return titleMap[route.path] || '仪表盘'
})

const openMobileSidebar = () => {
  isMobileSidebarOpen.value = true
}

const closeMobileSidebar = () => {
  isMobileSidebarOpen.value = false
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}
</script>
