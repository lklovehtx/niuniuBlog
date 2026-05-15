<template>
  <a-modal
    :title="title"
    :width="452"
    :visible="visible"
    :closable="false"
    :maskClosable="false">

    <template slot="footer">
      <a-button type="primary" @click="handleBinding" :loading="loading" :disabled="loading">绑定</a-button>
      <a-button type="link" @click="handleOther">其他方式登录 >></a-button>
    </template>
    <a-form-model ref="form" :model="model" :rules="validatorRules" >
      <a-form-model-item prop="username">
        <a-input v-model="model.username" size="large" placeholder="请输入用户名">
          <a-icon slot="prefix" type="user" :style="{ color: 'rgba(0,0,0,.25)' }"/>
        </a-input>
      </a-form-model-item>
      <a-form-model-item prop="password">
        <a-input v-model="model.password" size="large" type="password" autocomplete="false" placeholder="请输入密码">
          <a-icon slot="prefix" type="lock" :style="{ color: 'rgba(0,0,0,.25)' }"/>
        </a-input>
      </a-form-model-item>
    </a-form-model>
  </a-modal>
</template>

<script>
  import {postAction} from "@/api/manage";

  export default {
    name: 'BindingUser',
    data(){
      return {
        visible: false,
        loading: false,
        source: '',
        uuid: '',
        model:{
          username: '',
          password: '',
        },
        validatorRules:{
          username: [{ required: true, message: '请输入用户名!' }],
          password: [{ required: true, message: '请输入密码!', validator: 'click' }]
        }

      }
    },

    computed:{
      title(){
        let s = ''
        if (this.source==='wechat_mp') {
          s = '微信'
        } else if (this.source==='wechat_enterprise') {
          s = '企业微信'
        } else if (this.source==='dingtalk') {
          s = '钉钉'
        }
        return '绑定' + s
      }
    },

    methods:{
      show(source, uuid){
        this.source = source;
        this.uuid = uuid;
        this.visible = true;
      },

      handleBinding(){
        this.loading = true;
        this.validateFields([ 'username', 'password'], (err)=>{
          if(!err){
            let param = {
              username: this.model.username,
              password: this.model.password,
              uuid: this.uuid
            }
            postAction(`/sys/oauth2/binding/${this.source}`, param).then(response => {
              if (response.success){
                this.visible = false
                this.$emit('success', response)
              } else {
                this.bindingFailed(response)
              }
            }).catch((err) => {
              this.bindingFailed(response)
            });
          }else{
            this.loading = false;
          }
        })
      },

      handleOther(){
        this.$router.replace({path: '/user/login', query:{exOAuth2:'1'}})
      },

      validateFields(arr, callback){
        let promiseArray = []
        for(let item of arr){
          let p = new Promise((resolve, reject) => {
            this.$refs['form'].validateField(item, (err)=>{
              if(!err){
                resolve();
              }else{
                reject(err);
              }
            })
          });
          promiseArray.push(p)
        }
        Promise.all(promiseArray).then(()=>{
          callback()
        }).catch(err=>{
          callback(err)
        })
      },

      bindingFailed (err) {
        this.$notification[ 'error' ]({
          message: '绑定失败',
          description: ((err.response || {}).data || {}).message || err.message || "请求出现错误，请稍后再试",
          duration: 4,
        });
        this.loading = false;
      },

    }

  }
</script>

<style scoped>
  .other-login {
    font-size: 14px;
  }

</style>