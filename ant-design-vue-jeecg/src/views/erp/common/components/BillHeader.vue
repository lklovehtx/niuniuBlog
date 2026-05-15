<template>
  <a-form-model ref="form" :model="model" :rules="validatorRules">
      <a-row>
        <a-col :xl="8" :lg="12" :md="24">
          <a-form-model-item label="单据编号" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="billNo">
            <a-input v-model="model.billNo" :readOnly="true"/>
          </a-form-model-item>
        </a-col>
        <a-col :xl="8" :lg="12" :md="24">
          <a-row>
            <a-col :span="16">
              <a-form-model-item label="单据日期" :labelCol="{span:9}" :wrapperCol="{span:15}" prop="billDate">
                <!--JDate 清空或键盘删空，即使输入了合法日期，必填校验提示仍一直显示，故设置allowClear=false和inputReadOnly=true-->
                <j-date v-model="model.billDate" :readOnly="disabled" placeholder="请选择日期" style="width: 100%"
                        :allowClear="false" :inputReadOnly="true" />
              </a-form-model-item>
            </a-col>
            <a-col :span="7" :offset="1">
              <a-form-model-item label="阶段" :labelCol="{span:0}" :wrapperCol="{span:24}" prop="billStage">
                <j-dict-select-tag v-model="model.billStage" dictCode="x_bill_stage" :disabled="true"/>
              </a-form-model-item>
            </a-col>
          </a-row>
        </a-col>
        <a-col :xl="8" :lg="12" :md="24">
          <a-row>
            <a-col :md="16" :sm="16" :xs="24" style="margin-top: 8px; margin-bottom: 8px">
              <a-checkbox v-model:checked="model.isVoided===1" style="float: right">已作废</a-checkbox>
              <a-checkbox v-model:checked="model.isClosed===1" style="float: right; margin-right: 6px">已关闭</a-checkbox>
              <a-checkbox v-model:checked="model.isEffective===1" style="float: right; margin-right: 6px">已生效</a-checkbox>
            </a-col>
            <a-col :md="8" :sm="8" :xs="24" style="margin-top: 8px; margin-bottom: 8px">
              <a @click="handleMoreStatus2" style="float: right">
                <a-icon :type="moreStatus2_ ? 'up' : 'down'"/>
              </a>
              <a @click="handleMoreStatus" style="float: right; margin-right: 36px">
                <a-icon :type="moreStatus_ ? 'up' : 'down'"/>
              </a>
            </a-col>
          </a-row>
        </a-col>
      </a-row>
      <a-row v-show="moreStatus_">
        <a-col :xl="8" :lg="12" :md="24">
          <a-form-model-item label="制单时间" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="createTime">
            <j-date v-model="model.createTime" :showTime="true" dateFormat="YYYY-MM-DD HH:mm:ss" :disabled="true" style="width: 100%"/>
          </a-form-model-item>
        </a-col>
        <a-col :xl="8" :lg="12" :md="24">
          <a-form-model-item label="制单部门" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="sysOrgCode">
            <j-dict-select-tag v-model="model.sysOrgCode" dictCode="sys_depart,depart_name,org_code" :disabled="true"/>
          </a-form-model-item>
        </a-col>
        <a-col :xl="8" :lg="12" :md="24">
          <a-form-model-item label="制单人" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="createBy">
            <j-dict-select-tag v-model="model.createBy" dictCode="sys_user,realname,username" :disabled="true"/>
          </a-form-model-item>
        </a-col>
        <a-col :xl="8" :lg="12" :md="24">
          <a-form-model-item label="修改时间" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="updateTime">
            <j-date v-model="model.updateTime" :showTime="true" dateFormat="YYYY-MM-DD HH:mm:ss" :disabled="true" style="width: 100%"/>
          </a-form-model-item>
        </a-col>
        <a-col :xl="8" :lg="12" :md="24">
          <a-form-model-item label="修改人" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="updateBy">
            <j-dict-select-tag v-model="model.updateBy" dictCode="sys_user,realname,username" :disabled="true"/>
          </a-form-model-item>
        </a-col>
        <a-col :xl="4" :lg="6" :md="12" :sm="12" :xs="12">
          <a-form-model-item label="自动单据" :labelCol="labelCol6" :wrapperCol="wrapperCol6" prop="isAuto">
            <j-dict-select-tag v-model="model.isAuto" dictCode="yn" :disabled="true"/>
          </a-form-model-item>
        </a-col>
        <a-col :xl="4" :lg="6" :md="12" :sm="12" :xs="12">
          <a-form-model-item label="红字单据" :labelCol="labelCol6" :wrapperCol="wrapperCol6" prop="isRubric">
            <j-dict-select-tag v-model="model.isRubric" dictCode="yn" :disabled="disabled || isRubricDisabled"/>
          </a-form-model-item>
        </a-col>
        <a-col :xl="8" :lg="12" :md="24">
          <a-form-model-item label="生效时间" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="effectiveTime">
            <j-date v-model="model.effectiveTime" :disabled="true" style="width: 100%"/>
          </a-form-model-item>
        </a-col>
        <a-col :xl="8" :lg="12" :md="24">
          <a-form-model-item label="核批人" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="approver">
            <j-dict-select-tag v-model="model.approver" dictCode="sys_user,realname,username" :disabled="true"/>
          </a-form-model-item>
        </a-col>
        <a-col :xl="8" :lg="12" :md="24">
          <a-form-model-item label="审批实例" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="bpmiInstanceId">
            <a-input v-model="model.bpmiInstanceId" :readOnly="true"/>
          </a-form-model-item>
        </a-col>
        <a-col :xl="8" :lg="12" :md="24">
          <a-form-model-item label="核批意见" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="approvalRemark">
            <a-textarea v-model="model.approvalRemark" :readOnly="true" rows="1" autoSize/>
          </a-form-model-item>
        </a-col>
        <a-col :xl="8" :lg="12" :md="24">
          <a-form-model-item label="核批结果" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="approvalResultType">
            <j-dict-select-tag v-model="model.approvalResultType" dictCode="x_approval_result_type" :disabled="true"/>
          </a-form-model-item>
        </a-col>
      </a-row>
    </a-form-model>
