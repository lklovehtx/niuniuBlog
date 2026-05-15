import api from '@/utils/request'

export function register(data) {
  return api({
    url: '/psi/blog/user/register',
    method: 'post',
    data
  })
}

export function login(data) {
  return api({
    url: '/psi/blog/user/login',
    method: 'post',
    data
  })
}