<template>
  <div>
    <div id="loader-wrapper" style="z-index: 0">
      <div id="loader"></div>
      <div class="loader-section section-left"></div>
      <div class="loader-section section-right"></div>
      <div class="load_title">正在登录，请耐心等待</div>
    </div>
    <binding-user ref="binding" @success="bindingSuccess"/>
    <login-select-tenant ref="loginSelect" @success="loginSuccess"/>
  </div>
</template>

<script>
  import Vue from "vue";
  import { INDEX_MAIN_PAGE_PATH, ACCESS_TOKEN, USER_NAME,USER_INFO, UI_CACHE_DB_DICT_DATA } from "@/store/mutation-types"
  import { timeFix, welcome } from '@/utils/util'
  import {getAction} from "@/api/manage";
  import { isOAuth2AppEnv } from '@/utils/util'
  import BindingUser from "./BindingUser";
  import LoginSelectTenant from "../LoginSelectTenant";

  export default {
    name: 'OAuth2',
    components: {LoginSelectTenant, BindingUser},
    data() {
      return {
        source: '',
      }
    },

    // 20240227 cfm del
    // beforeCreate() {
    //   if (!isOAuth2AppEnv()) {
    //     this.$router.replace({path: '/user/login'})
    //   }
    // },

    mounted() {
      this.checkEnv();

      // 20240229 cfm add: this.doOAuth2()中调用的后端如果异常，则在redirect回本页面(加上“exOAuth2=1”参数)
      const ex = this.getQueryString('exOAuth2');
      if (ex === '1' || !this.source || this.source.length === 0) {
        this.$router.replace({path: '/user/login', query:{exOAuth2:'1'}});
        return;
      }

      //20240227 cfm modi：增加判断后端是否enable或配置该环境
      getAction(`/sys/oauth2/getEnabled/${this.source}`).then(response => {
        if (response.success && response.result) {
          this.login();//不能放created()中，否则$refs.binding等undefined！
        } else {
          this.$router.replace({path: '/user/login', query:{exOAuth2:'1'}});
        }
      })
    },

    methods: {
      getQueryString(name) {
        const reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i');
        const res = window.location.search.substr(1).match(reg);
        return !res ? null : unescape(res[2]);
       },

      /** 检测当前的环境 */
      checkEnv() {
        if (/wxwork/i.test(navigator.userAgent)) {
          //企业微信
          this.source = 'wechat_enterprise';
        } else if (/micromessenger/i.test(navigator.userAgent)) {
          //微信
          this.source = 'wechat_mp'
        } else if (/dingtalk/i.test(navigator.userAgent)) {
          //钉钉
          this.source = 'dingtalk';
        }
      },

      login() {
        if (!this.$route.query.code){
          this.doOAuth2();
        } else if (this.$route.query.code === '200') {
          this.getUserInfo(this.$route.query.token);
        } else if (this.$route.query.code === '206') {
          this.$message.info(this.$route.query.message);
          this.doBinding(this.$route.query.uuid);
        } else {
          this.loginFailed(this.$route.query)
        }
      },

      doOAuth2(){
        //后台构造oauth2授权链接
        let url = `${window._CONFIG['domianURL']}/sys/oauth2/render/${this.source}`;
        url += `?from=${encodeURIComponent(window.location.href)}`;
        window.location.href = url;
      },

      getUserInfo(token){
        getAction(`/sys/oauth2/userinfo/${this.source}/${token}`).then(response => {
          if (response.success) {
            this.localSave(response.result)
            this.selectTenant(response.result)
          } else {
            this.loginFailed(response)
          }
        })
      },

      doBinding(uuid){
        this.$refs.binding.show(this.source, uuid);
      },

      bindingSuccess(response) {
        this.localSave(response.result);
        this.selectTenant(response.result);
      },

      //用户有多租户、多部门时的选择, 参考Login.vue的requestSuccess()
      selectTenant(result) {
        this.$refs.loginSelect.show(result);
      },

      loginSuccess(response) {
        // 登录成功，重定向到主页
        this.$router.replace({path: INDEX_MAIN_PAGE_PATH}).catch(()=>{
          console.log('登录跳转首页出错,这个错误从哪里来的') //Login.vue也会报错
        });
        this.$notification.success({
          message: '欢迎',
          description: `${timeFix()}，欢迎回来`,
        })
      },

      loginFailed(err) {
        const that = this;
        this.$error({
          title: '登录失败',
          content: ((err.response || {}).data || {}).message || err.message || '请求出现错误，请用其他方式登录',
          okText: '其他方式登录',
          onOk() {
            that.$router.replace({path: '/user/login', query:{exOAuth2:'1'}})
          },
          onCancel() {
            that.$router.replace({path: '/user/login', query:{exOAuth2:'1'}})
          },
        })
      },

      //参考：src/store/modules/user.js
      localSave(result){
        const userInfo = result.userInfo;
        Vue.ls.set(ACCESS_TOKEN, result.token, 7 * 24 * 60 * 60 * 1000);
        Vue.ls.set(USER_NAME, userInfo.username, 7 * 24 * 60 * 60 * 1000);
        Vue.ls.set(USER_INFO, userInfo, 7 * 24 * 60 * 60 * 1000)
        Vue.ls.set(UI_CACHE_DB_DICT_DATA, result.sysAllDictItems, 7 * 24 * 60 * 60 * 1000);
        this.$store.commit('SET_TOKEN', result.token);
        this.$store.commit('SET_INFO', userInfo);
        this.$store.commit('SET_NAME', { username: userInfo.username, realname: userInfo.realname, welcome: welcome() });
        this.$store.commit('SET_AVATAR', userInfo.avatar)
      },

    },
  }
</script>

<style scoped>
</style>