</template>

<script>
  import { BillFormGridMixin } from '../mixins/bill/BillFormGridMixin'
  import { putAction } from '@/api/manage'
  import { mixinDevice } from '@/utils/mixin.js'

  export default {
    name: "BillHeader",
    mixins: [BillFormGridMixin, mixinDevice],
    props: {
      model: {
        type: Object,
        required: true
      },
      disabled: {//表单禁用
        type: Boolean,
        default: false,
      },
      isRubricDisabled: {//isRubric是否禁止修改
        type: Boolean,
        default: true,
      },
      moreStatus: {
        type: Boolean,
        default: false
      },
      moreStatus2: {
        type: Boolean,
        default: false
      }
    },

    data() {
      return {
        moreStatus_: this.moreStatus,
        moreStatus2_: this.moreStatus2,

        validatorRules: {
          billNo: [{required: true, message: '请输入单据编号!'}],
          billDate: [{required: true, message: '请选择单据日期!'}],
        },
      }
    },

    mounted() {
      // this.moreStatus_ = this.moreStatus;
    },

    methods: {
      //生成billNo：在单据的addAfter()中调用
      fillBillNo(ruleCode, callback) {
        // 请求后台的填值规则接口地址
        const url = '/sys/fillRule/executeRuleByCode/';
        putAction(url + ruleCode, {}).then(res => {
          if (res.success) {
            this.model.billNo = res.result;

            if (typeof callback === 'function') callback(this.model.billNo);
          }
        })
      },

      handleMoreStatus() {
        this.moreStatus_ = !this.moreStatus;
        this.$emit("update:moreStatus", this.moreStatus_);
      },

      handleMoreStatus2() {
        this.moreStatus2_ = !this.moreStatus2;
        this.$emit("update:moreStatus2", this.moreStatus2_);
      },
    }

  }
</script>

<style lang="less" scoped>
  @import "../less/BillForm.less";
</style>
