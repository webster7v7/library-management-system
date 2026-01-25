import api from '@/utils/request'

export interface Book {
  id: number
  isbn: string
  title: string
  author: string
  publisher: string
  publishDate: string
  categoryId: number
  price: number
  totalQuantity: number
  availableQuantity: number
  location: string
  description: string
}

export interface BorrowRecord {
  id: number
  userId: number
  bookId: number
  borrowDate: string
  dueDate: string
  returnDate: string
  status: string
  renewCount: number
}

export interface BorrowRecordDTO {
  id: number
  userId: number
  bookId: number
  bookTitle: string
  bookAuthor: string
  bookIsbn: string
  borrowDate: string
  dueDate: string
  returnDate: string
  status: string
  renewCount: number
}

export interface User {
  id: number
  username: string
  realName: string
  phone: string
  email: string
  role: string
}

export interface RegisterData {
  username: string
  password: string
  realName: string
  phone: string
  email: string
}

export const authApi = {
  login: (username: string, password: string) => {
    return api.post('/auth/login', { username, password })
  },
  
  register: (data: RegisterData) => {
    return api.post('/auth/register', data)
  },
  
  logout: () => {
    return api.post('/auth/logout')
  }
}

export const bookApi = {
  getList: (page: number, size: number, keyword?: string) => {
    return api.get('/books', { params: { page, size, keyword } })
  },
  
  getById: (id: number) => {
    return api.get(`/books/${id}`)
  },
  
  add: (book: Book) => {
    return api.post('/books', book)
  },
  
  update: (id: number, book: Book) => {
    return api.put(`/books/${id}`, book)
  },
  
  delete: (id: number) => {
    return api.delete(`/books/${id}`)
  }
}

export const borrowApi = {
  getList: (page: number, size: number, userId?: number, status?: string) => {
    return api.get('/borrow', { params: { page, size, userId, status } })
  },

  getMyBorrows: (page: number, size: number) => {
    return api.get('/borrow/my-borrows', { params: { page, size } })
  },

  borrow: (bookId: number) => {
    return api.post(`/borrow/${bookId}`)
  },

  return: (recordId: number) => {
    return api.put(`/borrow/return/${recordId}`)
  },

  renew: (recordId: number) => {
    return api.put(`/borrow/renew/${recordId}`)
  },

  getHistory: (page: number, size: number) => {
    return api.get('/borrow/history', { params: { page, size } })
  }
}

export interface DashboardStats {
  totalBooks: number
  availableBooks: number
  borrowedBooks: number
  totalUsers: number
}

export interface DashboardRecord {
  id: number
  bookTitle: string
  username: string
  borrowDate: string
  status: string
}

export interface DashboardResponse {
  stats: DashboardStats
  recentRecords: DashboardRecord[]
}

export const dashboardApi = {
  getData: () => {
    return api.get('/dashboard')
  }
}
