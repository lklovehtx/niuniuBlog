<template>
  <div class="login-container">
    <div class="login-box">
      <h1 class="title">博客系统</h1>
      <a-form
        :form="form"
        class="login-form"
        @submit.prevent="handleSubmit"
      >
        <a-form-item>
          <a-input
            v-decorator="[
              'username',
              { rules: [{ required: true, message: '请输入用户名' }] }
            ]"
            size="large"
            placeholder="用户名"
          >
            <a-icon slot="prefix" type="user" />
          </a-input>
        </a-form-item>
        <a-form-item>
          <a-input
            v-decorator="[
              'password',
              { rules: [{ required: true, message: '请输入密码' }] }
            ]"
            size="large"
            type="password"
            placeholder="密码"
          >
            <a-icon slot="prefix" type="lock" />
          </a-input>
        </a-form-item>
        <a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            size="large"
            block
            :loading="loading"
          >
            登录
          </a-button>
        </a-form-item>
        <div class="login-footer">
          <span>还没有账号？</span>
          <router-link to="/blog/register">立即注册</router-link>
        </div>
      </a-form>
    </div>
  </div>
</template>

<script>
export default {
  name: 'BlogLogin',
  data() {
    return {
      loading: false,
      form: this.$form.createForm(this)
    }
  },
  methods: {
    handleSubmit() {
      this.form.validateFields(async (err, values) => {
        if (err) return

        this.loading = true
        try {
          await this.$store.dispatch('user/login', values)
          this.$message.success('登录成功')
          const redirect = this.$route.query.redirect || '/blog/home'
          this.$router.push(redirect)
        } catch (error) {
          this.$message.error('登录失败：' + (error.message || '用户名或密码错误'))
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 28px;
}

.login-form {
  margin-top: 20px;
}

.login-footer {
  text-align: center;
  margin-top: 16px;
}

.login-footer span {
  color: #666;
}

.login-footer a {
  color: #1890ff;
  margin-left: 8px;
}

.login-footer a:hover {
  color: #40a9ff;
}
</style>
