import { register as registerApi, login as loginApi } from '@/api/blog/user'
import { sendVerificationCode, verifyCode } from '@/api/blog/mail'

const state = {
  userId: localStorage.getItem('blog_user_id') || '',
  username: localStorage.getItem('blog_username') || '',
  nickname: localStorage.getItem('blog_nickname') || '',
  avatar: localStorage.getItem('blog_avatar') || '',
  token: localStorage.getItem('blog_token') || '',
  isLoggedIn: !!localStorage.getItem('blog_token'),
  emailVerified: localStorage.getItem('blog_email_verified') === 'true' || false
}

const mutations = {
  SET_USER(state, user) {
    state.userId = user.id
    state.username = user.username
    state.nickname = user.nickname
    state.avatar = user.avatar || ''
    state.isLoggedIn = true
  },
  SET_TOKEN(state, token) {
    state.token = token
    state.isLoggedIn = true
    localStorage.setItem('blog_token', token)
  },
  SET_USER_INFO(state, user) {
    state.userId = user.id
    state.nickname = user.nickname || user.username
    state.avatar = user.avatar || ''
    state.email = user.email || ''
    state.emailVerified = user.emailVerified || false
    localStorage.setItem('blog_user_id', user.id)
    localStorage.setItem('blog_username', user.username)
    localStorage.setItem('blog_nickname', user.nickname || user.username)
    localStorage.setItem('blog_avatar', user.avatar || '')
    localStorage.setItem('blog_email_verified', String(user.emailVerified || false))
  },
  SET_EMAIL_VERIFIED(state, verified) {
    state.emailVerified = verified
    localStorage.setItem('blog_email_verified', String(verified))
  },
  LOGOUT(state) {
    state.userId = ''
    state.username = ''
    state.nickname = ''
    state.avatar = ''
    state.token = ''
    state.isLoggedIn = false
    state.emailVerified = false
    localStorage.removeItem('blog_token')
    localStorage.removeItem('blog_user_id')
    localStorage.removeItem('blog_username')
    localStorage.removeItem('blog_nickname')
    localStorage.removeItem('blog_avatar')
    localStorage.removeItem('blog_email_verified')
  }
}

const actions = {
  async login({ commit }, userData) {
    // 调用后端API登录
    const response = await loginApi(userData)
    
    if (response.success) {
      const user = response.result
      commit('SET_TOKEN', user.id)
      commit('SET_USER_INFO', user)
      return { success: true, result: user }
    } else {
      throw new Error(response.message || '登录失败')
    }
  },
  async register(_, userData) {
    // 调用后端API注册
    const response = await registerApi(userData)
    
    if (response.success) {
      return { success: true, result: response.result }
    } else {
      throw new Error(response.message || '注册失败')
    }
  },
  async getVerificationCode(_, email) {
    const response = await sendVerificationCode(email)
    if (response.success) {
      return response.result
    } else {
      throw new Error(response.message || '发送验证码失败')
    }
  },
  async verifyEmail({ commit }, { code }) {
    // 获取用户邮箱
    const email = localStorage.getItem('blog_email') || ''
    const response = await verifyCode(email, code)
    if (response.success) {
      // 更新邮箱验证状态
      commit('SET_EMAIL_VERIFIED', true)
      return response.result
    } else {
      throw new Error(response.message || '验证失败')
    }
  },
  logout({ commit }) {
    commit('LOGOUT')
  }
}

const getters = {
  userId: state => state.userId,
  username: state => state.username,
  nickname: state => state.nickname,
  avatar: state => state.avatar,
  isLoggedIn: state => state.isLoggedIn,
  emailVerified: state => state.emailVerified
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}