<template>
  <a-spin :spinning="confirmLoading">
    <!-- 主表单区域 -->
    <div>
      <a-form-model ref="form" :model="model" :rules="validatorRules">
        <bill-header ref="billHeader" :model="model" :disabled="disabled" :moreStatus.sync="moreStatus" :moreStatus2.sync="moreStatus2"/>
        <a-row v-show="moreStatus">
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="源单类型" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="srcBillType">
              <j-dict-select-tag v-model="model.srcBillType" dictCode="x_bill_type" :disabled="true"/>
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="供应商" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="supplierId">
              <j-search-select-tag v-model="model.supplierId" dict="bas_supplier,aux_name,id" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24" >
            <a-form-model-item label="业务员" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="operator"ref="operatorFmi">
              <j-select-user-by-dep v-model="model.operator" :multi="false" :disabled="true"/>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24" >
            <a-form-model-item label="业务部门" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="opDept" ref="opDeptFmi">
              <j-dict-select-tag v-model="model.opDept" dictCode="sys_depart,depart_name,org_code" :disabled="true" />
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="源单类型" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="srcBillType">
              <j-dict-select-tag v-model="model.srcBillType" dictCode="x_bill_type" :disabled="true"/>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="金额" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="amt">
              <a-input-number
                v-model="model.amt"
                :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                :parser="value => value.replace(/\$\s?|(,*)/g, '')"
                :precision="2" style="width: 100%" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24" v-show="action==='detail'">
            <a-form-model-item label="已核销金额" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="checkedAmt">
              <a-input-number
                v-model="model.checkedAmt"
                :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                :parser="value => value.replace(/\$\s?|(,*)/g, '')"
                :precision="2" style="width: 100%" :disabled="true"/>
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :xl="8" :lg="12" :md="24" v-show="moreStatus2 || !!model.subject && model.subject.length > 0">
            <a-form-model-item label="单据主题" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="subject">
              <a-input v-model="model.subject" placeholder="请输入" :readOnly="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24" v-show="moreStatus2 || !!model.remark && model.remark.length > 0">
            <a-form-model-item label="备注" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="remark">
              <a-textarea v-model="model.remark" :readOnly="disabled" rows="1" autoSize/>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24" v-show="moreStatus2 || !!model.attachment && model.attachment.length > 0">
            <a-form-model-item label="附件" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="attachment">
              <j-upload v-model="model.attachment" :disabled="disabled" bizPath="erp"/>
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-divider v-if="action==='check' || action==='ebpm'"/>
        <bill-approval v-if="action==='check' || action==='ebpm'" :model="model" :disabled="disabled" style="margin-top: 16px"/>
      </a-form-model>
    </div>

  </a-spin>
</template>

<script>
  import {BillFormMixin} from '../../common/mixins/bill/BillFormMixin'
  import {BillFormGridMixin} from '../../common/mixins/bill/BillFormGridMixin'
  import {NoJVxeTableModelMixin} from "../../common/mixins/NoJVxeTableModelMixin";
  import BillHeader from "../../common/components/BillHeader";
  import BillApproval from "../../common/components/BillApproval"

  export default {
    name: 'PurPayableForm',
    mixins: [BillFormMixin, BillFormGridMixin, NoJVxeTableModelMixin],
    components: {BillHeader, BillApproval},

    data() {
      return {
        validatorRules: {},
        url: {
          //20240815 cfm add
          check: "/finance/finPayable/check",
          ebpm: "/finance/finPayable/bpm/end",
          execute: "/finance/finPayable/execute",
        },
     }
    },

  }
</script>

<style lang="less" scoped>
  @import "../../common/less/BillForm.less";
</style>
