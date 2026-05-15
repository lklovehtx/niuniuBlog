<template>
  <a-spin :spinning="confirmLoading">
    <!-- 主表单区域 -->
    <div>
      <a-form-model ref="form" :model="model" :rules="validatorRules">
        <bill-header ref="billHeader" :model="model" :disabled="disabled" :moreStatus.sync="moreStatus" :moreStatus2.sync="moreStatus2" :isRubricDisabled="true" />
        <a-row v-show="moreStatus">
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="有应付" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="hasRp">
              <j-dict-select-tag v-model="model.hasRp" dictCode="yn" :disabled="true"/>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="有涨吨" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="hasSwell">
              <j-dict-select-tag v-model="model.hasSwell" dictCode="yn" :disabled="true"/>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="源单类型" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="srcBillType">
              <j-dict-select-tag v-model="model.srcBillType" dictCode="x_bill_type"  :disabled="true"/>
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="供应商" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="supplierId" ref="supplierIdFmi">
              <a-tooltip :title="!disabled && entryTable.rowCount>0 ? '有退货明细时不能改变！' : ''" placement="bottom">
                <j-search-select-tag v-model="model.supplierId" dict="bas_supplier,aux_name,id" :disabled="disabled || entryTable.rowCount>0"  @change="val => resetSrc()"/>
              </a-tooltip>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24" >
            <a-form-model-item label="源单业务员" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="operator">
              <a-tooltip :title="!disabled && entryTable.rowCount>0 ? '有退货明细时不能改变！' : ''" placement="bottom">
                <j-select-user-by-dep v-model="model.operator" :multi="false" :disabled="disabled || entryTable.rowCount>0"
                                      @change="val => {this.resetSrc(); this.onOperatorChange(val); }"/>
              </a-tooltip>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24" >
            <a-form-model-item label="业务部门" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="opDept" ref="opDeptFmi">
              <j-dict-select-tag v-if="disabled"  v-model="model.opDept" dictCode="sys_depart,depart_name,org_code" :disabled="true"/>
              <a-tooltip v-else :title="entryTable.rowCount>0 ? '有退货明细时不能改变！' : model.operator && model.operator.length>0 ? '' : '请先选择业务员！'" placement="bottom">
                <j-dict-select-tag ref="opDept"  v-model="model.opDept" placeholder="请选择"
                                   :dictCode="`sys_user_dept,depart_name,org_code,username='${model.operator}'`"
                                   :disabled="entryTable.rowCount>0" @change="val => resetSrc()"/>
              </a-tooltip>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="采购入库单" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="srcNo" ref="srcNoFmi">
              <a-input v-if="disabled" v-model="model.srcNo" :readOnly="true" />
              <a-tooltip v-else :title="entryTable.rowCount>0 ? '有退货明细时不能改变！' : '“业务员、业务部门和供应商”是弹窗查询的参数！'" placement="bottom">
                <j-popup v-model="model.srcNo" :disabled="entryTable.rowCount > 0"
                         field="srcNo" code="stk_in_bill" :param="srcNoPopupParam"
                         org-fields="id,bill_no,supplier_id,operator,op_dept,has_swell,invoice_type"
                         dest-fields="srcBillId,srcNo,supplierId,operator,opDept,hasSwell,invoiceType"
                         @input="onSrcNoPopupInput" />
              </a-tooltip>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="发票类型" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="invoiceType">
              <j-dict-select-tag v-model="model.invoiceType" dictCode="x_invoice_type" :disabled="true"/>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="库管员" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="handler">
              <j-select-user-by-dep v-model="model.handler" :multi="false" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>

          <a-col :xl="8" :lg="12" :md="24" v-if="action==='detail'">
            <a-form-model-item label="已结算金额" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="settledAmt">
              <a-input-number v-model="model.settledAmt" :disabled="true"
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
          <a-tab-pane v-if="srcVisible" tab="采购入库明细" key="srcEntryTable" :forceRender="true">
            <j-vxe-table
              keep-source
              ref="srcEntryTable"
              :loading="srcEntryTable.loading"
              :columns="srcEntryTable.columns"
              :dataSource="srcEntryTable.dataSource"
              :maxHeight="300"
              :disabled="true"
              :rowNumber="false"
              :rowSelection="true"
              :toolbar="!disabled"
              :resizable="true"
              :toolbar-config="{slots: ['prefix', 'suffix'], btn: ['clearSelection']}"
              :checkbox-config="{checkMethod: srcEntryCheckboxMethod}"
              :edit-config="{showIcon: false}"
              @selectRowChange="({selectedRows}) => this.srcEntryTable.selectedRowCount = selectedRows.length">

              <template v-if="!disabled" v-slot:toolbarSuffix>
                <a-button :disabled="srcEntryTable.selectedRowCount===0" @click="handleAddEntryFromSrc">添加<a-icon type="right"/></a-button>
              </template>
            </j-vxe-table>
          </a-tab-pane>

          <a-tab-pane tab="退货出库明细" key="entryTable" :forceRender="true">
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
              @added="event => this.entryTable.rowCount++"
              @selectRowChange="({selectedRows}) => {this.entryTable.selectedRowCount = selectedRows.length;}"
              @remove="event => this.entryTable.rowCount = this.$refs.entryTable.getTableData().length"
              @valueChange="onEntryValueChange"/>

          </a-tab-pane>

          <template slot="tabBarExtraContent">
            <vxe-table-columns-setter v-show="activeKey==='entryTable'"
              :table-key="activeKey + (disabled ? '1':'0')"
              :column-defs="entryTable.columns" :excluded-cols="disabled ? '':entryTable.exKeysWhenEdit" ignored-cols="swellQty"
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
  import {mixinDevice} from '@/utils/mixin'
  import BillHeader from "../../common/components/BillHeader"
  import BillApproval from "../../common/components/BillApproval"
  import VxeTableColumnsSetter from "../../common/components/VxeTableColumnsSetter"

  export default {
    name: 'RubricPurInForm',
    mixins: [JVxeTableModelMixin, BillFormMixin, BillFormGridMixin, DetailMixin, DetailValueMixin, DetailFormatMixin, DetailValidatorMixin, DataMixin, mixinDevice],
    components: {BillHeader, BillApproval, VxeTableColumnsSetter},

    data() {
      return {
        model: {//设置初始值的属性、程序赋值的响应式属性
          billNo:'',
          billDate: new Date().format('yyyy-MM-dd'),
          isAuto: 0,
          isRubric: 1,
          srcBillType:'StkIo:101', //采购入库
          srcBillId:  '',
          stockIoType: '1011', //采购退货出库
          hasRp: 1,
          hasSwell: 0,
          srcNo:'',
          supplierId:'',
          operator: '',
          opDept: '',
          invoiceType: '',
          hasSingle: 0,
        },

        validatorRules: {
          supplierId: [{required: true, message: '请选择供应商!'}],
          srcNo: [{required: true, message: '请选择采购入库单!'}]
        },

        entryNoStep: 10,
        addDefaultRowNum: 0,
        tableKeys: ['entryTable'],//需校验和提交的子表：在getAllTable()用到
        refKeys:   ['entryTable'],//需校验子表所在tab的key
        activeKey: 'entryTable',

        // 采购退货出库明细
        entryTable: {
          loading: false,
          dataSource: [],
          rowCount: 0,
          selectedRowCount: 0,
          url: {list: '/stock/stkIo/queryEntryByMainId', editingList: '/stock/stkIo/queryEditingEntryByMainId'}, //20240923 cfm modi: 增加 editingList
          exKeysWhenEdit: 'invoicedQty,invoicedAmt',
          exKeysWhenDetail: 'materialPopup,inventoryUnitId,inventoryQty,inventoryCost', //20240923 cfm add
          columns: [
            {
              title: '出入方向',
              key: 'stockIoDirection',
              type: JVXETypes.hidden,
              defaultValue: '1',
            },
            {
              title: '单位',
              key: 'unitId',
              type: JVXETypes.select,
              options:[],
              dictCode:"bas_unit,name,id",
              width:"85px",
              align:"center",
              disabled: true,
            },
            {
              title: '结算/入库数量',
              key: 'settleQty',
              type: JVXETypes.inputNumber,
              width:"125px",
              align:"right",
              formatter: this.formatQty,
              placeholder: '请输入',
              defaultValue:'',
              //20240923 cfm modi: 增加 rubricInQtyValidator
              validateRules: [{required: true, message: '${title}不能为空'}, {handler: this.rubricRangeValidator}, {handler: this.rubricInQtyValidator}],
              statistics: ['sum'],
            },
            {
              title: '#',
              key: 'entryNo',
              type: JVXETypes.inputNumber,
              width:"60px",
              align:"center",
              fixed: 'left',
              disabled: true,
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
              disabled: true,
            },
            {
              title: '规格型号',
              key: 'materialModel',
              type: JVXETypes.input,
              width:"160px",
              fixed: 'left',
              defaultValue:'',
              disabled: true,
            },
            {
              title: '仓库',
              key: 'warehouseId',
              type: JVXETypes.select,
              options:[],
              dictCode:"bas_warehouse,aux_name,id",
              width:"150px",
              fixed: 'left',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '批次',
              key: 'batchNo',
              type: JVXETypes.input,
              width:"130px",
              fixed: 'left',
              defaultValue:'',
              disabled: true, //20240923 cfm add
              //20240923 cfm del: validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '税率%',
              key: 'taxRate',
              type: JVXETypes.select,
              dictCode:"x_tax_rate",
              width:"80px",
              align:"center",
              defaultValue:'',
              disabled: true,
            },
            {
              title: '含税单价',
              key: 'price',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue:'',
              disabled: true,
            },
            {
              title: '折扣率%',
              key: 'discountRate',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"center",
              defaultValue:'',
              disabled: true,
            },
            {
              title: '税额',
              key: 'tax',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              placeholder: '请输入',
              defaultValue:'',
              validateRules: [{required: true, message: '${title}不能为空'}, { handler: this.rubricRangeValidator}, {handler: this.settleTaxValidator}],
              statistics: ['sum'],
            },
            {
              title: '结算金额',
              key: 'settleAmt',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              placeholder: '请输入',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }, { handler: this.rubricRangeValidator}, { handler: this.settleAmtValidator}],
              statistics: ['sum'],
            },
            {
              title: '',
              key: 'swellQty',
              type: JVXETypes.hidden,
              defaultValue:0,
            },
            {
              title: '入库数量',
              key: 'qty',
              type: JVXETypes.hidden,
              disabled: true,
            },
            {
              title: '采购费用',
              key: 'expense',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              placeholder: '请输入',
              defaultValue:0,
              validateRules: [{ required: true, message: '${title}不能为空' }],
              statistics: ['sum'],
            },
            {
              title: '入库金额',
              key: 'cost',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue: '',
              disabled: true,
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
              disabled:true,
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
              disabled:true,
            },

            //20240923 cfm add
            {
              title: '库存单位',
              key: 'inventoryUnitId',
              type: JVXETypes.select,
              dictCode:"bas_unit,name,id",
              options:[],
              disabled:true,
              width:"90px",
              align:"center",
            },
            {
              title: '库存数量',
              key: 'inventoryQty',
              type: JVXETypes.input,
              disabled:true,
              width:"120px",
              align:"right",
              formatter: this.formatQty,
            },
            {
              title: '库存金额',
              key: 'inventoryCost',
              type: JVXETypes.inputNumber,
              disabled:true,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
            },

            {
              title: '源单分录号',
              key: 'srcNo',
              type: JVXETypes.input,
              width:"160px",
              defaultValue: '',
              disabled: true,
            },
            {
              title: '条码',
              key: 'barcode',
              type: JVXETypes.input,
              width:"150px",
              sortable: true,
              disabled:true,
            },
            {
              title: '备注',
              key: 'remark',
              type: JVXETypes.input,
              width:"100px",
              defaultValue:'',
            },
            {
              title: '自定义1',
              key: 'custom1',
              type: JVXETypes.input,
              width:"80px",
              defaultValue:'',
            },
            {
              title: '自定义2',
              key: 'custom2',
              type: JVXETypes.input,
              width:"80px",
              defaultValue:'',
            },
          ]
        },

        //原采购入库单
        srcEntryTable: {
          loading: false,
          dataSource: [],
          selectedRowCount: 0,
          url: {list: '/stock/stkIo/queryEntryByMainId'},
          columns: [],
        },

        url: {
          add: "/stock/stkIo/add",
          edit: "/stock/stkIo/edit",
          check: "/stock/stkIo/check",
          ebpm: "/stock/stkIo/bpm/end",
          execute: "/stock/stkIo/execute",
          void: "/stock/stkIo/void",
          queryById: "/stock/stkIo/queryById", //20251101 cfm add for 内置BPM
        },

      }
    },

    watch:{
      'entryTable.dataSource'() {
        this.entryTable.rowCount = this.entryTable.dataSource.length;
      },

      'srcEntryTable.dataSource'() {
        if (this.bizOptions().batchMode === '0') return;

        let arr = this.srcEntryTable.dataSource.map(r => r.batchNo);
        arr = arr.filter((item, index, arr) => arr.indexOf(item, 0) === index); //去重
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
      srcNoPopupParam() {
        const v = {stock_io_type: '101', has_single: 0};
        v.supplier_id = this.model.supplierId;
        v.operator = this.model.operator;
        v.op_dept = this.model.opDept;
        return v;
      },

    },

    created() {
      for(let col of this.entryTable.columns) {
        let col2 = Object.assign({}, col)
        delete col2.validateRules;
        this.srcEntryTable.columns.push(col2);
      }

      this.isCalcOutCost = false; //20240923 cfm add
      this.initBatchNoColumn([this.entryTable, this.srcEntryTable]);
      this.filterColumns([this.entryTable, this.srcEntryTable]);
      this.initColumnsForMobile([this.entryTable, this.srcEntryTable]); // 20240718 cfm modi: 删除 if (this.hasBarcode)
    },

    methods: {
      addBefore(){
        this.entryTable.dataSource=[];
        this.srcEntryTable.dataSource=[];
      },

       addAfter() {
        this.$refs.billHeader.fillBillNo('stk_cgrk_bill_no');
      },

      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key));
        return Promise.all(values);
      },

      editAfter() {
        if (!this.model.id) return;
        //20240923 cfm modi：加上了editingList
        let url = this.disabled ? this.entryTable.url.list : this.entryTable.url.editingList;
        this.requestSubTableData(url, {id: this.model.id}, this.entryTable);
        this.requestSubTableData(this.srcEntryTable.url.list, {id: this.model.srcBillId}, this.srcEntryTable);
      },

       classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
          stkIoEntryList: allValues.tablesValue[0].tableData,
        }
      },

      onSrcNoPopupInput(val, row){
        this.$refs.srcNoFmi.onFieldChange();

        if (this.model.srcBillId === row.srcBillId) return;
        if (!row.srcNo || row.srcNo.length===0) {
          this.resetSrc();
          return;
        }

        //srcBillType 声明时已设置
        this.model.srcBillId = row.srcBillId;
        this.model.supplierId = row.supplierId;
        this.model.invoiceType = row.invoiceType;
        this.model.operator = row.operator;
        this.model.opDept = row.opDept;
        this.model.hasSwell = row.hasSwell;
        this.$refs.supplierIdFmi.onFieldChange();

        // 加载源单分录
        if (!this.hasBarcode) this.activeKey = 'srcEntryTable';
        this.requestSubTableData(this.srcEntryTable.url.list, { id: this.model.srcBillId }, this.srcEntryTable);
      },

      resetSrc() {
        this.model.srcBillId = '';
        this.model.srcNo = '';
        this.model.invoiceType = '';
        this.srcEntryTable.dataSource = [];
        this.activeKey = 'entryTable';
      },

      handleAddEntryFromSrc(){
        for(let row1 of this.$refs.srcEntryTable.selectedRows) {
          const row0 = {};
          row0.barcode = row1.barcode;
          row0.warehouseId = row1.warehouseId;
          row0.batchNo = row1.batchNo;

          this.setEntryFromSrc(row0, row1);

          row0.swellQty = -row1.swellQty;
          row0.tax = -row1.tax;
          row0.settleAmt = -row1.settleAmt;
          row0.expense = -row1.expense;
          row0.cost = -row1.cost;
          this.$refs.entryTable.addRows(row0);
        }
        this.$refs.srcEntryTable.clearSelection();

        //20240923 cfm add
        for(let row of this.$refs.entryTable.getNewDataWithId()) {
          if (!!row.inventoryQty) continue;
          this.setEntryInventory(this.$refs.entryTable, row);
        }

      },

      setEntryFromSrc(entry, src) {
        entry.materialId = src.materialId;
        entry.materialModel = src.materialModel;
        entry.srcBillType = this.model.srcBillType;
        entry.srcBillId = src.mid;
        entry.srcEntryId = src.id;
        entry.entryNo = src.entryNo;
        entry.srcNo = src.billNo + ':' + src.entryNo;
        entry.unitId = src.unitId;
        entry.price = src.price;
        entry.taxRate = src.taxRate;
        entry.discountRate = src.discountRate;
      },

      onEntryValueChange(event) {
        const { type, value, oldValue, row, column, target, isSetValues } = event;
        if (value === oldValue || isSetValues) return;

        let values = {};
        switch (column.property) {
          //20240923 cfm add
          case 'warehouseId':
            this.setEntryInventory(target, row);
            break;

          case "settleQty":
            values = {};
            values.tax = this.calcSettleTax(row);
            values.settleAmt = this.calcSettleAmt(row);
            values.qty = row.settleQty; //假定：无涨吨
            values.cost = Number(values.settleAmt) + Number(row.expense);
            target.setValues([{rowKey: row.id, values: values}]);
            break;
          case "settleAmt":
          case "expense":
            values = {};
            values.cost = Number(row.settleAmt) + Number(row.expense);
            target.setValues([{rowKey: row.id, values: values}]);
            break;
        }
      },

    }
  }
</script>

<style lang="less" scoped>
  @import "../../common/less/BillForm.less";
</style>
