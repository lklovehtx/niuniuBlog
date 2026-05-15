<template>
  <a-spin :spinning="confirmLoading">
    <!-- 主表单区域 -->
    <div>
      <a-form-model ref="form" :model="model" :rules="validatorRules">
        <bill-header ref="billHeader" :model="model" :disabled="disabled" :moreStatus.sync="moreStatus" :moreStatus2.sync="moreStatus2" :isRubricDisabled="true" />
        <a-row v-show="moreStatus">
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="有应收" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="hasRp">
              <j-dict-select-tag v-model="model.hasRp" dictCode="yn" :disabled="true"/>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24">
            <a-tooltip title="销售出库：出库数量+涨吨数量=结算数量" placement="top">
              <a-form-model-item label="有涨吨" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="hasSwell">
                <j-dict-select-tag v-model="model.hasSwell" dictCode="yn" :disabled="true"/>
              </a-form-model-item>
            </a-tooltip>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="源单类型" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="srcBillType">
              <j-dict-select-tag v-model="model.srcBillType" dictCode="x_bill_type" :disabled="true"/>
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :xl="8" :lg="12" :md="24" >
            <a-form-model-item label="客户" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="customerId" ref="customerIdFmi">
              <a-tooltip :title="!disabled && entryTable.rowCount>0 ? '有明细时不能改变！' : ''" placement="bottom">
                <j-search-select-tag v-model="model.customerId" dict="bas_customer,aux_name,id" :disabled="disabled || entryTable.rowCount>0" @change="val => resetSrc()"/>
              </a-tooltip>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24" >
            <a-form-model-item label="业务员" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="operator" ref="operatorFmi">
              <a-tooltip :title="!disabled && entryTable.rowCount>0 ? '有明细时不能改变！' : ''" placement="bottom">
                <j-select-user-by-dep v-model="model.operator" :multi="false" :disabled="disabled || entryTable.rowCount>0"
                                      @change="val =>{this.resetSrc(); this.onOperatorChange(val); }"/>
              </a-tooltip>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24" >
            <a-form-model-item label="业务部门" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="opDept" ref="opDeptFmi">
              <j-dict-select-tag v-if="disabled"  v-model="model.opDept" dictCode="sys_depart,depart_name,org_code" :disabled="true"/>
              <a-tooltip v-else :title="entryTable.rowCount>0 ? '有明细时不能改变！' : model.operator && model.operator.length>0 ? '' : '请先选择业务员！'" placement="bottom">
                <j-dict-select-tag ref="opDept"  v-model="model.opDept" placeholder="请选择"
                                   :dictCode="`sys_user_dept,depart_name,org_code,username='${model.operator}'`"
                                   :disabled="entryTable.rowCount>0" @change="val => resetSrc()"/>
              </a-tooltip>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="销售订单" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="srcNo" ref="srcNoFmi">
              <a-input v-if="disabled" v-model="model.srcNo" :readOnly="true"/>
              <a-tooltip v-else :title="entryTable.rowCount>0 ? '有明细时不能改变！' : '业务员、业务部门和客户是弹窗查询参数'" placement="bottom">
                <j-popup v-model="model.srcNo" :disabled="entryTable.rowCount > 0"
                         field="srcNo" code="sal_order" :param="srcNoPopupParam"
                         org-fields="id,bill_no,customer_id,op_dept,operator,invoice_type"
                         dest-fields="srcBillId,srcNo,customerId,opDept,operator,invoiceType"
                         @input="onSrcNoPopupInput" />
              </a-tooltip>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="发票类型" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="invoiceType">
              <j-dict-select-tag v-model="model.invoiceType" dictCode="x_invoice_type" :disabled="!!model.srcNo && model.srcNo.length > 0"/>
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
          <a-tab-pane v-if="srcVisible" tab="销售订单明细" key="srcEntryTable" :forceRender="true">
            <j-vxe-table
              keep-source
              ref="srcEntryTable"
              :loading="srcEntryTable.loading"
              :columns="srcEntryTable.columns"
              :dataSource="srcEntryTable.dataSource"
              :maxHeight="300"
              :rowNumber="false"
              :rowSelection="!disabled"
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
              :toolbar-config="{slots: ['prefix', 'suffix'], btn: ['remove','clearSelection']}"
              :edit-config="{trigger: 'click', mode: 'row', showIcon: false}"
              @edit-actived="({row}) => {setMaterialUnitOptions(row); if (this.bizOptions().batchMode==='0') setWarehouseOptions(row);}"
              @added="event => {this.entryTable.rowCount++; this.onEntryAdded(event);}"
              @selectRowChange="({selectedRows}) => {this.entryTable.selectedRowCount = selectedRows.length;}"
              @remove="event => this.entryTable.rowCount = this.$refs.entryTable.getTableData().length"
              @valueChange="onEntryValueChange">

              <template v-if="!disabled" v-slot:materialPopup="props">
                <vxe-column-popup :props="props" @valuesChange="onMaterialValuesChange"/>
              </template>

              <template v-if="!disabled" v-slot:toolbarPrefix>
                <a-tooltip :title="addTip" placement="bottom">
                  <a-button :disabled="addDisabled" type="primary" icon="plus" @click="() => $refs.entryTable.addRows({})">新增</a-button>
                </a-tooltip>
                <a-tooltip v-if="!isMobile()" :title="entryTable.selectedRowCount!==1 ? '请选择一行明细!':''" placement="bottom">
                  <a-button @click="handleCopyAndAdd" icon="plus" :disabled="entryTable.selectedRowCount!==1">复制新增</a-button>
                </a-tooltip>
              </template>

              <!-- 20241106 cfm modi: isMobile() 改为 !isMobile() -->
              <template v-if="!disabled && !isMobile()" v-slot:toolbarSuffix>
                <p v-if="bizOptions().batchMode==='0'" style="float: right;">提示：明细录入时，先顺序录入物料、仓库！ 仓库只能选择有库存的！</p>
                <p v-else style="float: right;">提示：明细录入时，“物料、仓库”是“批次”查询的参数！</p>
              </template>
            </j-vxe-table>
          </a-tab-pane>

          <template slot="tabBarExtraContent">
            <vxe-table-columns-setter v-show="activeKey==='entryTable'"
              :table-key="activeKey + (disabled ? '1':'0')"
              :column-defs="entryTable.columns"
              :excluded-cols="disabled ? entryTable.exKeysWhenDetail:entryTable.exKeysWhenEdit"
              ignored-cols="swellQty" style="float: right;"/>
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
  import VxeColumnPopup from "../../common/components/VxeColumnPopup"
  import {stringIsEmpty} from "../../common/utils/util";
  import pick from "lodash.pick";

  export default {
    name: 'SalOutForm',
    mixins: [JVxeTableModelMixin, BillFormMixin, BillFormGridMixin, DetailMixin, DetailValueMixin, DetailFormatMixin, DetailValidatorMixin, DataMixin, mixinDevice],
    components: {BillHeader, BillApproval, VxeTableColumnsSetter, VxeColumnPopup},

    data() {
      return {
        model: {//设置初始值的属性、程序赋值的响应式属性
          billNo: '',
          billDate: new Date().format('yyyy-MM-dd'),
          isAuto: 0,
          isRubric: 0,
          srcBillType: '',
          srcBillId:  '',
          srcNo:'',
          stockIoType: '201', //销售出库
          hasRp: 1,
          hasSwell: 0,
          customerId: '',
          operator: '',
          opDept: '',
          invoiceType: '',
          hasSingle: 0,
        },

        validatorRules: {
          customerId: [{required: true, message: '请选择客户!'}],
          invoiceType: [{required: true, message: '请选择发票类型！'}]
        },

        entryNoStep: 10,
        addDefaultRowNum: 0,
        tableKeys: ['entryTable'],//需校验和提交的子表：在getAllTable()用到
        refKeys:   ['entryTable'],//需校验子表所在tab的key
        activeKey: 'entryTable',

        // 明细
        entryTable: {
          loading: false,
          dataSource: [],
          rowCount: 0,
          selectedRowCount: 0,
          url: {list: '/stock/stkIo/queryEntryByMainId', editingList: '/stock/stkIo/queryEditingEntryByMainId'},
          exKeysWhenEdit: 'invoicedQty,invoicedAmt',
          exKeysWhenDetail: 'materialPopup,inventoryUnitId,inventoryQty,inventoryCost',
          columns: [
            {
              title: '出入方向',
              key: 'stockIoDirection',
              type: JVXETypes.hidden,
              defaultValue: '2',
            },
            {
              title: '单位',
              key: 'unitId',
              type: JVXETypes.select,
              dictCode:"bas_unit,name,id",
              options:[],
              width:"85px",
              align:"center",
              placeholder: '请选择',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '出库/结算数量',
              key: 'qty',
              type: JVXETypes.inputNumber,
              width:"125px",
              align:"right",
              formatter: this.formatQty,
              placeholder: '请输入',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }, {handler: this.rubricValidator}, {handler: this.outQtyValidator}],
              statistics: ['sum'],
            },
            {
              title: '#',
              key: 'entryNo',
              type: JVXETypes.inputNumber,
              width:"60px",
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
              width:"150px",
              fixed: 'left',
              placeholder: '请选择',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '',
              key: 'materialPopup',
              type: JVXETypes.slot,
              slotName:"materialPopup",
              width:"40px",
              fixed: 'left',
              popupCode: 'bas_material',
              orgFields: "id",
              destFields: "materialId",
              paramFields: "",
              param: {},
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
              type: JVXETypes.selectSearch,
              options:[],
              dictCode:"bas_warehouse,aux_name,id",
              width:"180px",
              fixed: 'left',
              placeholder: '请选择',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '批次',
              key: 'batchNo',
              type: JVXETypes.popup,
              popupCode: 'stk_inventory_batch',
              orgFields: "material_id,barcode,material_model,warehouse_id,batch_no,unit_id,qty,cost",
              destFields: "materialId,barcode,materialModel,warehouseId,batchNo,inventoryUnitId,inventoryQty,inventoryCost",
              paramFields: "materialId,warehouseId",
              field: 'batchNo',
              width:"160px",
              fixed: 'left',
              placeholder: '请选择',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '出库金额',
              key: 'cost',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue: '',
              disabled: true,
              validateRules: [{ required: true, message: '${title}不能为空（修改出库数量重新计算本值）' }],
              statistics: ['sum'],
            },
            {
              title: '',
              key: 'swellQty',
              type: JVXETypes.hidden,
              defaultValue:0,
            },
            {
              title: '结算数量',
              key: 'settleQty',
              type: JVXETypes.hidden,
              width:"100px",
              align:"right",
              formatter: this.formatQty,
              defaultValue:'',
              disabled: true,
              validateRules: [{ required: true, message: '${title}不能为空（修改出库数量重新计算本值）' }],
            },
            {
              title: '税率%',
              key: 'taxRate',
              type: JVXETypes.selectSearch,
              dictCode:"x_tax_rate",
              width:"80px",
              align:"center",
              placeholder: '请选择',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '含税单价',
              key: 'price',
              type: JVXETypes.inputNumber,
              width:"100px",
              align:"right",
              formatter: this.formatAmt,
              placeholder: '请输入',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '折扣率%',
              key: 'discountRate',
              type: JVXETypes.inputNumber,
              width:"90px",
              align:"center",
              placeholder: '请输入',
              defaultValue:100,
              validateRules: [{required: true, message: '${title}不能为空'}, {pattern: /^[1-9]\d*$/, message: '${title}须为正整数'}],
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
              validateRules: [{required: true, message: '${title}不能为空'}, {handler: this.settleTaxValidator}],
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
              validateRules: [{ required: true, message: '${title}不能为空' }, {handler: this.settleAmtValidator}],
              statistics: ['sum'],
            },
            {
              title: '已开票数量',
              key: 'invoicedQty',
              type: JVXETypes.inputNumber,
              disabled:true,
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
              disabled:true,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue:'',
              statistics: ['sum'],
            },
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
              statistics: ['sum'],
            },
            {
              title: '库存金额',
              key: 'inventoryCost',
              type: JVXETypes.inputNumber,
              disabled:true,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              statistics: ['sum'],
            },
            {
              title: '源单分录号',
              key: 'srcNo',
              type: JVXETypes.input,
              width:"160px",
              defaultValue: '',
              disabled:true,
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

        //源单明细：销售订单明细
        srcEntryTable: {
          loading: false,
          dataSource: [],
          selectedRowCount: 0,
          url: {list: '/sale/salOrder/queryEntryByMainId'},
          columns: [
            {
              title: '#',
              key: 'entryNo',
              type: JVXETypes.inputNumber,
              width:"70px",
              align:"center",
              fixed: 'left',
              sortable: true,
              defaultValue:'',
            },
            {
              title: '物料',
              key: 'materialId',
              type: JVXETypes.selectSearch,
              dictCode:"bas_material,aux_name,id",
              width:"160px",
              fixed: 'left',
              defaultValue: '',
              sortable: true,
            },
            {
              title: '规格型号',
              key: 'materialModel',
              type: JVXETypes.input,
              width:"160px",
              defaultValue:'',
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
              title: '数量',
              key: 'qty',
              type: JVXETypes.inputNumber,
              width:"100px",
              align:"right",
              formatter: this.formatQty,
              defaultValue: '',
              statistics: ['sum'],
            },
            {
              title: '税率%',
              key: 'taxRate',
              type: JVXETypes.inputNumber,
              width:"80px",
              align:"center",
              defaultValue: '',
            },
            {
              title: '含税单价',
              key: 'price',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue: '',
            },
            {
              title: '折扣率%',
              key: 'discountRate',
              type: JVXETypes.inputNumber,
              width:"90px",
              align:"center",
              defaultValue:100,
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
              title: '含税金额',
              key: 'amt',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue: '',
              statistics: ['sum'],
            },
            {
              title: '已出库数量',
              key: 'outQty',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatQty,
              defaultValue:'',
              statistics: ['sum'],
            },
            {
              title: '已出库金额',
              key: 'outCost',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue:'',
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
              title: '源单类型',
              key: 'srcBillType',
              type: JVXETypes.select,
              dictCode:"x_bill_type",
              width:"120px",
              defaultValue:'',
              sortable: true,
            },
            {
              title: '源单分录号',
              key: 'srcNo',
              type: JVXETypes.input,
              width:"160px",
              defaultValue:'',
              sortable: true,
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
        const v = {is_closed: 0};
        v.customer_id = this.model.customerId;
        v.operator = this.model.operator;
        v.op_dept = this.model.opDept;
        return v;
      },

      addTip() {
        if (stringIsEmpty(this.model.customerId)) return "请先选择客户！";
        if (!stringIsEmpty(this.model.srcNo)) return "请从订单明细中选择添加！";
        return "";
      },

      addDisabled() {
        return stringIsEmpty(this.model.customerId) || !stringIsEmpty(this.model.srcNo);
      },

    },

    created() {
      this.initBatchNoColumn();
      this.filterColumns([this.entryTable, this.srcEntryTable]);
      this.initColumnsForMobile();
    },

    methods: {
      addBefore(){
        this.entryTable.dataSource=[];
        this.srcEntryTable.dataSource=[];
      },

      addAfter() {
        this.$refs.billHeader.fillBillNo('stk_xsck_bill_no');
      },

      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key))
        return Promise.all(values)
      },

      editAfter() {
        if (!this.model.id) return;

        let url = this.disabled ? this.entryTable.url.list : this.entryTable.url.editingList;
        this.requestSubTableData(url, {id: this.model.id}, this.entryTable);
        if (!stringIsEmpty(this.model.srcBillId)) this.requestSubTableData(this.srcEntryTable.url.list, {id: this.model.srcBillId}, this.srcEntryTable);
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
        if (stringIsEmpty(row.srcNo)) {
          this.resetSrc();
          return;
        }

        this.model.srcBillType = 'SalOrder';
        this.model.srcBillId = row.srcBillId;
        this.model.operator = row.operator;
        this.model.opDept = row.opDept;
        this.model.customerId = row.customerId;
        this.model.invoiceType = row.invoiceType;
        this.$refs.customerIdFmi.onFieldChange();

        // 加载源单分录
        if (!this.hasBarcode) this.activeKey = 'srcEntryTable';
        this.requestSubTableData(this.srcEntryTable.url.list, {id: row.srcBillId}, this.srcEntryTable)
      },

      resetSrc() {
        this.model.srcBillType = '';
        this.model.srcBillId = '';
        this.model.srcNo = '';
        this.model.invoiceType = '';
        this.srcEntryTable.dataSource = [];
        this.activeKey = 'entryTable';
      },

      handleAddEntryFromSrc(){
        for(let srcRow of this.$refs.srcEntryTable.selectedRows) {
          const row = {}
          row.barcode = srcRow.barcode;
          row.qty = srcRow.qty - srcRow.settleQty;
          row.settleQty = row.qty;
          this.setEntryFromSrc(row, srcRow);
          this.$refs.entryTable.addRows(row);
        }
        this.$refs.srcEntryTable.clearSelection();
      },

      setEntryFromSrc(entry, src) {
        entry.materialId = src.materialId;
        entry.barcode = src.barcode;
        entry.materialModel = src.materialModel;
        entry.srcBillType = this.model.srcBillType;
        entry.srcBillId = src.mid;
        entry.srcEntryId = src.id;
        entry.srcNo = src.billNo + ':' + src.entryNo;
        entry.unitId = src.unitId;
        entry.price = src.price;
        entry.taxRate = src.taxRate;
        entry.discountRate = src.discountRate;
        entry.swellQty = 0;
        entry.tax = this.calcSettleTax(entry);
        entry.settleAmt = this.calcSettleAmt(entry);
      },

      handleCopyAndAdd() {
        let jxTable = this.$refs.entryTable;
        let row = pick(jxTable.selectedRows[0], 'srcBillType','srcBillId','srcEntryId','srcNo',
          'materialId','barcode','materialModel','unitId','taxRate','price','discountRate',
          'remark','custom1','custom2');
        jxTable.addRows(row);
      },

      onEntryValueChange(event) {
        const { type, value, oldValue, row, column, target, isSetValues } = event;
        // 库存批次batchNo相同，但inventoryId可能不同（不同仓库、不同物料可能同batchNo）
        if (value === oldValue && column.property !== 'batchNo' || isSetValues ) return;

        //如果有源单，则不清除源单带过来的数据
        let emptyKeys = stringIsEmpty(row.srcNo) ? 'unitId,qty,cost,price,swellQty,settleQty,tax,settleAmt' : 'cost';
        emptyKeys += ',inventoryUnitId,inventoryQty,inventoryCost';
        if (this.bizOptions().batchMode !== '0') emptyKeys += ',batchNo';
        let values = {};
        switch (column.property) {
          case 'materialId':
            this.onMaterialValuesChange({values: {materialId: value}, oldValues: {materialId: oldValue}, row: row, target: target});
            break;
          case 'warehouseId':
             if (this.bizOptions().batchMode === '0')
              this.handleWarehouseChange(row, target, emptyKeys)
            else
              this.emptyColumns(row, emptyKeys, target);
             break;
          case 'batchNo':
            if (stringIsEmpty(row.batchNo)) {
              this.emptyColumns(row, emptyKeys, target);
              break;
            }

            this.setMaterialUnitOptions(row);//batchNo通过popup选择，会导致materialId改变，需要重新设置单位的下拉选项
            if (stringIsEmpty(row.unitId)) {
              values.unitId = row.inventoryUnitId;
              values.cost = this.calcOutCost(row, {unitId: values.unitId});
            }
            else {
              values.cost = this.calcOutCost(row);
              //batchNo改变带出的inventoryUnitId变化，可能会导致与unitId不能转
              if (values.cost === null) values.unitId = ''; //注意：不能!values.cost，因为!0 = true
            }
            target.setValues([{rowKey: row.id, values: values}]);
            break;
          case 'unitId':
            if (stringIsEmpty(oldValue)) break;
            // unitId下列代码限制由非空变为空：因为value空，得到的rate为空，将恢复原值
            const rate = this.getUnitRate(row.materialId, oldValue, value);
            if (!rate) //unitId新值不合法：与原值不能转换，恢复原值
              target.setValues([{rowKey: row.id, values: {unitId: oldValue} }]);
            else {
              values = {};
              values.qty = (row.qty * rate).toFixed(3);
              values.settleQty = Number(values.qty); //假定：无涨吨
              values.price = (row.price / rate).toFixed(2);
              target.setValues([{rowKey: row.id, values: values}]);
            }
            break;
          case 'qty':
            values = {};
            values.cost = this.calcOutCost(row);
            values.settleQty = Number(row.qty); //假定：无涨吨
            values.tax = this.calcSettleTax(row, {settleQty: values.settleQty});
            values.settleAmt = this.calcSettleAmt(row, {settleQty: values.settleQty});
            target.setValues([{rowKey: row.id, values: values}]);
            break;
          case "price":
          case "discountRate":
            values = {};
            values.tax = this.calcSettleTax(row);
            values.settleAmt = this.calcSettleAmt(row);
            target.setValues([{rowKey: row.id, values: values}]);
            break;
          case "taxRate":
            target.setValues([{rowKey: row.id, values: {tax: this.calcSettleTax(row)}}]);
            break;
        }
      },

      onMaterialValuesChange(event) {
        const {values, oldValues, row, target} = event;
        if (values.materialId === oldValues.materialId) return;

        if (!stringIsEmpty(row.srcNo)) {
          this.$warning({title: '明细', content: '有“源单分录号”不能改变物料！'});
          target.setValues([{rowKey: row.id, values: oldValues}]);
          return;
        }

        //如果有源单，则不清除源单带过来的数据
        let emptyKeys = stringIsEmpty(row.srcNo) ? 'unitId,qty,cost,price,swellQty,settleQty,tax,settleAmt' : 'cost';
        emptyKeys += ',warehouseId,inventoryUnitId,inventoryQty,inventoryCost';
        if (this.bizOptions().batchMode !== '0') emptyKeys += ',batchNo';

        this.handleMaterialChange(row, target, emptyKeys);
        if (this.bizOptions().batchMode==='0') this.setWarehouseOptions(row);
      },

    }
  }
</script>

<style lang="less" scoped>
  @import "../../common/less/BillForm.less";
</style>
