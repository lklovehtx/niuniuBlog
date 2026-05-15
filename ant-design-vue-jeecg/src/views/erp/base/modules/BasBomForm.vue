<template>
  <a-spin :spinning="confirmLoading">
    <div>
      <!-- 主表单区域 -->
      <a-form-model ref="form" :model="model" :rules="validatorRules">
        <a-row>
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="编码" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="code">
              <a-input v-model="model.code" placeholder="请输入" @blur="onCodeNameBlur" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24">
            <a-form-model-item label="名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="name">
              <a-input v-model="model.name" placeholder="请输入" @blur="onCodeNameBlur" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24" >
            <a-form-model-item label="助记名" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="auxName">
              <a-input v-model="model.auxName" placeholder="请输入" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24" >
            <a-form-model-item label="主物料" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="materialId">
              <a-row type="flex">
                <a-col flex="auto"><j-search-select-tag v-model="model.materialId" dict="bas_material,aux_name,id" placeholder="请选择" :disabled="disabled"/></a-col>
                <a-col flex="40px"><a-button @click="$refs.materialPopup.openModal()" type="link" icon="cluster" :disabled="disabled"/></a-col>
              </a-row>
              <j-popup v-show="false" v-model="model.materialId" ref="materialPopup" code="bas_material" :param="{}"
                       field="materialId" org-fields="id" dest-fields="materialId" :multi="false"/>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24" >
            <a-form-model-item label="规格型号" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="materialModel">
              <a-input v-model="model.materialModel" :disabled="true"/>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24" >
            <a-form-model-item label="单位" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="unitId">
              <j-search-select-tag type="list" v-model="model.unitId" :dictOptions="unitOptions" placeholder="请选择" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24" >
            <a-form-model-item label="启用" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="isEnabled">
              <j-dict-select-tag type="list" v-model="model.isEnabled" dictCode="yn" placeholder="请选择" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :xl="8" :lg="12" :md="24" >
            <a-form-model-item label="备注" :labelCol="labelCol1_5" :wrapperCol="wrapperCol1_5" prop="remark">
              <a-textarea v-model="model.remark" rows="1" autoSize :readOnly="disabled" />
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-collapse v-model:activeKey="collapseActiveKey" :bordered="false">
          <a-collapse-panel key="0" header="操作信息" style="background: #f7f7f7;border-radius: 4px;margin-bottom: 24px;border: 0;overflow: hidden">
            <a-row>
              <a-col :xl="8" :lg="12" :md="24">
                <a-form-model-item label="创建时间" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="createTime">
                  <j-date v-model="model.createTime" :showTime="true" dateFormat="YYYY-MM-DD HH:mm:ss" style="width: 100%" disabled/>
                </a-form-model-item>
              </a-col>
              <a-col :xl="8" :lg="12" :md="24">
                <a-form-model-item label="创建人" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="createBy">
                  <j-dict-select-tag v-model="model.createBy" dictCode="sys_user,realname,username" :disabled="true"/>
                </a-form-model-item>
              </a-col>
              <a-col :xl="8" :lg="12" :md="24">
                <a-form-model-item label="修改时间" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="updateTime">
                  <j-date v-model="model.updateTime" :showTime="true" dateFormat="YYYY-MM-DD HH:mm:ss" style="width: 100%" disabled/>
                </a-form-model-item>
              </a-col>
              <a-col :xl="8" :lg="12" :md="24">
                <a-form-model-item label="修改人" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="updateBy">
                  <j-dict-select-tag v-model="model.updateBy" dictCode="sys_user,realname,username" :disabled="true"/>
                </a-form-model-item>
              </a-col>
            </a-row>
          </a-collapse-panel>
        </a-collapse>

        <!-- 子表单区域 -->
        <a-tabs v-model="activeKey" @change="handleChangeTabs">
          <a-tab-pane tab="子物料" key="entryTable" :forceRender="true">
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
              @added="event => this.onEntryAdded(event)"
              @valueChange="onEntryValueChange">

              <!-- 20251106 cfm add -->
              <template v-if="!isMobile()" v-slot:toolbarSuffix>
                <p style="float: right;">生产损耗率‌：生产中需额外增加子件数量以弥补损耗（如损耗率为5%，则每生产1父件需准备1.05个子件）。</p>
              </template>
              <template v-if="!disabled" v-slot:materialPopup="props">
                <vxe-column-popup :props="props" @valuesChange="onMaterialValuesChange"/>
              </template>
            </j-vxe-table>
          </a-tab-pane>
        </a-tabs>
      </a-form-model>
    </div>
  </a-spin>
</template>

