<template>
  <a-spin :spinning="confirmLoading">
    <!-- 主表单区域 -->
    <div>
      <a-form-model ref="form" :model="model" :rules="validatorRules">
        <bill-header ref="billHeader" :model="model" :disabled="disabled" :moreStatus.sync="moreStatus" :moreStatus2.sync="moreStatus2"/>

        <a-row>
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="供应商" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="supplierId">
              <j-search-select-tag v-model="model.supplierId" :disabled="disabled" dict="bas_supplier,aux_name,id"/>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24" >
            <a-form-model-item label="业务员" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="operator"ref="operatorFmi">
              <j-select-user-by-dep v-model="model.operator" :multi="false" @change="onOperatorChange" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24" >
            <a-form-model-item label="业务部门" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="opDept" ref="opDeptFmi">
              <a-tooltip :title="model.operator && model.operator.length>0 ? '' : '请先选择业务员！'" placement="bottom">
                 <j-dict-select-tag ref="opDept"  v-model="model.opDept" placeholder="请选择"
                                    :dictCode="`sys_user_dept,depart_name,org_code,username='${model.operator}'`"
                                    :disabled="disabled"/>
              </a-tooltip>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="金额" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="amt">
              <a-input-number v-model="model.amt" :disabled="disabled"
                :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                :parser="value => value.replace(/\$\s?|(,*)/g, '')"
                :precision="2" style="width: 100%"/>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24" v-show="action==='detail'">
            <a-form-model-item label="已核销金额" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="checkedAmt">
              <a-input-number v-model="model.checkedAmt" :disabled="true"
                :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                :parser="value => value.replace(/\$\s?|(,*)/g, '')"
                :precision="2" style="width: 100%"/>
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
  import {BillValidatorMixin} from "../../common/mixins/bill/BillValidatorMixin"
  import BillHeader from "../../common/components/BillHeader";
  import BillApproval from "../../common/components/BillApproval"

  export default {
    name: 'OtherPayableForm',
    mixins: [BillFormMixin, BillFormGridMixin, NoJVxeTableModelMixin, BillValidatorMixin],
    components: {BillHeader, BillApproval},

    data() {
      return {
        model: {//设置初始值的属性、程序赋值的响应式属性
          billNo:'',
          billDate: new Date().format('yyyy-MM-dd'),
          isAuto: 0,
          isRubric: 0,
          srcBillType: '',
          srcBillId: '',
          srcNo: '',
          payableType: '299', //其他应付
          operator: '',
          opDept: '',
          checkedAmt: 0,
        },

        validatorRules: {
          supplierId: [{required: true, message: '请输入供应商!'}],
          amt: [{required: true, message: '请输入金额!'}, {validator: this.rubricFormModelValidator}],
        },
        url: {
          add: "/finance/finPayable/add",
          edit: "/finance/finPayable/edit",
          check: "/finance/finPayable/check",
          ebpm: "/finance/finPayable/bpm/end", //20240815 cfm modi: finReceivable -> finPayable
          execute: "/finance/finPayable/execute", //20240815 cfm modi: finReceivable -> finPayable
          void: "/finance/finPayable/void",
          queryById: "/finance/finPayable/queryById", //20251101 cfm add for 内置BPM
        },
     }
    },

    methods: {
      addAfter() {
        this.$refs.billHeader.fillBillNo('fin_qtap_bill_no');
      },
    }

  }
</script>

<style lang="less" scoped>
  @import "../../common/less/BillForm.less";
</style>
