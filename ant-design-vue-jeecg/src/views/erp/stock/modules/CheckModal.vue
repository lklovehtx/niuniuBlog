<template>
  <j-modal
    :title="'库存盘点卡 - ' + title"
    :width="width"
    :visible="visible"
    :maskClosable="false"
    :keyboard="false"
    switchFullscreen
    :okButtonProps="{ class:{'jee-hidden': disableSubmit} }"
    @cancel="handleCancel">

    <template slot="footer">
      <a-tooltip v-if="isMobile()" :title="tip" placement="topLeft">
        <a-icon type="info-circle" style="float: left; font-size: 20px; margin-top: 6px"/>
      </a-tooltip>
      <span v-else style="float: left">{{tip}}</span>

      <a-button v-if="disableSubmit" :disabled="isDisabledAuth('Check:print')" key="print" @click="handlePrint" style="margin-right: 48px">打印</a-button>
      <a-button @click="handleCancel" :type="action==='detail'?'primary':''">{{action==='detail'?'关闭':'取消'}}</a-button>
      <a-button v-if="!disableSubmit" key="save" @click="handleMySave" type="primary" :disabled="loading">保存</a-button>
      <a-button v-if="!disableSubmit && action!=='add'" key="submit" @click="handleMySubmit" type="primary" :disabled="loading">提交</a-button>
      <a-button v-if="action==='check'" key="check" @click="handleCheck" type="primary">审核</a-button>
      <a-button v-if="action==='ebpm'" key="ebpm" @click="handleEbpm" type="primary">结束审批</a-button>
      <a-button v-if="action==='execute'" key="execute" @click="handleExecute" type="primary">执行</a-button>
      <a-popconfirm  title="作废后不可恢复，确定要作废？" @confirm="handleVoid" okText="确定" cancelText="取消">
        <a-button v-if="action==='void'" key="void" type="primary">作废</a-button>
      </a-popconfirm>
    </template>
    <check-form ref="realForm" @ok="submitCallback" :disabled="disableSubmit" :loading.sync="loading"/>
  </j-modal>
</template>

<script>
  import { BillModalMixin } from '../../common/mixins/bill/BillModalMixin'
  import CheckForm from "./CheckForm";
  import { mixinDevice } from '@/utils/mixin.js'

  export default {
    name: 'CheckModal',
    mixins: [BillModalMixin, mixinDevice],
    components: {CheckForm},

    data() {
      return {
        tip: '提示：1、盘点单生效后自动生成盘盈入库单和盘亏出库单,金额为0！ 2、新增盘点卡时不能录入实存数量！ 3、实存数量为空能保存但不能提交！',
      }
    },

    methods: {
      handleMySave() {
        this.$refs.realForm.handleMySave();
      },

      handleMySubmit() {
        this.$refs.realForm.handleMySubmit();
      }

    },

  }
</script>

<style lang="less" scoped>
  .ant-row .ant-form-item {
    margin-bottom: 12px;
  }
</style>
