<template>
  <div class="bg-white rounded-lg shadow-md p-6">
    <h2 class="text-2xl font-bold text-gray-800 mb-6">我的借阅</h2>
    
    <div class="overflow-x-auto">
      <table class="min-w-full bg-white">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">图书ID</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">借阅日期</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">应还日期</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">状态</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">续借次数</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">操作</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-200">
          <tr v-for="record in records" :key="record.id" class="hover:bg-gray-50">
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ record.bookId }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ formatDate(record.borrowDate) }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ formatDate(record.dueDate) }}</td>
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
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ record.renewCount }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm">
              <button
                v-if="record.status === 'BORROWED'"
                @click="handleReturn(record.id)"
                class="px-3 py-1 bg-orange-500 text-white rounded hover:bg-orange-600 transition mr-2"
              >
                归还
              </button>
              <button
                v-if="record.status === 'BORROWED' && record.renewCount < 3"
                @click="handleRenew(record.id)"
                class="px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600 transition"
              >
                续借
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <div class="mt-4 flex justify-between items-center">
      <span class="text-sm text-gray-600">
        第 {{ page }} 页，共 {{ totalPages }} 页
      </span>
      <div class="flex gap-2">
        <button
          @click="changePage(page - 1)"
          :disabled="page <= 1"
          class="px-4 py-2 bg-gray-200 rounded hover:bg-gray-300 transition disabled:bg-gray-100 disabled:cursor-not-allowed"
        >
          上一页
        </button>
        <button
          @click="changePage(page + 1)"
          :disabled="page >= totalPages"
          class="px-4 py-2 bg-gray-200 rounded hover:bg-gray-300 transition disabled:bg-gray-100 disabled:cursor-not-allowed"
        >
          下一页
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { borrowApi, type BorrowRecord } from '@/api'

const records = ref<BorrowRecord[]>([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const totalPages = ref(1)

const loadRecords = async () => {
  try {
    const response = await borrowApi.getList(page.value, size.value)
    records.value = response.data.records
    total.value = response.data.total
    totalPages.value = Math.ceil(total.value / size.value)
  } catch (error) {
    console.error('加载借阅记录失败:', error)
  }
}

const changePage = (newPage: number) => {
  page.value = newPage
  loadRecords()
}

const handleReturn = async (recordId: number) => {
  try {
    await borrowApi.return(recordId)
    alert('归还成功')
    loadRecords()
  } catch (error: any) {
    alert(error.message || '归还失败')
  }
}

const handleRenew = async (recordId: number) => {
  try {
    await borrowApi.renew(recordId)
    alert('续借成功')
    loadRecords()
  } catch (error: any) {
    alert(error.message || '续借失败')
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
  loadRecords()
})
</script>
