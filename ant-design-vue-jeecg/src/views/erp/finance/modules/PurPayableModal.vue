<template>
  <j-modal
    :title="'采购应付 - ' + title"
    :width="width"
    :visible="visible"
    :maskClosable="false"
    :keyboard="false"
    switchFullscreen
    :okButtonProps="{ class:{'jee-hidden': disableSubmit} }"
    @cancel="handleCancel">

    <template slot="footer">
      <a-button v-if="disableSubmit" :disabled="isDisabledAuth('PurPayable:print')" key="print" @click="handlePrint" style="margin-right: 48px">打印</a-button>
      <a-button @click="handleCancel" :type="action==='detail'?'primary':''">{{action==='detail'?'关闭':'取消'}}</a-button>

      <!-- 20240815 cfm add -->
      <a-button v-if="action==='check'" key="check" @click="handleCheck" type="primary">审核</a-button>
      <a-button v-if="action==='ebpm'" key="ebpm" @click="handleEbpm" type="primary">结束审批</a-button>
      <a-button v-if="action==='execute'" key="execute" @click="handleExecute" type="primary">执行</a-button>
    </template>
    <pur-payable-form ref="realForm" @ok="submitCallback" :disabled="disableSubmit"/>
  </j-modal>
</template>

<script>
  import { BillModalMixin } from '../../common/mixins/bill/BillModalMixin';
  import PurPayableForm from "./PurPayableForm";

  export default {
    name: "PurPayableModal",
    mixins: [ BillModalMixin ],
    components: {PurPayableForm},
  }
</script>
