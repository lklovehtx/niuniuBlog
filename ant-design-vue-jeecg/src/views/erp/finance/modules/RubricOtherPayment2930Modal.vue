<template>
  <j-modal
    :title="'其他付款退款(红字付款) - ' + title"
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

      <a-button v-if="disableSubmit" :disabled="isDisabledAuth('RubricOtherPayment2930:print')" key="print" @click="handlePrint" style="margin-right: 48px">打印</a-button>
      <a-button @click="handleCancel" :type="action==='detail'?'primary':''">{{action==='detail'?'关闭':'取消'}}</a-button>
      <a-button v-if="!disableSubmit" key="save" @click="handleSave" type="primary" :disabled="loading">保存</a-button>
      <a-tooltip :title="entryCount===0 ? '无明细不能提交！' : ''" placement="top">
        <a-button v-if="!disableSubmit" key="submit" @click="handleSubmit" type="primary" :disabled="loading || entryCount===0">提交</a-button>
      </a-tooltip>
      <a-button v-if="action==='check'" key="check" @click="handleCheck" type="primary">审核</a-button>
      <a-button v-if="action==='ebpm'" key="ebpm" @click="handleEbpm" type="primary">结束审批</a-button>
      <a-button v-if="action==='execute'" key="execute" @click="handleExecute" type="primary">执行</a-button>
      <a-popconfirm  title="作废后不可恢复，确定要作废？" @confirm="handleVoid" okText="确定" cancelText="取消">
        <a-button v-if="action==='void'" key="void" type="primary">作废</a-button>
      </a-popconfirm>
    </template>
    <bill-form ref="realForm" @ok="submitCallback" :disabled="disableSubmit" :loading.sync="loading" :entryCount.sync="entryCount"/>
  </j-modal>
</template>

<script>
  import { BillModalMixin } from '../../common/mixins/bill/BillModalMixin';
  import BillForm from "./RubricOtherPayment2930Form";
  import { mixinDevice } from '@/utils/mixin.js'

  export default {
    name: 'RubricOtherPayment2930Modal',
    mixins: [BillModalMixin, mixinDevice],
    components: {BillForm},

    data() {
      return {
        tip: '说明：其他付款退款(红字付款)生效后，可用【应付核销】手动核销采购预付、其他预付、采购付款、其他付款等！',
      }
    },
  }
</script>
