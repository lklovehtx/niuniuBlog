<template>
  <a-spin :spinning="confirmLoading">
    <div>
    <!-- 主表单区域 -->
      <a-form-model ref="form" :model="model" :rules="validatorRules">
        <bill-header ref="billHeader" :model="model" :disabled="disabled" :moreStatus.sync="moreStatus" :moreStatus2.sync="moreStatus2"/>

        <a-row>
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="供应商" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="supplierId">
              <j-search-select-tag v-model="model.supplierId" dict="bas_supplier,aux_name,id" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="业务员" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="operator" ref="operatorFmi">
              <j-select-user-by-dep v-model="model.operator" :multi="false" @change="onOperatorChange" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24" >
            <a-form-model-item label="业务部门" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="opDept" ref="opDeptFmi">
              <j-dict-select-tag v-if="disabled"  v-model="model.opDept" dictCode="sys_depart,depart_name,org_code" :disabled="true" />
              <a-tooltip v-else :title="model.operator && model.operator.length>0 ? '' : '请先选择业务员！'" placement="bottom">
                <j-dict-select-tag ref="opDept"  v-model="model.opDept" placeholder="请选择"
                                   :dictCode="`sys_user_dept,depart_name,org_code,username='${model.operator}'`"/>
              </a-tooltip>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24" v-show="action==='detail'">
            <a-form-model-item label="已付金额" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="paidAmt">
              <a-input-number v-model="model.paidAmt" :disabled="true"
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

        <!-- 子表单区域 -->
        <a-tabs v-model="activeKey" @change="handleChangeTabs">
          <a-tab-pane tab="明细" key="entryTable" :forceRender="true">
            <j-vxe-table
              keep-source
              ref="entryTable"
              :loading="entryTable.loading"
              :columns="entryTable.columns"
              :dataSource="entryTable.dataSource"
              :maxHeight="300"
              :disabled="disabled"
              :rowNumber="false"
              :rowSelection="!disabled"
              :toolbar="false"
              :resizable="true"
              :edit-config="{trigger: 'click', mode: 'row', showIcon: false}"
              @added="onEntryAdded"
            />
          </a-tab-pane>

          <template slot="tabBarExtraContent">
            <vxe-table-columns-setter v-show="activeKey==='entryTable'"
              :table-key="activeKey + (disabled ? '1':'0')"
              :column-defs="entryTable.columns" :excluded-cols="disabled ? '':entryTable.exKeysWhenEdit"
              style="float: right;"/>
          </template>
        </a-tabs>

        <bill-approval v-if="action==='check' || action==='ebpm'" :model="model" :disabled="disabled" style="margin-top: 16px"/>
      </a-form-model>
    </div>
  </a-spin>
</template>

<script>
  import {JVXETypes} from '@/components/jeecg/JVxeTable'
  import {getRefPromise} from '@/components/jeecg/JVxeTable/utils/vxeUtils.js'
  import {JVxeTableModelMixin} from '@/mixins/JVxeTableModelMixin'
  import {BillFormMixin} from '../../common/mixins/bill/BillFormMixin'
  import {BillFormGridMixin} from '../../common/mixins/bill/BillFormGridMixin'
  import {DetailMixin} from '../../common/mixins/bill/DetailMixin'
  import {DetailValueMixin} from '../../common/mixins/bill/DetailValueMixin'
  import {DetailFormatMixin} from '../../common/mixins/bill/DetailFormatMixin'
  import {DetailValidatorMixin} from '../../common/mixins/bill/DetailValidatorMixin'
  import {DataMixin} from '../../common/mixins/DataMixin'
  import BillHeader from "../../common/components/BillHeader"
  import BillApproval from "../../common/components/BillApproval"
  import VxeTableColumnsSetter from "../../common/components/VxeTableColumnsSetter"

  export default {
    name: 'OtherPrepayment2911ReqForm',
    mixins: [JVxeTableModelMixin, BillFormMixin, BillFormGridMixin, DetailMixin, DetailValueMixin, DetailFormatMixin, DetailValidatorMixin, DataMixin],
    components: {BillHeader, BillApproval, VxeTableColumnsSetter},

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
          paymentType: '2911', //其他预付申请
          amt: 0,
          checkedAmt: 0,
        },

        validatorRules: {
          supplierId: [{required: true, message: '请输入供应商!'}],
        },

        entryNoStep: 10,
        addDefaultRowNum: 1,
        refKeys:  ['entryTable',],
        tableKeys:['entryTable',],
        activeKey: 'entryTable',

        // 明细
        entryTable: {
          loading: false,
          rowCount: 0,
          selectedRowCount:0,
          dataSource: [],
          url: {list: '/finance/finPaymentReq/queryEntryByMainId'},
          exKeysWhenEdit: 'paidAmt',
          columns: [
            {
              title: '#',
              key: 'entryNo',
              type: JVXETypes.inputNumber,
              width:"70px",
              align:"center",
              fixed: 'left',
              sortable: true,
              placeholder: '请输入',
              defaultValue:'',
              validateRules: [
                { required: true, message: '${title}不能为空' },
                { pattern: /^[1-9]\d*$/, message: '${title}须为正整数' },
                { unique: true, message: '${title}不能重复' },
              ],
            },
            {
              title: '申请金额',
              key: 'amt',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue: '',
              validateRules: [{ required: true, message: '${title}不能为空' }, {handler: this.rubricValidator}],
              statistics: ['sum'],
            },
            {
              title: '已付金额',
              key: 'paidAmt',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue:'',
              disabled: true,
              statistics: ['sum'],
            },
            {
              title: '备注',
              key: 'remark',
              type: JVXETypes.input,
              width:"100px",
              defaultValue: '',
            },
            {
              title: '自定义1',
              key: 'custom1',
              type: JVXETypes.input,
              width:"80px",
              defaultValue: '',
            },
            {
              title: '自定义2',
              key: 'custom2',
              type: JVXETypes.input,
              width:"80px",
              defaultValue: '',
            },
          ]
        },

        url: {
          add: "/finance/finPaymentReq/add",
          edit: "/finance/finPaymentReq/edit",
          check: "/finance/finPaymentReq/check",
          ebpm: "/finance/finPaymentReq/bpm/end",
          execute: "/finance/finPaymentReq/execute",
          void: "/finance/finPaymentReq/void",
          queryById: "/finance/finPaymentReq/queryById", //20251101 cfm add for 内置BPM
        },

      }
    },

    created() {
      this.filterColumns();
      this.initColumnsForMobile(); //20240718 cfm add
    },

    methods: {
      addBefore(){
        this.entryTable.dataSource=[]
      },

      addAfter() {
        this.$refs.billHeader.fillBillNo('fin_qtyfsq_bill_no');
      },

      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key))
        return Promise.all(values)
      },

      editAfter() {
        if (this.model.id)
          this.requestSubDatas(this.entryTable);
      },

      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
          finPaymentReqEntryList: allValues.tablesValue[0].tableData,
        }
      },

    }
  }
</script>

<style lang="less" scoped>
  @import "../../common/less/BillForm.less";
</style>