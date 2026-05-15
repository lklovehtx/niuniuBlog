<template>
  <a-spin :spinning="confirmLoading">
    <!-- 主表单区域 -->
    <div>
      <a-form-model ref="form" :model="model" :rules="validatorRules">
        <bill-header ref="billHeader" :model="model" :disabled="disabled" :moreStatus.sync="moreStatus" :moreStatus2.sync="moreStatus2"/>
        <a-row v-show="moreStatus">
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="有应付" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="hasRp">
              <j-dict-select-tag v-model="model.hasRp" dictCode="yn" :disabled="true"/>
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :xl="8" :lg="12" :md="24" >
            <a-form-model-item label="供应商" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="supplierId">
              <j-search-select-tag v-model="model.supplierId" dict="bas_supplier,aux_name,id" placeholder="请选择"
                                   :disabled="disabled" @change="val => {if (!val) this.model.supplierId=''}"/> <!-- 20251030 cfm modi: 增加@change -->
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="库管员" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="handler">
              <j-select-user-by-dep v-model="model.handler" :multi="false" :disabled="disabled"/>
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
              :toolbar="!disabled"
              :resizable="true"
              :edit-config="{trigger: 'click', mode: 'row', showIcon: false}"
              @edit-actived="({row}) => setMaterialUnitOptions(row, $refs.entryTable)"
              @added="event => {this.entryTable.rowCount++; this.onInEntryAdded(event);}"
              @selectRowChange="({selectedRows}) => {this.entryTable.selectedRowCount = selectedRows.length;}"
              @remove="event => this.entryTable.rowCount = this.$refs.entryTable.getTableData().length"
              @valueChange="onEntryValueChange">

              <template v-if="!disabled" v-slot:materialPopup="props">
                <vxe-column-popup :props="props" @valuesChange="onMaterialValuesChange"/>
              </template>
            </j-vxe-table>

          </a-tab-pane>

          <template slot="tabBarExtraContent">
            <vxe-table-columns-setter
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
  import {mixinDevice} from '@/utils/mixin'
  import BillHeader from "../../common/components/BillHeader"
  import BillApproval from "../../common/components/BillApproval"
  import VxeTableColumnsSetter from "../../common/components/VxeTableColumnsSetter"
  import VxeColumnPopup from "../../common/components/VxeColumnPopup"
  import {stringIsEmpty} from "../../common/utils/util"

  export default {
    name: 'OtherInForm',
    mixins: [JVxeTableModelMixin, BillFormMixin, BillFormGridMixin, DetailMixin, DetailValueMixin, DetailFormatMixin, DetailValidatorMixin, DataMixin, mixinDevice],
    components: {BillHeader, BillApproval, VxeTableColumnsSetter, VxeColumnPopup},

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
          stockIoType: '199', //其他入库
          hasRp: 0,
          hasSwell: 0,
          hasSingle: 0,
        },

        validatorRules: {},

        entryNoStep: 10,
        addDefaultRowNum: 1,
        tableKeys:['entryTable', ],
        refKeys:  ['entryTable', ],
        activeKey: 'entryTable',

        // 明细
        entryTable: {
          loading: false,
          dataSource: [],
          rowCount: 0,
          selectedRowCount:0,
          url: {list: '/stock/stkIo/queryEntryByMainId'},
          exKeysWhenDetail: 'materialPopup',
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
              placeholder: '请输入',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '入库数量',
              key: 'qty',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatQty,
              placeholder: '请输入',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }, {handler: this.rubricValidator}],
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
              placeholder: '请输入',
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
              title: '批次',
              key: 'batchNo',
              type: JVXETypes.input,
              width:"130px",
              fixed: 'left',
              placeholder: '请输入',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '仓库',
              key: 'warehouseId',
              type: JVXETypes.selectSearch,
              options:[],
              dictCode:"bas_warehouse,aux_name,id",
              width:"180px",
              fixed: 'left',
              placeholder: '请输入',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '单位金额',
              key: 'price',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              placeholder: '请输入',
              defaultValue:0,
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '入库金额',
              key: 'cost',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              placeholder: '请输入',
              defaultValue: 0,
              validateRules: [{ required: true, message: '${title}不能为空' }, {handler: this.rubricValidator}, {handler: this.inCostValidator}],
              statistics: ['sum'],
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
    },

    created() {
      this.initBatchNoColumn();
      this.filterColumns();
      this.initColumnsForMobile();
    },

    methods: {
      addBefore(){
        this.entryTable.dataSource=[];
      },

      addAfter() {
        this.$refs.billHeader.fillBillNo('stk_qtrk_bill_no');
      },

      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key))
        return Promise.all(values)
      },

      editAfter() {
        if (!this.model.id) return;
        this.requestSubTableData(this.entryTable.url.list, {id: this.model.id}, this.entryTable);
      },

      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
          stkIoEntryList: allValues.tablesValue[0].tableData,
        }
      },

      onEntryValueChange(event) {
        const { type, value, oldValue, row, column, target, isSetValues } = event;
        if (value === oldValue || isSetValues) return;

        let values = {};
        switch (column.property) {
          case "materialId":
            this.onMaterialValuesChange({values: {materialId: value}, oldValues: {materialId: oldValue}, row: row, target: target});
            break;
          case "unitId":
            if (stringIsEmpty(oldValue)) break;
            // unitId下列代码限制由非空变为空：因为value空，得到的rate为空，将恢复原值
            const rate = this.getUnitRate(row.materialId, oldValue, value);
            if (!rate) //unitId新值不合法：与原值不能转换，恢复原值
              target.setValues([{rowKey: row.id, values: {unitId: oldValue} }]);
            else {
              values = {};
              values.qty = (row.qty * rate).toFixed(3);
              values.price = (row.price/rate).toFixed(2);
              target.setValues([{rowKey: row.id, values: values}]);
            }
            break;
          case "qty":
          case "price":
            values = {};
            values.cost = (row.qty*row.price).toFixed(2);
            target.setValues([{rowKey: row.id, values: values}]);
            break;
        }
      },

      onMaterialValuesChange(event) {
        const {values, oldValues, row, target} = event;
        if (values.materialId === oldValues.materialId) return;

        this.handleMaterialChange(row, target, 'qty,price,cost');
      },

      inCostValidator({ cellValue, row, column }, callback, target) {
        const v = Number(cellValue)
        if (isNaN(v)) {
          callback()
          return
        }

        let diff = v - Number((row.qty * row.price).toFixed(2))
        if (diff < -0.01001 || diff > 0.01001) {
          callback(false, '${title}的输入值与计算值相差超过0.01元！')
        } else {
          callback(true) //true：通过验证
        }
      },

    }
  }
</script>

<style lang="less" scoped>
  @import "../../common/less/BillForm.less";
</style>
