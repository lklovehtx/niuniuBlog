<template>
  <div class="items-container">
    <div class="table-operator" v-if="!disabled">
      <a-button @click="handleAddRow" type="primary" icon="plus">添加行</a-button>
      <a-button @click="handleDeleteRows" type="danger" icon="delete" :disabled="selectedRowKeys.length === 0">删除行</a-button>
    </div>

    <a-table
      ref="table"
      size="small"
      bordered
      rowKey="id"
      :columns="columns"
      :dataSource="dataSource"
      :pagination="false"
      :loading="loading"
      :scroll="{ y: 400 }"
      :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}">

      <template slot="lineNo" slot-scope="text, record, index">
        {{ index + 1 }}
      </template>

      <template slot="materialName" slot-scope="text, record">
        <a-input v-model="record.materialName" placeholder="选择物料" readonly @click="openMaterialSelect(record)"/>
      </template>

      <template slot="factory" slot-scope="text, record">
        <a-input v-model="record.factory" placeholder="工厂"/>
      </template>

      <template slot="requiredQty" slot-scope="text, record">
        <a-input-number v-model="record.requiredQty" :min="0" style="width: 100%"/>
      </template>

      <template slot="unit" slot-scope="text, record">
        <a-input v-model="record.unit" placeholder="单位"/>
      </template>

      <template slot="requiredDate" slot-scope="text, record">
        <j-date v-model="record.requiredDate" placeholder="需求日期" style="width: 100%"/>
      </template>

      <template slot="action" slot-scope="text, record, index">
        <a @click="handleDeleteRow(index)" v-if="!disabled">删除</a>
      </template>
    </a-table>
  </div>
</template>

<script>
  import { getAction, postAction, deleteAction } from '@/api/manage'

  export default {
    name: 'PsiIndependentDemandItems',

    props: {
      demandId: {
        type: String,
        default: null
      },
      disabled: {
        type: Boolean,
        default: false
      }
    },

    data() {
      return {
        loading: false,
        dataSource: [],
        selectedRowKeys: [],
        columns: [
          {
            title: '#',
            key: 'lineNo',
            width: 60,
            scopedSlots: { customRender: 'lineNo' }
          },
          {
            title: '物料名称',
            dataIndex: 'materialName',
            width: 200,
            scopedSlots: { customRender: 'materialName' }
          },
          {
            title: '物料编码',
            dataIndex: 'materialCode',
            width: 120
          },
          {
            title: '需求工厂',
            dataIndex: 'factory',
            width: 120,
            scopedSlots: { customRender: 'factory' }
          },
          {
            title: '需求数量',
            dataIndex: 'requiredQty',
            width: 120,
            scopedSlots: { customRender: 'requiredQty' }
          },
          {
            title: '单位',
            dataIndex: 'unit',
            width: 80,
            scopedSlots: { customRender: 'unit' }
          },
          {
            title: '需求日期',
            dataIndex: 'requiredDate',
            width: 150,
            scopedSlots: { customRender: 'requiredDate' }
          },
          {
            title: '操作',
            key: 'action',
            width: 80,
            scopedSlots: { customRender: 'action' }
          }
        ]
      }
    },

    methods: {
      loadItems() {
        if (!this.demandId) {
          return
        }
        this.loading = true
        getAction('/psi/independentDemand/items', { demandId: this.demandId }).then(res => {
          if (res.success) {
            this.dataSource = res.result || []
          }
        }).finally(() => {
          this.loading = false
        })
      },

      handleAddRow() {
        const newRow = {
          id: null,
          demandId: this.demandId,
          lineNo: this.dataSource.length + 1,
          materialId: '',
          materialCode: '',
          materialName: '',
          factory: '',
          requiredQty: 0,
          unit: '',
          requiredDate: null
        }
        this.dataSource.push(newRow)
      },

      handleDeleteRow(index) {
        this.dataSource.splice(index, 1)
        this.recalculateLineNo()
      },

      handleDeleteRows() {
        if (this.selectedRowKeys.length > 0) {
          this.selectedRowKeys.forEach(key => {
            const index = this.dataSource.findIndex(item => item.id === key)
            if (index > -1) {
              this.dataSource.splice(index, 1)
            }
          })
          this.recalculateLineNo()
        }
      },

      recalculateLineNo() {
        this.dataSource.forEach((item, index) => {
          item.lineNo = index + 1
        })
      },

      onSelectChange(selectedRowKeys) {
        this.selectedRowKeys = selectedRowKeys
      },

      openMaterialSelect(record) {
        this.$message.info('物料选择功能待实现')
      },

      getItems() {
        return this.dataSource
      },

      submitItems() {
        if (!this.demandId || this.dataSource.length === 0) {
          return Promise.resolve()
        }
        
        const items = this.dataSource.map((item, index) => ({
          ...item,
          demandId: this.demandId,
          lineNo: index + 1
        }))
        
        return new Promise((resolve, reject) => {
          this.loading = true
          postAction('/psi/independentDemand/saveItems', { demandId: this.demandId, items: items }).then(res => {
            if (res.success) {
              resolve(res.result)
            } else {
              this.$message.warning(res.message)
              reject(res)
            }
          }).finally(() => {
            this.loading = false
          })
        })
      }
    }
  }
</script>

<style scoped>
  .items-container {
    padding: 10px;
  }
  .table-operator {
    margin-bottom: 10px;
  }
</style>
