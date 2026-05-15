<template>
  <j-modal
    :title="'BOM - ' + title"
    :width="width"
    :visible="visible"
    :maskClosable="false"
    switchFullscreen
    @ok="handleOk"
    :okButtonProps="{ class:{'jee-hidden': disableSubmit} }"
    @cancel="myHandleCancel">

    <template slot="footer">
      <a-button v-if="!disableSubmit" @click="handleOk" type="primary" style="margin-bottom: 0;">确定</a-button>
      <a-button @click="myHandleCancel" style="margin-bottom: 0;">{{disableSubmit ? '关闭':'取消'}}</a-button>
    </template>

    <bas-bom-form ref="realForm" @ok="submitCallback" :disabled="disableSubmit"/>
  </j-modal>
</template>

<script>
  import BasBomForm from './BasBomForm'
  import { BillModalMixin } from '../../common/mixins/bill/BillModalMixin'

  export default {
    name: 'BasBomModal',
    components: {BasBomForm},
    mixins: [BillModalMixin],

    methods: {
      //20240815 cfm add；template中的相应的对应的2处属性也修改。
      myHandleCancel() {
        this.$emit('close');
        this.visible = false;
      },
    }
  }
</script>

<style scoped>
</style>