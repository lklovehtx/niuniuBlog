import api from '@/utils/request'

export function sendVerificationCode(email) {
  return api({
    url: '/psi/blog/mail/sendVerificationCode',
    method: 'post',
    data: { email }
  })
}

export function verifyCode(email, code) {
  return api({
    url: '/psi/blog/mail/verifyCode',
    method: 'post',
    data: { email, code }
  })
}