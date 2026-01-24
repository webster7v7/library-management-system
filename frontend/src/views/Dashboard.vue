<template>
  <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
    <div class="bg-white rounded-lg shadow-md p-6">
      <div class="flex items-center justify-between">
        <div>
          <p class="text-gray-500 text-sm">总图书数</p>
          <p class="text-3xl font-bold text-gray-800">{{ stats.totalBooks }}</p>
        </div>
        <div class="bg-blue-100 p-3 rounded-full">
          <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
          </svg>
        </div>
      </div>
    </div>

    <div class="bg-white rounded-lg shadow-md p-6">
      <div class="flex items-center justify-between">
        <div>
          <p class="text-gray-500 text-sm">可借阅</p>
          <p class="text-3xl font-bold text-green-600">{{ stats.availableBooks }}</p>
        </div>
        <div class="bg-green-100 p-3 rounded-full">
          <svg class="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
          </svg>
        </div>
      </div>
    </div>

    <div class="bg-white rounded-lg shadow-md p-6">
      <div class="flex items-center justify-between">
        <div>
          <p class="text-gray-500 text-sm">借阅中</p>
          <p class="text-3xl font-bold text-blue-600">{{ stats.borrowedBooks }}</p>
        </div>
        <div class="bg-blue-100 p-3 rounded-full">
          <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
        </div>
      </div>
    </div>

    <div class="bg-white rounded-lg shadow-md p-6">
      <div class="flex items-center justify-between">
        <div>
          <p class="text-gray-500 text-sm">用户总数</p>
          <p class="text-3xl font-bold text-purple-600">{{ stats.totalUsers }}</p>
        </div>
        <div class="bg-purple-100 p-3 rounded-full">
          <svg class="w-6 h-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
          </svg>
        </div>
      </div>
    </div>
  </div>

  <div class="bg-white rounded-lg shadow-md p-6">
    <h3 class="text-xl font-bold text-gray-800 mb-4">最新借阅记录</h3>
    <div class="overflow-x-auto">
      <table class="min-w-full bg-white">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">图书</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">借阅人</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">借阅日期</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">状态</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-200">
          <tr v-for="record in recentRecords" :key="record.id" class="hover:bg-gray-50">
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ record.bookTitle }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ record.username }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ formatDate(record.borrowDate) }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm">
              <span
                :class="{
                  'bg-green-100 text-green-800': record.status === 'RETURNED',
                  'bg-blue-100 text-blue-800': record.status === 'BORROWED',
                  'bg-red-100 text-red-800': record.status === 'OVERDUE'
                }"
                class="px-2 py-1 rounded-full text-xs font-medium"
              >
                {{ getStatusText(record.status) }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { borrowApi } from '@/api'

interface DashboardRecord {
  id: number
  bookTitle: string
  username: string
  borrowDate: string
  status: string
}

const stats = ref({
  totalBooks: 0,
  availableBooks: 0,
  borrowedBooks: 0,
  totalUsers: 0
})

const recentRecords = ref<DashboardRecord[]>([])

const loadDashboard = async () => {
  try {
    const response = await borrowApi.getList(1, 5)
    recentRecords.value = response.data.records.map((r: any) => ({
      id: r.id,
      bookTitle: `图书 ${r.bookId}`,
      username: `用户 ${r.userId}`,
      borrowDate: r.borrowDate,
      status: r.status
    }))
    stats.value = {
      totalBooks: 120,
      availableBooks: 85,
      borrowedBooks: 35,
      totalUsers: 50
    }
  } catch (error) {
    console.error('加载仪表盘数据失败:', error)
  }
}

const formatDate = (dateStr: string) => {
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    BORROWED: '借阅中',
    RETURNED: '已归还',
    OVERDUE: '已逾期'
  }
  return statusMap[status] || status
}

onMounted(() => {
  loadDashboard()
})
</script>
