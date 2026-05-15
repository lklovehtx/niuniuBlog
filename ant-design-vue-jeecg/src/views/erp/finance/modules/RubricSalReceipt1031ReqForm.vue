<template>
  <a-spin :spinning="confirmLoading">
    <div>
    <!-- 主表单区域 -->
      <a-form-model ref="form" :model="model" :rules="validatorRules">
        <bill-header ref="billHeader" :model="model" :disabled="disabled" :moreStatus.sync="moreStatus" :moreStatus2.sync="moreStatus2"/>

        <a-row>
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="客户" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="customerId">
              <a-tooltip :title="!disabled && entryTable.rowCount>0 ? '有明细时不能改变！' : ''" placement="bottom">
                <j-search-select-tag v-model="model.customerId" dict="bas_customer,aux_name,id" :disabled="disabled || entryTable.rowCount>0"/>
              </a-tooltip>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="业务员" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="operator" ref="operatorFmi">
              <a-tooltip :title="!disabled && entryTable.rowCount>0 ? '有明细时不能改变！' : ''" placement="bottom">
                <j-select-user-by-dep v-model="model.operator" :multi="false" :disabled="disabled || entryTable.rowCount>0"
                                      @change="onOperatorChange"/>
              </a-tooltip>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="业务部门" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="opDept" ref="opDeptFmi">
              <j-dict-select-tag v-if="disabled"  v-model="model.opDept"
                                 dictCode="sys_depart,depart_name,org_code" :disabled="true"/>
              <a-tooltip v-else :title="entryTable.rowCount>0 ? '有明细时不能改变！' : model.operator && model.operator.length>0 ? '' : '请先选择业务员！'" placement="bottom">
                <j-dict-select-tag ref="opDept"  v-model="model.opDept" placeholder="请选择"
                                   :dictCode="`sys_user_dept,depart_name,org_code,username='${model.operator}'`"
                                   :disabled="entryTable.rowCount>0"/>
              </a-tooltip>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24" v-show="action==='detail'">
            <a-form-model-item label="已收金额" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="receivedAmt">
              <a-input-number v-model="model.receivedAmt" :disabled="true"
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
          <!--20251101 cfm modi for 内置BPM：各源单tab增加 v-if... -->
          <a-tab-pane v-if="srcVisible" tab="销售退货入库单" key="srcTable" :forceRender="true">
            <j-vxe-table
              keep-source
              ref="srcTable"
              :loading="srcTable.loading"
              :columns="srcTable.columns"
              :dataSource="srcTable.dataSource"
              :maxHeight="300"
              :rowNumber="false"
              :rowSelection="false"
              :toolbar="false"
              :resizable="true"
              :edit-config="{enabled: false, showIcon: false}"/>
          </a-tab-pane>

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
              :toolbar="!disabled"
              :resizable="true"
              :toolbar-config="{slots: ['prefix', 'suffix'], btn: ['remove', 'clearSelection']}"
              :edit-config="{trigger: 'click', mode: 'row', showIcon: false}"
              @added="event => {this.entryTable.rowCount++; this.onEntryAdded(event);}"
              @remove="event => {this.entryTable.rowCount = this.$refs.entryTable.getTableData().length; this.removeFreeSrcBills();}"
            >
              <template v-if="!disabled" v-slot:toolbarPrefix>
                <a-tooltip :title="addTip" placement="bottom">
                  <a-button @click="$refs.outPopup.openModal()" icon="plus" type="primary" :disabled="addDisabled">销售退货入库单</a-button>
                </a-tooltip>
                <j-popup v-show="false" ref="outPopup" code="stk_out_bill" :param="outPopupParam"
                         org-fields="id" dest-fields="id" :multi="false" @input="onOutPopupInput"/>
              </template>
            </j-vxe-table>
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
  import {stringIsEmpty} from "../../../erp/common/utils/util";

  export default {
    name: 'RubricSalReceipt1031ReqForm',
    mixins: [JVxeTableModelMixin, BillFormMixin, BillFormGridMixin, DetailMixin, DetailValueMixin, DetailFormatMixin, DetailValidatorMixin, DataMixin],
    components: {BillHeader, BillApproval, VxeTableColumnsSetter},

    data() {
      return {
        model: {//设置初始值的属性、程序赋值的响应式属性
          billNo: '',
          billDate: new Date().format('yyyy-MM-dd'),
          isAuto: 0,
          isRubric: 1,
          srcBillType: '',
          srcBillId: '',
          srcNo: '',
          receiptType: '1031', //销售退货退款
          operator: '',
          opDept: '',
          customerId:'',
        },

        validatorRules: {
          customerId: [{required: true, message: '请输入客户!'}],
        },

        entryNoStep: 10,
        addDefaultRowNum: 0,
        refKeys:  ['entryTable'],
        tableKeys:['entryTable'],
        activeKey: 'entryTable',

        // 明细
        entryTable: {
          loading: false,
          rowCount: 0,
          selectedRowCount:0,
          dataSource: [],
          url: {list: '/finance/finReceiptReq/queryEntryByMainId'},
          exKeysWhenEdit: 'receivedAmt',
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
              title: '源单号',
              key: 'srcNo',
              type: JVXETypes.input,
              width:"200px",
              defaultValue:'',
              fixed: 'left',
              disabled: true,
              sortable: true,
            },
            {
              title: '申请金额',
              key: 'amt',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue: '',
              validateRules: [{ required: true, message: '${title}不能为空' }, {handler: this.rubricValidator}, {handler: this.amtValidator}],
              statistics: ['sum'],
            },
            {
              title: '已收金额',
              key: 'receivedAmt',
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

        // 源单：（红字）销售出库单
        srcTable: {
          loading: false,
          dataSource: [],
          url: {list: '/stock/stkIo/listByIds'},
          columns: [
            {
              title: '单据编号',
              key: 'billNo',
              type: JVXETypes.input,
              width:"160px",
              align:"center",
              fixed: 'left',
              sortable: true,
            },
            {
              title: '单据日期',
              key: 'billDate',
              type: JVXETypes.date,
              width:"100px",
              align:"center",
              sortable: true,
            },
            {
              title: '源单号',
              key: 'srcNo',
              type: JVXETypes.input,
              width:"160px",
              align:"center",
              sortable: true,
            },
            {
              title: '业务部门',
              key: 'opDept',
              type: JVXETypes.select,
              dictCode:"sys_depart,depart_name,org_code",
              options:[],
              width:"120px",
              align:"center",
              sortable: true,
            },
            {
              title: '业务员',
              key: 'operator',
              type: JVXETypes.select,
              dictCode:"sys_user,realname,username",
              options:[],
              width:"90px",
              align:"center",
              sortable: true,
            },
            {
              title: '库管员',
              key: 'handler',
              type: JVXETypes.select,
              dictCode:"sys_user,realname,username",
              options:[],
              width:"100px",
              align:"center",
              sortable: true,
            },
            {
              title: '结算金额',
              key: 'settleAmt',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue:'',
              statistics: ['sum'],
            },
            {
              title: '已结算金额',
              key: 'settledAmt',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue:'',
              statistics: ['sum'],
            },
            {
              title: '单据主题',
              key: 'subject',
              type: JVXETypes.input,
              width:"200px",
              defaultValue:'',
              sortable: true,
            },
            {
              title: '备注',
              key: 'remark',
              type: JVXETypes.input,
              width:"160px",
              defaultValue:'',
            },
          ]
        },

        url: {
          add: "/finance/finReceiptReq/add",
          edit: "/finance/finReceiptReq/edit",
          check: "/finance/finReceiptReq/check",
          ebpm: "/finance/finReceiptReq/bpm/end",
          execute: "/finance/finReceiptReq/execute",
          void: "/finance/finReceiptReq/void",
          queryById: "/finance/finReceiptReq/queryById", //20251101 cfm add for 内置BPM
        },
      }
    },

    watch:{
      'entryTable.dataSource'() {
        this.entryTable.rowCount = this.entryTable.dataSource.length;
      },

      'entryTable.loading': {
        immediate: true,
        handler() {
          this.$emit("update:loading", this.entryTable.loading);
        }
      },

      'entryTable.rowCount': {
        immediate: true,
        handler() {
          this.$emit("update:entryCount", this.entryTable.rowCount);
        }
      }
    },

    computed: {
      addTip() {
        if (stringIsEmpty(this.model.customerId)) return "请先选择客户！";
        if (this.entryTable.rowCount > 0)  return "只能添加一个销售退货入库单！";
        return "业务员、业务部门和客户是弹窗查询参数";
      },

      addDisabled() {
        return stringIsEmpty(this.model.customerId) || this.entryTable.rowCount > 0;
      },

      outPopupParam() {
        const v = {stock_io_type: '2011', is_closed: 0};
        v.customer_id = this.model.customerId;
        v.operator = this.model.operator;
        v.op_dept = this.model.opDept;
        return v;
      },
    },

    created() {
      this.filterColumns();
      this.initColumnsForMobile(); //20240718 cfm add
    },

    methods: {
      addBefore(){
        this.entryTable.dataSource=[];
        this.srcTable.dataSource=[];
      },

      addAfter() {
        this.$refs.billHeader.fillBillNo('fin_xsthtksq_bill_no');
      },

      editAfter() {
        if (this.model.id) this.requestSubDatas(this.entryTable, this.srcTable);
      },

      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key))
        return Promise.all(values)
      },

      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
          finReceiptReqEntryList: allValues.tablesValue[0].tableData,
        }
      },

      onOutPopupInput(val, row) {
        if(stringIsEmpty(row.id)) return;
        const that = this;
        this.requestSrcDeltas(row.id, this.srcTable, null, success);

        function success(deltas) {
          const jxTable = that.$refs.entryTable;
          let entries = jxTable.getTableData();
          for (let srcBill of deltas.srcBills) {
            if (entries.filter(row => row.srcBillId === srcBill.id).length ===0)
              jxTable.addRows({srcBillType: srcBill.billType, srcBillId: srcBill.id, srcNo: srcBill.billNo,
                amt: srcBill.settleAmt-srcBill.settledAmt}).then(
                ({row}) => jxTable.$refs.vxe.$refs.xTable.setActiveCell(row, 'amt'));
          }
        }
      },

      amtValidator({cellValue, row, column}, callback, target) {
        let v = Number(cellValue);
        if (isNaN(v) || stringIsEmpty(row.srcBillId)) {
          callback();
          return;
        }

        let row1 = this.$refs.srcTable.getTableData().find(r => r.id === row.srcBillId);
        if (v < row1.settleAmt - row1.settledAmt) {
          callback(false, '${title}不能超出未结算金额');
        } else {
          callback(true); //true：通过验证
        }
      },

    }
  }
</script>

<style lang="less" scoped>
  @import "../../common/less/BillForm.less";
</style>