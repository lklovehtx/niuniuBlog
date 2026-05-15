<template>
  <div class="verify-container">
    <div class="verify-box">
      <h1 class="title">邮箱验证</h1>
      <p class="subtitle">请输入发送到您邮箱的验证码</p>
      
      <a-form
        :form="form"
        class="verify-form"
        @submit.prevent="handleSubmit"
      >
        <a-form-item>
          <a-input
            v-decorator="[
              'code',
              { rules: [{ required: true, message: '请输入验证码' }] }
            ]"
            size="large"
            placeholder="验证码"
          >
            <a-icon slot="prefix" type="mail" />
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
            验证邮箱
          </a-button>
        </a-form-item>
        <div class="resend-box">
          <span>没有收到验证码？</span>
          <a-button type="link" @click="resendCode" :loading="resending">
            重新发送
          </a-button>
        </div>
      </a-form>
    </div>
  </div>
</template>

<script>
export default {
  name: 'EmailVerify',
  data() {
    return {
      loading: false,
      resending: false,
      form: this.$form.createForm(this)
    }
  },
  methods: {
    async handleSubmit() {
      this.form.validateFields(async (err, values) => {
        if (err) return

        this.loading = true
        try {
          const userId = localStorage.getItem('blog_user_id')
          await this.$store.dispatch('user/verifyEmail', { userId, code: values.code })
          this.$message.success('邮箱验证成功')
          this.$router.push('/blog/home')
        } catch (error) {
          this.$message.error('验证失败：' + (error.message || '请稍后重试'))
        } finally {
          this.loading = false
        }
      })
    },
    async resendCode() {
      this.resending = true
      try {
        const userId = localStorage.getItem('blog_user_id')
        const result = await this.$store.dispatch('user/getVerificationCode', userId)
        this.$message.success('验证码已重新发送: ' + result.verificationCode)
      } catch (error) {
        this.$message.error('发送失败：' + (error.message || '请稍后重试'))
      } finally {
        this.resending = false
      }
    }
  }
}
</script>

<style scoped>
.verify-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.verify-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.title {
  text-align: center;
  margin-bottom: 10px;
  color: #333;
  font-size: 28px;
}

.subtitle {
  text-align: center;
  color: #666;
  margin-bottom: 30px;
}

.verify-form {
  margin-top: 20px;
}

.resend-box {
  text-align: center;
  margin-top: 20px;
  color: #666;
}
</style>