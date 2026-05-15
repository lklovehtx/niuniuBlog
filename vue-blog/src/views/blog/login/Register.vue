<template>
  <div class="register-container">
    <div class="register-box">
      <h1 class="title">注册账号</h1>
      <a-form
        :form="form"
        class="register-form"
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
              'nickname',
              { rules: [{ required: true, message: '请输入昵称' }] }
            ]"
            size="large"
            placeholder="昵称"
          >
            <a-icon slot="prefix" type="smile" />
          </a-input>
        </a-form-item>
        <a-form-item>
          <a-input
            v-decorator="[
              'password',
              { rules: [{ required: true, message: '请输入密码' }, { min: 6, message: '密码至少6位' }] }
            ]"
            size="large"
            type="password"
            placeholder="密码"
          >
            <a-icon slot="prefix" type="lock" />
          </a-input>
        </a-form-item>
        <a-form-item>
          <a-input
            v-decorator="[
              'confirmPassword',
              { rules: [{ required: true, message: '请确认密码' }, { validator: compareToFirstPassword }] }
            ]"
            size="large"
            type="password"
            placeholder="确认密码"
          >
            <a-icon slot="prefix" type="lock" />
          </a-input>
        </a-form-item>
        <a-form-item>
          <a-input
            v-decorator="[
              'email',
              { rules: [
                { required: true, message: '请输入邮箱' },
                { type: 'email', message: '请输入正确的邮箱格式' }
              ]}
            ]"
            size="large"
            placeholder="邮箱"
          >
            <a-icon slot="prefix" type="mail" />
          </a-input>
        </a-form-item>

        <a-form-item v-if="!emailVerified">
          <a-input
            v-model="verificationCode"
            size="large"
            placeholder="邮箱验证码"
            style="width: 60%; margin-right: 10px"
          >
            <a-icon slot="prefix" type="safety-certificate" />
          </a-input>
          <a-button
            size="large"
            :loading="sendingCode"
            :disabled="sendCodeDisabled"
            @click="handleSendCode"
            style="width: 38%"
          >
            {{ sendCodeText }}
          </a-button>
        </a-form-item>

        <a-form-item v-if="!emailVerified && verificationCode">
          <a-button
            type="primary"
            size="large"
            block
            :loading="verifyingCode"
            @click="handleVerifyCode"
          >
            验证邮箱
          </a-button>
        </a-form-item>

        <a-form-item v-if="emailVerified">
          <a-alert
            message="邮箱验证成功"
            type="success"
            show-icon
          />
        </a-form-item>

        <a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            size="large"
            block
            :loading="loading"
            :disabled="!emailVerified"
          >
            {{ emailVerified ? '注册' : '请先验证邮箱' }}
          </a-button>
        </a-form-item>
        <div class="register-footer">
          <span>已有账号？</span>
          <router-link to="/blog/login">立即登录</router-link>
        </div>
      </a-form>
    </div>
  </div>
</template>

<script>
import { sendVerificationCode, verifyCode } from '@/api/blog/mail'

export default {
  name: 'BlogRegister',
  data() {
    return {
      loading: false,
      sendingCode: false,
      verifyingCode: false,
      emailVerified: false,
      verificationCode: '',
      sendCodeText: '发送验证码',
      sendCodeDisabled: false,
      countdown: 0,
      form: this.$form.createForm(this)
    }
  },
  methods: {
    compareToFirstPassword(rule, value, callback) {
      const form = this.form
      if (value && value !== form.getFieldValue('password')) {
        callback('两次输入的密码不一致')
      } else {
        callback()
      }
    },
    handleSendCode() {
      this.form.validateFields(['email'], async (err, values) => {
        if (err) return

        this.sendingCode = true
        try {
          const response = await sendVerificationCode(values.email)
          if (response.success) {
            this.$message.success('验证码已发送到您的邮箱')
            this.startCountdown()
          } else {
            this.$message.error(response.message || '发送失败')
          }
        } catch (error) {
          this.$message.error('发送验证码失败：' + (error.message || '请稍后重试'))
        } finally {
          this.sendingCode = false
        }
      })
    },
    handleVerifyCode() {
      this.form.validateFields(['email'], async (err, values) => {
        if (err) return

        if (!this.verificationCode) {
          this.$message.warning('请输入验证码')
          return
        }

        this.verifyingCode = true
        try {
          const response = await verifyCode(values.email, this.verificationCode)
          if (response.success) {
            this.$message.success('邮箱验证成功')
            this.emailVerified = true
            // 保存邮箱到 localStorage，以便后续验证使用
            localStorage.setItem('blog_email', values.email)
          } else {
            this.$message.error(response.message || '验证码错误或已过期')
          }
        } catch (error) {
          this.$message.error('验证失败：' + (error.message || '请稍后重试'))
        } finally {
          this.verifyingCode = false
        }
      })
    },
    startCountdown() {
      this.countdown = 60
      this.sendCodeDisabled = true
      this.sendCodeText = `${this.countdown}秒后重发`

      this.timer = setInterval(() => {
        this.countdown--
        this.sendCodeText = `${this.countdown}秒后重发`

        if (this.countdown <= 0) {
          clearInterval(this.timer)
          this.sendCodeDisabled = false
          this.sendCodeText = '发送验证码'
        }
      }, 1000)
    },
    handleSubmit() {
      if (!this.emailVerified) {
        this.$message.warning('请先验证邮箱')
        return
      }

      this.form.validateFields(async (err, values) => {
        if (err) return

        this.loading = true
        try {
          const result = await this.$store.dispatch('user/register', {
            ...values,
            verificationCode: this.verificationCode
          })

          if (result.success) {
            this.$message.success('注册成功！')
            this.$router.push('/blog/login')
          } else {
            this.$message.error(result.message || '注册失败')
          }
        } catch (error) {
          this.$message.error('注册失败：' + (error.message || '请稍后重试'))
        } finally {
          this.loading = false
        }
      })
    }
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-box {
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

.register-form {
  margin-top: 20px;
}

.register-footer {
  text-align: center;
  margin-top: 20px;
  color: #666;
}

.register-footer a {
  color: #667eea;
  margin-left: 5px;
}
</style>