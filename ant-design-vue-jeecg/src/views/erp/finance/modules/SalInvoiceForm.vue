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
            <a-form-model-item label="客户" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="customerId">
              <a-tooltip :title="disabled || entryTable.rowCount===0 ? '' : '有明细时不能改变！'" placement="bottom">
                <j-search-select-tag v-model="model.customerId" dict="bas_customer,aux_name,id" :disabled="disabled || entryTable.rowCount>0"/>
              </a-tooltip>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="发票类型" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="invoiceType">
              <j-dict-select-tag v-model="model.invoiceType" :disabled="disabled" dictCode="x_invoice_type"/>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="发票号" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="invoiceNo">
              <a-input v-model="model.invoiceNo" :readOnly="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="开票日期" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="invoiceDate">
              <j-date v-model="model.invoiceDate" :readOnly="disabled" placeholder="请选择日期" style="width: 100%"
                      :allowClear="false" :inputReadOnly="true" />
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
          <a-tab-pane v-if="srcVisible" tab="销售出库单" key="srcTable" :forceRender="true">
            <j-vxe-table
              keep-source
              ref="srcTable"
              :loading="srcTable.loading"
              :columns="srcTable.columns"
              :dataSource="srcTable.dataSource"
              :maxHeight="300"
              :rowNumber="false"
              :rowSelection="true"
              :toolbar="!disabled"
              :resizable="true"
              :checkbox-config="{checkMethod: srcBillCheckboxMethod}"
              :toolbar-config="{slots: ['prefix', 'suffix'], btn: ['clearSelection']}"
              :edit-config="{enabled: false, showIcon: false}"
              @selectRowChange="({selectedRows}) => this.srcTable.selectedRowCount = selectedRows.length">

              <template v-if="!disabled" v-slot:toolbarPrefix>
                <a-tooltip :title="!model.customerId || model.customerId.length===0 ? '请先选择客户！':'客户是弹窗查询参数'" placement="bottom">
                  <a-button :disabled="!model.customerId || model.customerId.length===0"
                            @click="$refs.outPopup.openModal()" icon="plus">销售出库单</a-button>
                </a-tooltip>
                <a-tooltip v-if="srcTable.selectedRowCount>0" title="明细引用的源单不会被移除！" placement="top">
                  <a-button @click="()=>removeFromSrcTable()" icon="minus">移除</a-button>
                </a-tooltip>

                <j-popup v-show="false" ref="outPopup" code="stk_out_bill" :param="outPopupParam"
                         org-fields="id" dest-fields="id" :multi="true"
                         @input="(val, row) => requestSrcDeltas(row.id)"/>
              </template>
            </j-vxe-table>

            <j-vxe-table
              style="margin-top: 24px"
              keep-source
              ref="srcEntryTable"
              :loading="srcEntryTable.loading"
              :columns="srcEntryTable.columns"
              :dataSource="srcEntryTable.dataSource"
              :maxHeight="300"
              :rowNumber="false"
              :rowSelection="true"
              :toolbar="!disabled"
              :resizable="true"
              :toolbar-config="{slots: ['prefix', 'suffix'], btn: ['clearSelection']}"
              :checkbox-config="{checkMethod: srcEntryCheckboxMethod}"
              :edit-config="{enabled: false, showIcon: false}"
              @selectRowChange="({selectedRows}) => this.srcEntryTable.selectedRowCount = selectedRows.length">

              <template v-if="!disabled" v-slot:toolbarSuffix>
                <a-button :disabled="srcEntryTable.selectedRowCount===0" @click="handleAddEntryFromSrc">添加<a-icon type="right"/></a-button>
              </template>
            </j-vxe-table>
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
              :toolbar-config="{btn: ['remove', 'clearSelection']}"
              :edit-config="{trigger: 'click', mode: 'row', showIcon: false}"
              @edit-actived="({row}) => setMaterialUnitOptions(row, $refs.entryTable)"
              @added="event => {this.entryTable.rowCount++; this.onEntryAdded(event);}"
              @remove="event => this.entryTable.rowCount = this.$refs.entryTable.getTableData().length"
              @valueChange="onEntryValueChange"
            />
          </a-tab-pane>

          <template slot="tabBarExtraContent">
            <vxe-table-columns-setter v-show="activeKey==='entryTable'"
              :table-key="activeKey + (disabled ? '1':'0')"
              :column-defs="entryTable.columns"
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
  import {stringIsEmpty} from "../../common/utils/util";

  export default {
    name: 'SalInvoiceForm',
    mixins: [JVxeTableModelMixin, BillFormMixin, BillFormGridMixin, DetailMixin, DetailValueMixin, DetailFormatMixin, DetailValidatorMixin, DataMixin],
    components: {BillHeader, BillApproval, VxeTableColumnsSetter},

    data() {
      return {
        model: {//设置初始值的属性、程序赋值的响应式属性
          billNo:'',
          billDate: new Date().format('yyyy-MM-dd'),
          isAuto: 0,
          isRubric: 0,
          isReturned: 0,
          srcBillType: 'StkIo:201',
          srcBillId: '',
          srcNo: '',
          invoiceType: '',
        },

        validatorRules: {
          customerId: [{required: true, message: '请选择客户!'}],
          invoiceType: [{required: true, message: '请选择发票类型!'}],
          invoiceNo: [{required: true, message: '请输入发票号!'}],
        },

        entryNoStep: 10,
        addDefaultRowNum: 0,
        refKeys:  ['entryTable', ],
        tableKeys:['entryTable', ],
        activeKey: 'entryTable',

        // 明细：销售发票明细
        entryTable: {
          loading: false,
          dataSource: [],
          rowCount: 0,
          selectedRowCount:0,
          url: {list: '/finance/finSalInvoice/queryEntryByMainId'},
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
              title: '物料',
              key: 'materialId',
              type: JVXETypes.selectSearch,
              dictCode:"bas_material,aux_name,id",
              options:[],
              width:"160px",
              fixed: 'left',
              defaultValue: '',
              disabled: true,
            },
            {
              title: '规格型号',
              key: 'materialModel',
              type: JVXETypes.input,
              width:"160px",
              defaultValue:'',
              disabled: true,
            },
            {
              title: '单位',
              key: 'unitId',
              type: JVXETypes.select,
              dictCode:"bas_unit,name,id",
              width:"90px",
              align:"center",
              placeholder: '请输入',
              defaultValue: '',
              validateRules: [{ required: true, message: '${title}不能为空' }],
              options:[],
            },
            {
              title: '税率%',
              key: 'taxRate',
              type: JVXETypes.selectSearch,
              dictCode:"x_tax_rate",
              options:[],
              width:"80px",
              align:"center",
              defaultValue: '',
            },
            {
              title: '开票数量',
              key: 'qty',
              type: JVXETypes.inputNumber,
              width:"100px",
              align:"right",
              formatter: this.formatQty,
              placeholder: '请输入',
              defaultValue: '',
              validateRules: [{required: true, message: '${title}不能为空'}, { handler: this.rubricValidator }, {handler: this.invoiceQtyValidator}],
              statistics: ['sum'],
            },
            {
              title: '开票金额',
              key: 'amt',
              type: JVXETypes.inputNumber,
              width: "100px",
              align:"right",
              formatter: this.formatAmt,
              placeholder: '请输入',
              defaultValue: '',
              validateRules: [{required: true, message: '${title}不能为空'}, { handler: this.rubricValidator }, {handler: this.invoiceAmtValidator}],
              statistics: ['sum'],
            },
            {
              title: '源单分录号',
              key: 'srcNo',
              type: JVXETypes.input,
              width:"180px",
              align:"center",
              fixed: 'left',
              defaultValue: '',
              disabled: true,
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

        // 源单：出库单
        srcTable: {
          loading: false,
          selectedRowCount: 0,
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
              title: '已开票金额',
              key: 'invoicedAmt',
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

        // 源单明细：出库单明细
        srcEntryTable: {
          loading: false,
          selectedRowCount: 0,
          dataSource: [],
          url: {list: '/stock/stkIo/queryEntryByMainIds'},
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
              title: '#',
              key: 'entryNo',
              type: JVXETypes.inputNumber,
              width:"70px",
              align:"center",
              fixed: 'left',
              sortable: true,
            },
            {
              title: '物料',
              key: 'materialId',
              type: JVXETypes.selectSearch,
              dictCode:"bas_material,aux_name,id",
              options:[],
              width:"160px",
              fixed: 'left',
              sortable: true,
            },
            {
              title: '规格型号',
              key: 'materialModel',
              type: JVXETypes.input,
              width:"160px",
              defaultValue:'',
              disabled: true,
            },
            {
              title: '单位',
              key: 'unitId',
              type: JVXETypes.select,
              dictCode:"bas_unit,name,id",
              width:"90px",
              align:"center",
              defaultValue: '',
              options:[],
            },
            {
              title: '出库数量',
              key: 'qty',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatQty,
              defaultValue: 0,
              statistics: ['sum'],
            },
            {
              title: '出库金额',
              key: 'cost',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue: 0,
              statistics: ['sum'],
            },
            {
              title: '涨吨数量+/-',
              key: 'swellQty',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatQty,
              defaultValue:0,
              statistics: ['sum'],
            },
            {
              title: '结算数量',
              key: 'settleQty',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatQty,
              defaultValue:'',
              statistics: ['sum'],
            },
            {
              title: '税率%',
              key: 'taxRate',
              type: JVXETypes.selectSearch,
              dictCode:"x_tax_rate",
              options:[],
              width:"120px",
              align:"center",
              defaultValue:'',
            },
            {
              title: '含税单价',
              key: 'price',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue:'',
            },
            {
              title: '折扣率%',
              key: 'discountRate',
              type: JVXETypes.input,
              width:"120px",
              align:"center",
            },
            {
              title: '税额',
              key: 'tax',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue:'',
              statistics: ['sum'],
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
              title: '已开票数量',
              key: 'invoicedQty',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatQty,
              defaultValue:'',
              statistics: ['sum'],
            },
            {
              title: '已开票金额',
              key: 'invoicedAmt',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue:'',
              statistics: ['sum'],
            },
            {
              title: '备注',
              key: 'remark',
              type: JVXETypes.input,
              width:"160px",
              defaultValue:'',
            },
            {
              title: '自定义1',
              key: 'custom1',
              type: JVXETypes.input,
              width:"100px",
              defaultValue:'',
            },
            {
              title: '自定义2',
              key: 'custom2',
              type: JVXETypes.input,
              width:"100px",
              defaultValue:'',
            },
          ]
        },

        url: {
          add: "/finance/finSalInvoice/add",
          edit: "/finance/finSalInvoice/edit",
          check: "/finance/finSalInvoice/check",
          ebpm: "/finance/finSalInvoice/bpm/end",
          execute: "/finance/finSalInvoice/execute",
          void: "/finance/finSalInvoice/void",
          queryById: "/finance/finSalInvoice/queryById", //20251101 cfm add for 内置BPM
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
      outPopupParam() {
        const v = {stock_io_type: '201'}; //销售出库单关闭仍可发票登记
        v.customer_id = this.model.customerId;
        return v;
      },
    },

    methods: {
      addBefore(){
        this.entryTable.dataSource=[];
        this.srcEntryTable.dataSource = [];
        this.srcTable.dataSource = [];
     },

      addAfter() {
        this.$refs.billHeader.fillBillNo('fin_xsfp_bill_no');
        this.activeKey = "srcTable";
      },

      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key));
        return Promise.all(values);
      },

      editAfter() {
        if (this.model.id) this.requestSubDatas(this.entryTable, this.srcTable, this.srcEntryTable);
      },

      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
          finSalInvoiceEntryList: allValues.tablesValue[0].tableData,
        }
      },

      handleAddEntryFromSrc(){
        for(let srcRow of this.$refs.srcEntryTable.selectedRows) {
          let row = {};
          row.srcBillType = this.model.srcBillType;
          row.srcBillId = srcRow.mid;
          row.srcEntryId = srcRow.id;
          row.srcNo = srcRow.billNo + ':' + srcRow.entryNo;
          row.materialId = srcRow.materialId;
          row.materialModel = srcRow.materialModel;
          row.unitId = srcRow.unitId;
          row.taxRate = srcRow.taxRate;
          row.qty = srcRow.settleQty - srcRow.invoicedQty;
          row.amt = srcRow.settleAmt - srcRow.invoicedAmt;
          this.$refs.entryTable.addRows(row);
        }
        this.$refs.srcEntryTable.clearSelection();
      },

      onEntryValueChange(event) {
        const { type, value, oldValue, row, column, target, isSetValues } = event;
        if (value === oldValue || isSetValues) return;

        switch (column.property) {
          case "unitId":
            if (stringIsEmpty(oldValue)) break;
            // unitId下列代码限制由非空变为空：因为value空，得到的rate为空，将恢复原值
            const rate = this.getUnitRate(row.materialId, oldValue, value);
            if (!rate) //unitId新值不合法：与原值不能转换，恢复原值
              target.setValues([{rowKey: row.id, values: {unitId: oldValue} }]);
            else
              target.setValues([{ rowKey: row.id, values: {qty: (row.qty * rate).toFixed(3)} }]);
            break;
        }
      },

      invoiceQtyValidator({cellValue, row, column}, callback, target) {
        const qty = Number(cellValue);
        if (isNaN(qty)) {
          callback();
          return;
        }

        let srcRec = this.srcEntryTable.dataSource.find(r => r.id === row.srcEntryId);
        if (!srcRec) {
          callback();
          return;
        }

        let srcSettleQty = Number(srcRec.settleQty);
        let srcInvoicedQty = Number(srcRec.invoicedQty);
        if (isNaN(srcSettleQty) || isNaN(srcInvoicedQty)) {
          callback();
          return;
        }
        const rate =this.getUnitRate(row.materialId, srcRec.unitId, row.unitId);
        if (qty + srcInvoicedQty * rate > srcSettleQty * rate) {
          callback(false, '开票数量不能大于未开票数量！');
        } else{
          callback(true);
        }
      },

      invoiceAmtValidator({cellValue, row, column}, callback, target) {
        const amt = Number(cellValue);
        if (isNaN(amt)) {
          callback();
          return;
        }

        let srcRec = this.srcEntryTable.dataSource.find(r => r.id === row.srcEntryId);
        if (!srcRec) {
          callback();
          return;
        }

        let srcSettleAmt = Number(srcRec.settleAmt);
        let srcInvoicedAmt = Number(srcRec.invoicedAmt);
        if (isNaN(srcSettleAmt) || isNaN(srcInvoicedAmt)) {
          callback();
          return;
        }
        if (amt + srcInvoicedAmt > srcSettleAmt) {
          callback(false, '开票金额不能大于未开票金额！');
        } else{
          callback(true);
        }
      },

    }

  }
</script>

<style lang="less" scoped>
  @import "../../common/less/BillForm.less";
</style>
