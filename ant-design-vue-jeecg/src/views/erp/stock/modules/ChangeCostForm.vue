<template>
  <a-spin :spinning="confirmLoading">
    <!-- 主表单区域 -->
    <div>
      <a-form-model ref="form" :model="model" :rules="validatorRules">
        <bill-header ref="billHeader" :model="model" :disabled="disabled" :moreStatus.sync="moreStatus" :moreStatus2.sync="moreStatus2"/>
        <a-row v-show="moreStatus">
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="有应收付" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="hasRp">
              <j-dict-select-tag v-model="model.hasRp" dictCode="yn" :disabled="true"/>
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
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
              @edit-actived="({row}) => {if (this.bizOptions().batchMode==='0') setWarehouseOptions(row);}"
              @added="event => {this.entryTable.rowCount++; this.onEntryAdded(event);}"
              @selectRowChange="({selectedRows}) => {this.entryTable.selectedRowCount = selectedRows.length;}"
              @remove="event => this.entryTable.rowCount = this.$refs.entryTable.getTableData().length"
              @valueChange="onEntryValueChange">

              <template v-if="!disabled" v-slot:materialPopup="props">
                <vxe-column-popup :props="props" @valuesChange="onMaterialValuesChange"/>
              </template>

              <!-- 20241106 cfm modi: isMobile() 改为 !isMobile() -->
              <template v-if="!disabled && !isMobile()" v-slot:toolbarSuffix>
                <p v-if="bizOptions().batchMode==='0'" style="float: right;">提示：明细录入时，先顺序录入物料、仓库！ 仓库只能选择有库存的！</p>
                <p v-else style="float: right;">提示：明细录入时，“物料、仓库”是“批次”查询的参数！</p>
              </template>
            </j-vxe-table>
          </a-tab-pane>

          <template slot="tabBarExtraContent">
            <vxe-table-columns-setter
              :table-key="activeKey + (disabled ? '1':'0')"
              :column-defs="entryTable.columns" :excluded-cols="disabled ? entryTable.exKeysWhenDetail:''"
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

  export default {
    name: 'ChangeCostForm',
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
          stockIoType: '801', //成本调整
          hasRp: 0,
          hasSwell: 0,
          hasSingle: 0,
        },

        validatorRules: {},

        entryNoStep: 10, //分录号自动编号步长
        addDefaultRowNum: 1,// 新增时子表默认添加几行空数据
        tableKeys:['entryTable', ],
        refKeys: ['entryTable', ],
        activeKey: 'entryTable',

        // 明细
        entryTable: {
          loading: false,
          dataSource: [],
          rowCount: 0,
          selectedRowCount:0,
          url: {list: '/stock/stkIo/queryEntryByMainId', editingList: '/stock/stkIo/queryEditingEntryByMainId'},
          exKeysWhenDetail: 'materialPopup,inventoryUnitId,inventoryQty,inventoryCost',
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
              title: '出入方向',
              key: 'stockIoDirection',
              type: JVXETypes.hidden,
              defaultValue: '1',
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
              title: '批次',
              key: 'batchNo',
              type: JVXETypes.popup,
              popupCode: 'stk_inventory_batch',
              orgFields: "material_id,material_model,warehouse_id,batch_no,unit_id,qty,cost",
              destFields: "materialId,materialModel,warehouseId,batchNo,inventoryUnitId,inventoryQty,inventoryCost",
              paramFields: "materialId,warehouseId",
              field: 'batchNo',
              width:"160px",
              fixed: 'left',
              placeholder: '请选择',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '单位',
              key: 'unitId',
              type: JVXETypes.hidden,
              width:"80px",
              defaultValue: '0',
            },
            {
              title: '调整数量',
              key: 'qty',
              type: JVXETypes.hidden,
              width:"100x",
              placeholder: '请输入',
              defaultValue:0,
            },
            {
              title: '调整金额(+/-)',
              key: 'cost',
              type: JVXETypes.inputNumber,
              width:"130px",
              align:"right",
              formatter: this.formatAmt,
              placeholder: '请输入',
              defaultValue: '',
              validateRules: [{ required: true, message: '${title}不能为空' }],
              statistics: ['sum'],
            },
            {
              title: '库存单位',
              key: 'inventoryUnitId',
              type: JVXETypes.select,
              options:[],
              dictCode:"bas_unit,name,id",
              width:"100px",
              align:"center",
              disabled:true,
            },
            {
              title: '库存数量',
              key: 'inventoryQty',
              type: JVXETypes.inputNumber,
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

    created() {
      this.isCalcOutCost = false;
      this.initBatchNoColumn();
      this.filterColumns();
      this.initColumnsForMobile(); //20240718 cfm add
    },

    methods: {
      addBefore(){
        this.entryTable.dataSource=[];
      },

      addAfter() {
        this.$refs.billHeader.fillBillNo('stk_cbtz_bill_no');
      },

      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key))
        return Promise.all(values)
      },

      editAfter() {
        if (!this.model.id) return;

        let url = this.disabled ? this.entryTable.url.list : this.entryTable.url.editingList;
        this.requestSubTableData(url, {id: this.model.id}, this.entryTable);
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

        let emptyKeys = 'inventoryUnitId,inventoryQty,inventoryCost,cost';
        if (this.bizOptions().batchMode !== '0') emptyKeys += ',batchNo';
        switch (column.property) {
           case "materialId":
             this.onMaterialValuesChange({values: {materialId: value}, oldValues: {materialId: oldValue}, row: row, target: target});
             break;
          case "warehouseId":
            this.emptyColumns(row, emptyKeys);
            if (this.bizOptions().batchMode === '0') this.handleWarehouseChange(row);
            break;
           case 'batchNo':
             target.setValues([{ rowKey: row.id, values: {unitId: row.inventoryUnitId, cost:''} }]);
             break;
        }
      },

      onMaterialValuesChange(event) {
        const {values, oldValues, row, target} = event;
        if (values.materialId === oldValues.materialId) return;

        let emptyKeys = 'inventoryUnitId,inventoryQty,inventoryCost,cost';
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
