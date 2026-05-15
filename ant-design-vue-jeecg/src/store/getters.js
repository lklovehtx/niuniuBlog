import Vue from 'vue'
import { USER_INFO, ENHANCE_PRE } from "@/store/mutation-types"
const getters = {
  device: state => state.app.device,
  theme: state => state.app.theme,
  color: state => state.app.color,
  token: state => state.user.token,
  avatar: state => {state.user.avatar = Vue.ls.get(USER_INFO).avatar; return state.user.avatar},
  username: state => state.user.info.username,
  nickname: state => {state.user.info.realname = Vue.ls.get(USER_INFO).realname; return state.user.info.realname},
  welcome: state => state.user.welcome,
  permissionList: state => state.user.permissionList,
  userInfo: state => {state.user.info = Vue.ls.get(USER_INFO); return state.user.info},
  addRouters: state => state.permission.addRouters,
  onlAuthFields: state => {return state.online.authFields },
  enhanceJs:(state) => (code) => {
    state.enhance.enhanceJs[code] = Vue.ls.get(ENHANCE_PRE+code);
    return state.enhance.enhanceJs[code]
  },
  sysSafeMode: state => state.user.sysSafeMode,

  //20221108 cfm add
  bizPeriod: state => state.user.bizPeriod,
  //20221218 cfm add
  bizOptions: state => state.user.bizOptions,
  materialList: state => state.user.materialList,
  //20230217 cfm add
  unitList: state => state.user.unitList,
}

export default getters
