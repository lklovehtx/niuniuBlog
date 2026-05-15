<template>
  <a-spin :spinning="confirmLoading">
    <!-- 主表单区域 -->
    <div>
      <a-form-model ref="form" :model="model" :rules="validatorRules">
        <bill-header ref="billHeader" :model="model" :disabled="disabled" :moreStatus.sync="moreStatus" :moreStatus2.sync="moreStatus2" />

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
          <a-tab-pane  tab="明细" key="entryTable" :forceRender="true">
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
              @added="event => {this.entryTable.rowCount++; this.onEntryAdded(event);}"
              @remove="event => this.entryTable.rowCount = this.$refs.entryTable.getTableData().length"
              @selectRowChange="({selectedRows}) => {this.entryTable.selectedRowCount = selectedRows.length; this.refreshCopyAndAddDisabled()}"
              @valueChange="onEntryValueChange">

              <template v-if="!disabled" v-slot:toolbarPrefix>
                <a-button type="primary" icon="plus"  @click="handleAddOut">新增调出</a-button>
                <a-tooltip :title="copyAndAddDisabled ? '请选择一条库存数量的调出分录':''" placement="bottom">
                  <a-button type="primary" icon="plus" @click="handleCopyAndAdd" :disabled="copyAndAddDisabled">复制新增调入</a-button>
                </a-tooltip>
              </template>

              <!-- 20241106 cfm modi: 增加  && !isMobile() -->
              <template v-if="!disabled && !isMobile()" v-slot:toolbarSuffix>
                <p v-if="bizOptions().batchMode==='0'" style="float: right;">提示：明细录入时，先顺序录入物料、仓库！ 仓库只能选择有库存的！</p>
                <p v-else style="float: right;">提示：明细录入时，“物料、仓库”是“批次”查询的参数！</p>
              </template>

              <template v-if="!disabled" v-slot:materialPopup="props">
                <vxe-column-popup :props="props" @valuesChange="onMaterialValuesChange"/>
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
  import {stringIsEmpty} from "../../common/utils/util";
  import pick from "lodash.pick";
  import XEUtils from "xe-utils";

  export default {
    name: 'MoveForm',
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
          stockIoType: '301', //库存调拨
          hasRp: 0,
          hasSwell: 0,
          hasSingle: 0,
        },

        validatorRules: {},

        entryNoStep: 10,
        addDefaultRowNum: 0,
        refKeys: ['entryTable', ],
        tableKeys:['entryTable', ],
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
              title: '出入方向',
              key: 'stockIoDirection',
              type: JVXETypes.select,
              dictCode:"x_stock_io_direction",
              options:[],
              width:"80px",
              align:"center",
              defaultValue: '',
              disabled: true,
            },
            {
              title: '单位',
              key: 'unitId',
              type: JVXETypes.select,
              dictCode:"bas_unit,name,id",
              options:[],
              disabled:true,
              width:"85px",
              align:"center",
            },
            {
              title: '调拨数量',
              key: 'qty',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatQty,
              placeholder: '请输入',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }, {handler: this.rubricValidator}, {handler: this.moveQtyValidator}],
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
              title: '调拨金额',
              key: 'cost',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue: '',
              validateRules: [{ required: true, message: '${title}不能为空' }, {handler: this.rubricValidator}, {handler: this.moveCosttValidator}],
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
              width:"100px",
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

        copyAndAddDisabled: true,
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
        this.$refs.billHeader.fillBillNo('stk_kcdb_bill_no');
      },

      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key))
        return Promise.all(values)
      },

      editAfter() {
        if (!this.model.id) return;
        const that = this;
        const url = this.disabled ? this.entryTable.url.list : this.entryTable.url.editingList;
        this.requestSubTableData(url, { id: this.model.id }, this.entryTable, success);

        function success() {
          for(let rec of that.entryTable.dataSource) {
            if (rec.stockIoDirection==='1') {//调入
              //调入按单位金额计算金额（调出是按与库存的比例计算的金额），如果单位金额四舍五入，可能导致金额与调出不同，所以不四舍五入！
              rec.unitCost = rec.qty === 0 ? 0 : Number((rec.cost / rec.qty));
              rec.inventoryUnitId = '';
              rec.inventoryQty = '';
              rec.inventoryCost = '';
            }
            //调入分录的materialId、batchNo等要与调出分录相同，但batchNo的popup会改变或清空这些列，oldValues用于恢复
            rec.oldValues = pick(rec, 'materialId','barcode','materialModel','batchNo','unitId','warehouseId');
          }

          if (that.entryTable.dataSource.length > 0) that.entryTable.dataSource = [...that.entryTable.dataSource];
        }
      },

      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
          stkIoEntryList: allValues.tablesValue[0].tableData,
        }
      },

      handleAddOut() {
        this.$refs.entryTable.addRows({stockIoDirection: '2'});
      },

      handleCopyAndAdd() {
        const srcRow = this.$refs.entryTable.selectedRows[0];
        const row = pick(srcRow, 'materialId','barcode','materialModel','batchNo','unitId','qty','cost');
        row.stockIoDirection = '1';
        // 20230313 modi: 调出是按比例计算的金额，而调入计算单价后四舍五入，导致计算金额与调出不同，所以不应四舍五入！
        // row.unitCost = srcRow.inventoryQty === 0 ? 0 : Number((srcRow.inventoryCost / srcRow.inventoryQty).toFixed(2));
        row.unitCost = srcRow.inventoryQty === 0 ? 0 : Number((srcRow.inventoryCost / srcRow.inventoryQty));
        //调入分录的materialId、batchNo等要与调出分录相同，但batchNo的popup会改变或清空这些列，oldValues用于恢复
        row.oldValues = pick(row, 'materialId','barcode','materialModel','batchNo','unitId');
        this.$refs.entryTable.addRows(row);
      },

      onEntryValueChange(event) {
        const { type, value, oldValue, row, column, target, isSetValues } = event;
        // 库存批次batchNo相同，但inventoryId可能不同（不同仓库、不同物料可能同batchNo）
        if (value === oldValue && column.property !== 'batchNo' || isSetValues) return;

        let emptyKeys = 'inventoryUnitId,inventoryQty,inventoryCost,unitId,qty';
        if (this.bizOptions().batchMode !== '0') emptyKeys += ',batchNo';
        let values = {};
        switch (column.property) {
          case 'materialId':
            this.onMaterialValuesChange({values: {materialId: value}, oldValues: {materialId: oldValue}, row: row, target: target});
            break;
          case 'warehouseId':
            if (row.stockIoDirection === '2') {
              this.emptyColumns(row, emptyKeys, target);
              if (this.bizOptions().batchMode === '0') this.handleWarehouseChange(row);
            }
            else
              row.oldValues.warehouseId = row.warehouseId;
            break;
          case 'batchNo':
            if (row.stockIoDirection === '1') {
              this.$warning({title: '库存调拨', content: '调入分录不能改变批次！'});
              target.setValues([{rowKey: row.id, values: row.oldValues}]);
              break;
            }

            this.refreshCopyAndAddDisabled();
            if (stringIsEmpty(row.batchNo)) {
              this.emptyColumns(row, emptyKeys, target);
              break;
            }

            this.setMaterialUnitOptions(row);//batchNo通过popup选择，会导致materialId改变，需要重新设置单位的下拉选项
            values.unitId = row.inventoryUnitId;
            values.cost = this.calcMoveCost(row);
            target.setValues([{rowKey: row.id, values: values}]);
            break;
          case 'qty':
            target.setValues([{rowKey: row.id, values: {cost: this.calcMoveCost(row)}}]);
            break;
        }
      },

      onMaterialValuesChange(event) {
        const {values, oldValues, row, target} = event;
        if (values.materialId === oldValues.materialId) return;

        let emptyKeys = 'warehouseId,inventoryUnitId,inventoryQty,inventoryCost,unitId,qty';
        if (this.bizOptions().batchMode !== '0') emptyKeys += ',batchNo';

        if (row.stockIoDirection === '2') {
          this.handleMaterialChange(row, target, emptyKeys);
          this.refreshCopyAndAddDisabled();
          // 20240923 cfm del：修复【BUG】库存调拨，调入仓库只能选择该物料有库存的仓库。
          // if (this.bizOptions().batchMode==='0') this.setWarehouseOptions(row);
        }
        else {
          this.$warning({title: '库存调拨', content: '调入分录不能改变物料！'});
          target.setValues([{rowKey: row.id, values:row.oldValues}]);
        }

      },

      refreshCopyAndAddDisabled() {
        const rows = this.$refs.entryTable.selectedRows;
        this.copyAndAddDisabled = rows.length !== 1 || rows[0].stockIoDirection !== '2' ||
          stringIsEmpty(rows[0].materialId) || stringIsEmpty(rows[0].batchNo);
      },

      calcMoveCost(row, newValues) {
        if (!!newValues && Object.keys(newValues).length > 0) {
          const values = {...row};
          Object.keys(newValues).forEach(key => values[key] = newValues[key]);
          return this.calcMoveCost(values);
        }

        const inventoryQty = Number(row.inventoryQty);
        if (row.stockIoDirection==='2' && inventoryQty === 0) return 0;

        const inventoryCost = Number(row.inventoryCost);
        const qty = Number(row.qty), unitCost = Number(row.unitCost);
        const cost = row.stockIoDirection==='2' ? inventoryCost * qty / inventoryQty : qty * unitCost;
        return Number(cost.toFixed(2));
      },

      moveCosttValidator({cellValue, row, column}, callback, target) {
        const v = Number(cellValue);
        if (!row.batchNo || row.batchNo.length===0 || isNaN(v)) {
          callback();
          return;
        }

        const diff = v - this.calcMoveCost(row); //moveCosttValidator调用了calcMoveCost，无法公用化
        if (diff < -0.01001 || diff > 0.01001) {
          callback(false, '${title}的输入值与计算值相差超过0.01元！');
          return;
        }

        let rows;
        if (row.stockIoDirection==='2') {
          rows = this.$refs.entryTable.getTableData();
          rows = rows.filter(r => r.stockIoDirection==='2' && r.materialId===row.materialId && r.batchNo===row.batchNo);
          if (XEUtils.sum(rows, 'cost') > row.inventoryCost){
            callback(false, '该仓库批次的调出金额合计不能大于库存金额！');
            return;
          }
        }

        rows = this.$refs.entryTable.getTableData();
        rows = rows.filter(r => r.materialId === row.materialId && r.batchNo === row.batchNo);
        let cost = 0;
        for(let r of rows) {
          cost += r.stockIoDirection==='2' ? r.cost : -r.cost;
        }
        if (Math.abs(cost) > 0.01) {
          callback(false, '该物料批次的调出调入金额不相等！');
          return;
        }

        callback(true); //true：通过验证
      },

    }
  }
</script>

<style lang="less" scoped>
  @import "../../common/less/BillForm.less";
</style>
