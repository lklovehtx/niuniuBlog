import axios from 'axios'
import { message } from 'ant-design-vue'

const service = axios.create({
  baseURL: '/jeecg-boot',
  timeout: 10000
})

service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('blog_token')
    if (token) {
      config.headers['X-Access-Token'] = token
    }
    const userId = localStorage.getItem('blog_user_id')
    if (userId) {
      config.headers['X-User-Id'] = userId
    }
    const username = localStorage.getItem('blog_username')
    if (username) {
      config.headers['X-Username'] = username
    }
    return config
  },
  error => {
    console.error('请求错误', error)
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.success) {
      return res
    } else {
      message.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  error => {
    if (error.response) {
      if (error.response.status === 401) {
        message.error('未授权，请重新登录')
        localStorage.removeItem('blog_token')
        localStorage.removeItem('blog_user_id')
        window.location.href = '#/blog/login'
      } else {
        message.error(error.response.data && error.response.data.message ? error.response.data.message : '请求失败')
      }
    } else {
      message.error('网络错误')
    }
    return Promise.reject(error)
  }
)

export default service
