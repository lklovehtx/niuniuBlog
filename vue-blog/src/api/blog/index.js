import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/psi/blog/user/login',
    method: 'post',
    data
  })
}

export function register(data) {
  return request({
    url: '/psi/blog/user/register',
    method: 'post',
    data
  })
}

export function getUserInfo(id) {
  return request({
    url: `/psi/blog/user/info/${id}`,
    method: 'get'
  })
}

export function updateUserInfo(data) {
  return request({
    url: '/psi/blog/user/update',
    method: 'post',
    data
  })
}

export function getUserStats(id) {
  return request({
    url: `/psi/blog/user/stats/${id}`,
    method: 'get'
  })
}

export function uploadAvatar(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/psi/blog/user/avatar/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function changePassword(data) {
  return request({
    url: '/psi/blog/user/changePassword',
    method: 'put',
    data
  })
}

export function getArticleList(params) {
  return request({
    url: '/psi/blog/article/list',
    method: 'get',
    params
  })
}

export function getArticleDetail(id) {
  return request({
    url: `/psi/blog/article/detail/${id}`,
    method: 'get'
  })
}

export function addArticle(data) {
  return request({
    url: '/psi/blog/article/add',
    method: 'post',
    data
  })
}

export function updateArticle(data) {
  return request({
    url: '/psi/blog/article/edit',
    method: 'put',
    data
  })
}

export function deleteArticle(id) {
  return request({
    url: `/psi/blog/article/${id}`,
    method: 'delete'
  })
}

export function likeArticle(id) {
  return request({
    url: `/psi/blog/article/like/${id}`,
    method: 'post'
  })
}

export function unlikeArticle(id) {
  return request({
    url: `/psi/blog/article/like/${id}`,
    method: 'delete'
  })
}

export function collectArticle(id) {
  return request({
    url: `/psi/blog/article/collect/${id}`,
    method: 'post'
  })
}

export function uncollectArticle(id) {
  return request({
    url: `/psi/blog/article/collect/${id}`,
    method: 'delete'
  })
}

export function getCommentList(articleId, params) {
  return request({
    url: `/psi/blog/comment/list/${articleId}`,
    method: 'get',
    params
  })
}

export function addComment(data) {
  return request({
    url: '/psi/blog/comment/add',
    method: 'post',
    data
  })
}

export function deleteComment(id) {
  return request({
    url: `/psi/blog/comment/${id}`,
    method: 'delete'
  })
}

export function likeComment(id) {
  return request({
    url: `/psi/blog/comment/like/${id}`,
    method: 'post'
  })
}

export function batchPublishArticles(data) {
  return request({
    url: '/psi/blog/article/batch-publish',
    method: 'post',
    data
  })
}
