import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: '/blog/home'
  },
  {
    path: '/blog',
    redirect: '/blog/home'
  },
  {
    path: '/blog/login',
    name: 'BlogLogin',
    component: () => import('@/views/blog/login/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/blog/register',
    name: 'BlogRegister',
    component: () => import('@/views/blog/login/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/blog/verify-email',
    name: 'BlogVerifyEmail',
    component: () => import('@/views/blog/login/EmailVerify.vue'),
    meta: { title: '邮箱验证', requiresAuth: true }
  },
  {
    path: '/blog/home',
    name: 'BlogHome',
    component: () => import('@/views/blog/BlogHome.vue'),
    meta: { title: '博客首页' }
  },
  {
    path: '/blog/article/:id',
    name: 'BlogArticle',
    component: () => import('@/views/blog/ArticleDetail.vue'),
    meta: { title: '文章详情', requiresAuth: false }
  },
  {
    path: '/blog/editor',
    name: 'BlogEditor',
    component: () => import('@/views/blog/modules/ArticleEditor.vue'),
    meta: { title: '写文章', requiresAuth: true }
  },
  {
    path: '/blog/editor/:id',
    name: 'BlogEditorEdit',
    component: () => import('@/views/blog/modules/ArticleEditor.vue'),
    meta: { title: '编辑文章', requiresAuth: true }
  },
  {
    path: '/blog/user/:id',
    name: 'BlogUser',
    component: () => import('@/views/blog/UserCenter.vue'),
    meta: { title: '个人中心', requiresAuth: true }
  },
  {
    path: '/blog/batch-publish',
    name: 'BlogBatchPublish',
    component: () => import('@/views/blog/BatchPublish.vue'),
    meta: { title: '批量发布', requiresAuth: true }
  }
]

const router = new VueRouter({
  mode: 'hash',
  base: process.env.BASE_URL,
  routes
})

router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    document.title = to.meta.title + ' - 博客系统'
  }

  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('blog_token')
    if (!token) {
      next({
        path: '/blog/login',
        query: { redirect: to.fullPath }
      })
      return
    }
    
    // 检查是否需要邮箱验证（写文章需要验证）
    if (to.path === '/blog/editor' || to.path.startsWith('/blog/editor/')) {
      const emailVerified = localStorage.getItem('blog_email_verified') === 'true'
      if (!emailVerified) {
        next({
          path: '/blog/verify-email',
          query: { redirect: to.fullPath }
        })
        return
      }
    }
  }
  next()
})

export default router
