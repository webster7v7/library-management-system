<template>
  <div class="bg-white rounded-lg shadow-md p-4 md:p-6">
    <h2 class="text-xl md:text-2xl font-bold text-gray-800 mb-4 md:mb-6">借阅历史</h2>

    <!-- Table View for Desktop -->
    <div class="hidden md:block overflow-x-auto">
      <table class="min-w-full bg-white">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-4 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">图书名称</th>
            <th class="px-4 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">作者</th>
            <th class="px-4 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ISBN</th>
            <th class="px-4 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">借阅日期</th>
            <th class="px-4 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">应还日期</th>
            <th class="px-4 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">归还日期</th>
            <th class="px-4 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">状态</th>
            <th class="px-4 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">续借次数</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-200">
          <tr v-for="record in records" :key="record.id" class="hover:bg-gray-50">
            <td class="px-4 md:px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ record.bookTitle }}</td>
            <td class="px-4 md:px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ record.bookAuthor }}</td>
            <td class="px-4 md:px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ record.bookIsbn }}</td>
            <td class="px-4 md:px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ formatDate(record.borrowDate) }}</td>
            <td class="px-4 md:px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ formatDate(record.dueDate) }}</td>
            <td class="px-4 md:px-6 py-4 whitespace-nowrap text-sm text-gray-900">
              {{ record.returnDate ? formatDate(record.returnDate) : '-' }}
            </td>
            <td class="px-4 md:px-6 py-4 whitespace-nowrap text-sm">
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
            <td class="px-4 md:px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ record.renewCount }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Card View for Mobile -->
    <div class="md:hidden space-y-4">
      <div
        v-for="record in records"
        :key="record.id"
        class="border border-gray-200 rounded-lg p-4 bg-gray-50"
      >
        <div class="flex justify-between items-start mb-2">
          <h4 class="font-semibold text-gray-800 text-sm">{{ record.bookTitle }}</h4>
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
        </div>
        <div class="text-sm text-gray-600 space-y-1">
          <p><span class="font-medium">作者：</span>{{ record.bookAuthor }}</p>
          <p><span class="font-medium">ISBN：</span>{{ record.bookIsbn }}</p>
          <p><span class="font-medium">借阅日期：</span>{{ formatDate(record.borrowDate) }}</p>
          <p><span class="font-medium">应还日期：</span>{{ formatDate(record.dueDate) }}</p>
          <p><span class="font-medium">归还日期：</span>{{ record.returnDate ? formatDate(record.returnDate) : '-' }}</p>
          <p><span class="font-medium">续借次数：</span>{{ record.renewCount }}</p>
        </div>
      </div>
    </div>

    <div class="mt-4 md:mt-6 flex flex-col sm:flex-row justify-between items-center gap-3">
      <span class="text-sm text-gray-600">
        第 {{ page }} 页，共 {{ totalPages }} 页
      </span>
      <div class="flex gap-2 w-full sm:w-auto">
        <button
          @click="changePage(page - 1)"
          :disabled="page <= 1"
          class="flex-1 sm:flex-none px-4 py-2 bg-gray-200 rounded hover:bg-gray-300 transition disabled:bg-gray-100 disabled:cursor-not-allowed text-sm"
        >
          上一页
        </button>
        <button
          @click="changePage(page + 1)"
          :disabled="page >= totalPages"
          class="flex-1 sm:flex-none px-4 py-2 bg-gray-200 rounded hover:bg-gray-300 transition disabled:bg-gray-100 disabled:cursor-not-allowed text-sm"
        >
          下一页
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { borrowApi, type BorrowRecordDTO } from '@/api'

const records = ref<BorrowRecordDTO[]>([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const totalPages = ref(1)

const loadHistory = async () => {
  try {
    const response = await borrowApi.getHistory(page.value, size.value)
    records.value = response.data.records
    total.value = response.data.total
    totalPages.value = Math.ceil(total.value / size.value)
  } catch (error) {
    console.error('加载借阅历史失败:', error)
  }
}

const changePage = (newPage: number) => {
  page.value = newPage
  loadHistory()
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
  loadHistory()
})
</script>
