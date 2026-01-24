<template>
  <div class="bg-white rounded-lg shadow-md p-6">
    <div class="mb-6 flex justify-between items-center">
      <h2 class="text-2xl font-bold text-gray-800">图书列表</h2>
      <button
        v-if="isAdmin"
        @click="showAddModal = true"
        class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition"
      >
        添加图书
      </button>
    </div>

    <div class="mb-4 flex gap-4">
      <input
        v-model="searchKeyword"
        type="text"
        placeholder="搜索书名、作者或ISBN..."
        class="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        @keyup.enter="loadBooks"
      />
      <button
        @click="loadBooks"
        class="bg-blue-500 text-white px-6 py-2 rounded hover:bg-blue-600 transition"
      >
        搜索
      </button>
    </div>

    <div class="overflow-x-auto">
      <table class="min-w-full bg-white">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ISBN</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">书名</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">作者</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">出版社</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">价格</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">可借/总数</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">操作</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-200">
          <tr v-for="book in books" :key="book.id" class="hover:bg-gray-50">
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ book.isbn }}</td>
            <td class="px-6 py-4 text-sm text-gray-900">{{ book.title }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ book.author }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ book.publisher || '-' }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">¥{{ book.price }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm">
              <span :class="book.availableQuantity > 0 ? 'text-green-600' : 'text-red-600'">
                {{ book.availableQuantity }} / {{ book.totalQuantity }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm">
              <button
                v-if="book.availableQuantity > 0"
                @click="handleBorrow(book.id)"
                class="px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600 transition mr-2"
              >
                借阅
              </button>
              <button
                v-if="isAdmin"
                @click="handleEdit(book)"
                class="px-3 py-1 bg-yellow-500 text-white rounded hover:bg-yellow-600 transition mr-2"
              >
                编辑
              </button>
              <button
                v-if="isAdmin"
                @click="handleDelete(book.id)"
                class="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600 transition"
              >
                删除
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="mt-4 flex justify-between items-center">
      <span class="text-sm text-gray-600">
        第 {{ page }} 页，共 {{ totalPages }} 页，总计 {{ total }} 条
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
import { ref, onMounted, computed } from 'vue'
import { bookApi, type Book } from '@/api'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

const books = ref<Book[]>([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const showAddModal = ref(false)

const isAdmin = computed(() => {
  const user = authStore.user
  return user?.role === 'ADMIN'
})

const totalPages = computed(() => Math.ceil(total.value / size.value))

const loadBooks = async () => {
  try {
    const response = await bookApi.getList(page.value, size.value, searchKeyword.value)
    books.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    console.error('加载图书列表失败:', error)
  }
}

const changePage = (newPage: number) => {
  page.value = newPage
  loadBooks()
}

const handleBorrow = async (bookId: number) => {
  try {
    await bookApi.borrow(bookId)
    alert('借阅成功')
    loadBooks()
  } catch (error: any) {
    alert(error.message || '借阅失败')
  }
}

const handleEdit = (book: Book) => {
  alert(`编辑图书: ${book.title}`)
}

const handleDelete = async (bookId: number) => {
  if (confirm('确定要删除这本书吗？')) {
    try {
      await bookApi.delete(bookId)
      alert('删除成功')
      loadBooks()
    } catch (error: any) {
      alert(error.message || '删除失败')
    }
  }
}

onMounted(() => {
  loadBooks()
})
</script>
