<template>
  <a-spin :spinning="confirmLoading">
      <!-- 主表单区域 -->
    <div>
      <a-form-model ref="form" :model="model" :rules="validatorRules">
        <bill-header ref="billHeader" :model="model" :disabled="disabled" :moreStatus.sync="moreStatus" :moreStatus2.sync="moreStatus2"/>

        <a-row>
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="盘点人" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="checker">
              <j-select-user-by-dep v-model="model.checker" :multi="false" :disabled="disabled"/>
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

        <a-divider orientation="left" style="font-size: 14px; color: #bfbfbf;">盘点范围</a-divider>
        <a-row>
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="仓库" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="warehouseId">
              <j-tree-select v-model="model.warehouseId" dict="bas_warehouse,aux_name,id" pidValue="0"
                             hasChildField="has_child" placeholder="请选择" :disabled="action!=='add'"/>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="物料分类" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="materialCategoryId">
              <j-tree-select v-model="model.materialCategoryId" dict="bas_material_category,name,id" pidValue="0"
                             hasChildField="has_child" placeholder="请选择" :disabled="action!=='add'"/>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24">
            <a-button v-show="action==='add'" @click="handleCheckRangeOk" :disabled="!isCheckRangeChange" type="primary" style="margin: 5px 0 0 36px">确定</a-button>
          </a-col>
        </a-row>

        <!-- 子表单区域 -->
        <a-tabs v-model="activeKey" @change="handleChangeTabs">
          <a-tab-pane :tab="bizOptions().batchMode==='0' ? '账存物料':'账存批次'" key="entryTable" :forceRender="true">
            <j-vxe-table
              v-show="!isCheckRangeChange"
              keep-source
              ref="entryTable"
              :loading="entryTable.loading"
              :columns="entryTable.columns"
              :dataSource="entryTable.dataSource"
              :maxHeight="300"
              :disabled="disabled"
              :rowNumber="false"
              :rowSelection="!disabled"
              :toolbar="action==='edit'"
              :resizable="true"
              :toolbar-config="{slots: ['prefix', 'suffix'], btn: ['clearSelection']}"
              :edit-config="{trigger: 'click', mode: 'row', showIcon: false}"
              @selectRowChange="({selectedRows}) => {this.entryTable.selectedRowCount = selectedRows.length; }"
              @valueChange="onEntryValueChange"/>
          </a-tab-pane>

          <a-tab-pane :tab="bizOptions().batchMode==='0' ? '账外物料':'账外批次'" key="newEntryTable" :forceRender="true">
            <j-vxe-table
              keep-source
              ref="newEntryTable"
              :loading="newEntryTable.loading"
              :columns="newEntryTable.columns"
              :dataSource="newEntryTable.dataSource"
              :maxHeight="300"
              :disabled="disabled"
              :rowNumber="false"
              :rowSelection="!disabled"
              :toolbar="action==='edit'"
              :resizable="true"
              :edit-config="{trigger: 'click', mode: 'row', showIcon: false}"
              @edit-actived="({row}) => setMaterialUnitOptions(row, $refs.newEntryTable)"
              @added="onInEntryAdded"
              @selectRowChange="({selectedRows}) => {this.newEntryTable.selectedRowCount = selectedRows.length; }"
              @valueChange="onEntryValueChange">

              <template v-if="!disabled" v-slot:materialPopup="props">
                <vxe-column-popup :props="props" @valuesChange="onMaterialValuesChange"/>
              </template>
            </j-vxe-table>
          </a-tab-pane>

          <template slot="tabBarExtraContent">
            <vxe-table-columns-setter
              :table-key="activeKey + (disabled ? '1':'0')"
              :column-defs="activeKey==='entryTable' ? entryTable.columns : newEntryTable.columns"
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
  import {stringIsEmpty, emitColumnsChange} from "../../common/utils/util";

  export default {
    name: 'CheckForm',
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

          //盘点范围
          warehouseId: '',
          materialCategoryId: '',
        },

        //上次盘点范围
        lastCheckRange: {
          warehouseId: ' ', //20231217 cfm modi: 初始为空格，使得新增一进入时可以【确定】
          materialCategoryId: '',
        },

        validatorRules: {
           //20231217 cfm del
           //warehouseId: [ { required: true, message: '请选择仓库!'} ],
        },

        entryNoStep: 10,
        addDefaultRowNum: 0,
        tableKeys:['entryTable', 'newEntryTable',],
        refKeys: ['entryTable', 'newEntryTable',],
        activeKey: 'entryTable',

        // 明细
        entryTable: {
          loading: false,
          dataSource: [],
          rowCount: 0,
          selectedRowCount: 0,
          url: {list: '/stock/stkCheck/queryEntryByMainId', listByRange: '/stock/stkCheck/queryEntryByRange'},
          columns: [
            {
              title: '是否新批次',
              key: 'isNewBatch',
              type: JVXETypes.hidden,
              width:"1px",
              defaultValue: '0',
              disabled:true,
            },
            {
              title: '单位',
              key: 'unitId',
              type: JVXETypes.select,
              dictCode:"bas_unit,name,id",
              width:"85px",
              align:"center",
              defaultValue: '',
              options:[],
              validateRules: [{ required: true, message: '${title}不能为空' }],
              disabled:true,
            },
            {
              title: '实存数量',
              key: 'qty',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatQty,
              defaultValue: '',
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
              defaultValue: '',
              disabled:true,
            },
            {
              title: '物料',
              key: 'materialId',
              type: JVXETypes.selectSearch,
              dictCode:"bas_material,aux_name,id",
              width:"150px",
              fixed: 'left',
              defaultValue: '',
              options:[],
              validateRules: [{ required: true, message: '${title}不能为空' }],
              disabled:true,
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
              dictCode:"bas_warehouse,aux_name,id",
              width:"180px",
              defaultValue: '',
              options:[],
              validateRules: [{ required: true, message: '${title}不能为空' }],
              disabled:true,
            },
            {
              title: '批次',
              key: 'batchNo',
              type: JVXETypes.input,
              width:"130px",
              fixed: 'left',
              sortable: true,
              defaultValue: '',
              validateRules: [{ required: true, message: '${title}不能为空' }],
              disabled:true,
            },
            {
              title: '供应商',
              key: 'supplierId',
              type: JVXETypes.select,
              dictCode:"bas_supplier,aux_name,id",
              width:"150px",
              defaultValue: '',
              disabled:true,
            },
            {
              title: '账存数量',
              key: 'bookQty',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatQty,
              defaultValue: 0,
              validateRules: [{ required: true, message: '${title}不能为空' }],
              statistics: ['sum'],
              disabled:true,
            },

           //20231118 cfm modi
            {
              title: '盘盈数量',
              key: 'profitQty',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatQty,
              defaultValue: 0,
              disabled:true,
              statistics: ['sum'],
            },
            {
              title: '盘亏数量',
              key: 'lossQty',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatQty,
              defaultValue: 0,
              disabled:true,
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
          ],
        },

        //明细：新批次
        newEntryTable: {
          loading: false,
          dataSource: [],
          rowCount: 0,
          selectedRowCount: 0,
          exKeysWhenDetail: 'materialPopup,inventoryUnitId,inventoryQty,inventoryCost',
          columns: []
        },

        url: {
          add: "/stock/stkCheck/add",
          edit: "/stock/stkCheck/edit",
          check: "/stock/stkCheck/check",
          ebpm: "/stock/stkCheck/bpm/end",
          execute: "/stock/stkCheck/execute",
          void: "/stock/stkCheck/void",
          queryById: "/stock/stkCheck/queryById", //20251101 cfm add for 内置BPM
        }
      }
    },

    watch:{
      'entryTable.dataSource'() {
        this.entryTable.rowCount = this.entryTable.dataSource.length;
      },

      'newEntryTable.dataSource'() {
        this.newEntryTable.rowCount = this.newEntryTable.dataSource.length;
      },

      'entryTable.loading': {
        immediate: true,
        handler() {
          this.$emit("update:loading", this.entryTable.loading || this.newEntryTable.loading);
        }
      },

      'newEntryTable.loading': {
        immediate: true,
        handler() {
          this.$emit("update:loading", this.entryTable.loading || this.newEntryTable.loading);
        }
      },

    },

    computed: {
       isCheckRangeChange() {
        return this.model.warehouseId !== this.lastCheckRange.warehouseId ||
          this.model.materialCategoryId !== this.lastCheckRange.materialCategoryId;
      },
    },

    created() {
      this.initBatchNoColumn();
      this.filterColumns();
      this.initColumnsForMobile();

      for(let col of this.entryTable.columns){
        //新增盘点卡时不能录入
        if (this.action==='add' && col.key==='qty') {
          col.validateRules = [];
          col.disabled = true;
        }

        //账外
        let col2 = Object.assign({}, col);
        if (!['entryNo', 'isNewBatch', 'bookQty', 'profitQty','lossQty'].includes(col2.key)) col2.disabled = false; //20231118 cfm modi: 增加lossQty
        if (col2.key==='isNewBatch') col2.defaultValue = 1;
        if (col2.key==='bookQty') col2.defaultValue = 0;
        this.newEntryTable.columns.push(col2);

        if (col.key==='materialPopup') col.type = JVXETypes.hidden; //账存物料/账存批次，不用选择物料
      }

    },

    methods: {
      addBefore(){
        this.entryTable.dataSource=[];
        this.newEntryTable.dataSource=[];
      },

      addAfter() {
        this.$refs.billHeader.fillBillNo('stk_kcpd_bill_no');
      },

      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key))
        return Promise.all(values)
      },

      editAfter() {
        if (!this.model.id) {
          //不能在created()中判断，因为this.action还未传入，this.model.id也还未设置
          for(let col of this.entryTable.columns) {
            //新增盘点卡时不能录入；
            if (col.key === 'qty') {
              col.disabled = true;
              emitColumnsChange(this.entryTable.columns);
              break;
            }
          }
          return;
        }

        this.lastCheckRange.warehouseId = this.model.warehouseId;
        this.lastCheckRange.materialCategoryId = this.model.materialCategoryId;
        const that = this;
        let params = { id: this.model.id }
        this.requestSubTableData(this.entryTable.url.list, params, this.entryTable, splitData);

        function splitData(){
          let ds1 = [], ds2 = [];
          for(let row of that.entryTable.dataSource) row.isNewBatch===0 ? ds1.push(row):ds2.push(row);
          that.entryTable.dataSource = ds1;
          that.newEntryTable.dataSource = ds2;
        }
      },

      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
          stkCheckEntryList: allValues.tablesValue[0].tableData.concat(allValues.tablesValue[1].tableData),
        }
      },

      handleCheckRangeOk(){
        const that = this;
        this.lastCheckRange.warehouseId = this.model.warehouseId;
        this.lastCheckRange.materialCategoryId = this.model.materialCategoryId;
        this.requestSubTableData(this.entryTable.url.listByRange, this.lastCheckRange, this.entryTable, success);

        function success() {
          const ds = that.entryTable.dataSource;
          that.entryTable.dataSource = [];
          setTimeout(() => that.$refs.entryTable.addRows(ds), 100);
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
              values.bookQty = (row.bookQty * rate).toFixed(3);
              this.calcProfitLossQty(values);
              target.setValues([{rowKey: row.id, values: values}]);
            }
            break;
          case "qty":
            values = {};
            this.calcProfitLossQty(row, values);
            target.setValues([{rowKey: row.id, values: values}]);
            break;
        }
      },

      onMaterialValuesChange(event) {
        const {values, oldValues, row, target} = event;
        if (values.materialId === oldValues.materialId) return;

        this.handleMaterialChange(row, target, 'unit,qty,profitQty,lossQty'); //20231118 cfm modi: 增加lossQty
      },

      //在CheckModal中调用
      handleMySave() {
        for(let col of this.entryTable.columns){
          if (col.key==='qty') {
            col.validateRules = []; //清除校验，以便可以中途保存
            break;
          }
        }
        emitColumnsChange(this.entryTable.columns);

        this.handleSave();
      },

      //在CheckModal中调用
      handleMySubmit() {
        let str = '';
        for (let row of this.$refs.entryTable.getTableData()) {
          if (Number(row.qty) === 0) {
            str = `【分录号：${row.entryNo} 等】实存数量为0，`;
            break;
          }
        }

        this.$confirm({
          title: '盘点完成',
          content: `${str}确认本卡盘点完成？`, //entry.qty不为空有validateRules限制
          autoFocusButton: 'cancel',
          onOk: () => this.handleSubmit(),
        });
      },

      //计算盘点卡明细的盈亏数量
      //values：返回结果
      calcProfitLossQty(record, values) {
        values = values || record;
        const delta = Number(record.qty) - Number(record.bookQty);
        values.profitQty = delta > 0 ? delta : 0;
        values.lossQty = delta < 0 ? -delta : 0;
      },

    }
  }
</script>

<style lang="less" scoped>
  @import "../../common/less/BillForm.less";
</style>
