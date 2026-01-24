import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authApi, type User } from '@/api'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const user = ref<User | null>(null)

  const isAuthenticated = () => !!token.value

  const login = async (username: string, password: string) => {
    const response = await authApi.login(username, password)

    // 检查响应数据是否存在
    if (!response || !response.data) {
      const errorMsg = (response as any)?.message || '登录失败：服务器返回数据为空'
      console.error('Login failed:', errorMsg)
      throw new Error(errorMsg)
    }

    if (!response.data.token) {
      console.error('Login failed: No token in response')
      throw new Error('登录失败：未获取到 token')
    }

    token.value = response.data.token
    user.value = response.data
    localStorage.setItem('token', token.value)
    return response
  }

  const register = async (data: {
    username: string
    password: string
    realName: string
    phone: string
    email: string
  }) => {
    const response = await authApi.register(data)

    // 检查响应数据是否存在
    if (!response || !response.data) {
      const errorMsg = (response as any)?.message || '注册失败：服务器返回数据为空'
      console.error('Register failed:', errorMsg)
      throw new Error(errorMsg)
    }

    if (!response.data.token) {
      console.error('Register failed: No token in response')
      throw new Error('注册失败：未获取到 token')
    }

    token.value = response.data.token
    user.value = response.data
    localStorage.setItem('token', token.value)
    return response
  }

  const logout = () => {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
  }

  return { token, user, isAuthenticated, login, register, logout }
})