<script>
  import { JVxeTableModelMixin } from '@/mixins/JVxeTableModelMixin.js'
  import { JVXETypes } from '@/components/jeecg/JVxeTable'
  import { getRefPromise } from '@/components/jeecg/JVxeTable/utils/vxeUtils.js'
  import { validateDuplicateValue } from '@/utils/util'
  import {BillFormMixin} from '../../common/mixins/bill/BillFormMixin'
  import {BillFormGridMixin} from '../../common/mixins/bill/BillFormGridMixin'
  import {DataMixin} from '../../common/mixins/DataMixin'
  import {DetailMixin} from '../../common/mixins/bill/DetailMixin'
  import VxeColumnPopup from "../../common/components/VxeColumnPopup"

  export default {
    name: 'BasBomForm',
    mixins: [JVxeTableModelMixin, BillFormMixin, BillFormGridMixin, DataMixin, DetailMixin],
    components: {VxeColumnPopup},

    data() {
      return {
        collapseActiveKey: '',

        model:{
          materialId: '',
          materialModel: '',
          unitId: '',
          isEnabled: 1,
        },
        unitOptions: [],

        validatorRules: {
          code: [
            { required: true, message: '请输入编码!'},
            { validator: (rule, value, callback) => validateDuplicateValue('bas_bom', 'code', value, this.model.id, callback) }
          ],
          name: [
            { required: true, message: '请输入名称!'},
            { validator: (rule, value, callback) => validateDuplicateValue('bas_bom', 'name', value, this.model.id, callback) }
          ],
          auxName: [
            { required: true, message: '请输入助记名!'},
            { validator: (rule, value, callback) => validateDuplicateValue('bas_bom', 'aux_name', value, this.model.id, callback) }
          ],
           materialId: [
              { required: true, message: '请输入主物料!'},
           ],
           unitId: [
              { required: true, message: '请输入计量单位!'},
           ],
           isEnabled: [
              { required: true, message: '请输入是否启用!'},
           ],
        },

        entryNoStep: 1,
        addDefaultRowNum: 0,
        refKeys: ['entryTable', ],
        tableKeys:['entryTable', ],
        activeKey: 'entryTable',

        // BOM明细
        entryTable: {
          loading: false,
          dataSource: [],
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
              width:"260px",
              placeholder: '请输入',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }, { unique: true, message: '${title}不能重复' }],
            },
            {
              title: '',
              key: 'materialPopup',
              type: JVXETypes.slot,
              slotName:"materialPopup",
              width:"40px",
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
              width:"260px",
              defaultValue:'',
              disabled: true,
            },
            {
              title: '单位',
              key: 'unitId',
              type: JVXETypes.select,
              options:[],
              dictCode:"bas_unit,name,id",
              width:"90px",
              align:"center",
              placeholder: '请选择',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '数量',
              key: 'qty',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              placeholder: '请输入',
              formatter: this.formatQty,
              defaultValue: 1,
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            { //20250406 cfm add
              title: '生产损耗率%',
              key: 'lossRate',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              placeholder: '请输入',
              formatter: this.formatQty,
              defaultValue: 0,
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '拆卸时成本占比%',
              key: 'costRate',
              type: JVXETypes.inputNumber,
              width:"160px",
              align:"right",
              placeholder: '请输入',
              formatter: this.formatQty,
              defaultValue: 0,
              validateRules: [{ required: true, message: '${title}不能为空' }],
              statistics: ['sum'],
            },
            {
              title: '备注',
              key: 'remark',
               type: JVXETypes.input,
              width:"100px",
              placeholder: '请输入',
              defaultValue:'',
            },
          ]
        },
        url: {
          add: "/base/basBom/add",
          edit: "/base/basBom/edit",
          queryById: "/base/basBom/queryById",
          basBomEntry: {
            list: '/base/basBom/queryEntryByMainId'
          },
        }
      }
    },

    watch: {
      'model.materialId': {
        immediate: true,
        handler() {
          this.model.materialModel = this.getMaterialModel(this.model.materialId);

          const units = this.getMaterialUnits(this.model.materialId);
          if (!units || !units.find(u => u.id === this.model.unitId)) this.model.unitId = '';
          this.unitOptions = units ? units.map(u => ({value: u.id, text: u.name})) : [];
        }
      },

    },

    methods: {
      addBefore(){
        this.entryTable.dataSource=[]
      },

      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key))
        return Promise.all(values)
      },

      editAfter() {
        if (this.model.id) {
          let params = { id: this.model.id }
          this.requestSubTableData(this.url.basBomEntry.list, params, this.entryTable)
        }
      },

      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
          basBomEntryList: allValues.tablesValue[0].tableData,
        }
      },

      onCodeNameBlur(val) {
        if (!this.model.code || this.model.code.length === 0 || !this.model.name || this.model.name.length === 0)
          return;
        if (!this.model.auxName || this.model.auxName.length === 0)
          this.model.auxName = this.model.code + ' ' + this.model.name;
      },

      onEntryValueChange(event) {
        const { type, value, oldValue, row, column, target, isSetValues } = event;
        if (value === oldValue || isSetValues) return;

        switch (column.property) {
          case "materialId":
            this.onMaterialValuesChange({values: {materialId: value}, oldValues: {materialId: oldValue}, row: row, target: target});
            break;
        }
      },

      onMaterialValuesChange(event) {
        const {values, oldValues, row, target} = event;
        if (values.materialId === oldValues.materialId) return;

        this.handleMaterialChange(row, target);
      },

    }
  }
</script>

<style lang="less" scoped>
  @import "../../common/less/BillForm.less";
</style>
