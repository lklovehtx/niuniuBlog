<template>
  <a-form-model ref="form" :model="model">
    <a-row>
      <a-col :xl="8" :lg="12" :md="24">
        <a-form-model-item label="核批意见" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="approvalRemark">
          <a-textarea v-model="model.approvalRemark" rows="1" autoSize/>
        </a-form-model-item>
      </a-col>
      <a-col :xl="8" :lg="12" :md="24">
        <a-form-model-item label="核批结果" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="approvalResultType">
          <j-dict-select-tag v-model="model.approvalResultType" dictCode="x_approval_result_type" placeholder="请选择"/>
        </a-form-model-item>
      </a-col>
    </a-row>
  </a-form-model>
</template>

<script>
  import { BillFormGridMixin } from '../mixins/bill/BillFormGridMixin'

  export default {
    name: "BillHeader",
    mixins: [BillFormGridMixin],
    props: {
      model: {
        type: Object,
        required: true
      },
    },

  }
</script>

<style lang="less" scoped>
  @import "../less/BillForm.less";
</style>
