<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="独立需求号">
              <a-input placeholder="请输入" v-model="queryParam.demandNo"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="需求公司">
              <a-input placeholder="请输入" v-model="queryParam.company"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="需求供应商">
              <a-input placeholder="请输入" v-model="queryParam.supplierName"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="myHandleAdd" type="link" icon="plus">新增</a-button>
      <a-button type="link" icon="download" @click="handleExportXls('独立需求')">导出</a-button>
      <a-upload
        name="file"
        :showUploadList="false"
        :multiple="false"
        :headers="tokenHeader"
        :action="importExcelUrl"
                @change="handleImportExcel">
        <a-button type="link" icon="import">导入</a-button>
      </a-upload>
      <i class="anticon anticon-info-circle ant-alert-icon"></i>
      已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
      <a v-if="selectedRowKeys.length > 0" style="margin-left: 12px" @click="onClearSelected">清空</a>
    </div>

    <!-- table区域 -->
    <div>
      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :scroll="{ x: true}"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">

        <span slot="action" slot-scope="text, record">
          <a @click="myHandleEdit(record)">编辑</a>
          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item key="1">
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">删除</a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>
      </a-table>
    </div>

    <psi-independent-demand-modal ref="modalForm" @ok="modalFormOk"></psi-independent-demand-modal>
  </a-card>
</template>

<script>
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { getAction, deleteAction } from '@/api/manage'
  import PsiIndependentDemandModal from './modules/PsiIndependentDemandModal'

  export default {
    name: 'PsiIndependentDemandList',
    mixins: [JeecgListMixin],
    components: { PsiIndependentDemandModal },

    data() {
      return {
        description: '独立需求管理',
        url: {
          list: '/psi/independentDemand/list',
          queryItems: '/psi/independentDemand/items',
          queryById: '/psi/independentDemand/queryById',
          delete: '/psi/independentDemand/delete',
          deleteBatch: '/psi/independentDemand/deleteBatch',
          exportXlsUrl: '/psi/independentDemand/exportXls',
          importExcelUrl: '/psi/independentDemand/importExcel'
        },
        columns: [
          {
            title: '#',
            dataIndex: '',
            key: 'rowIndex',
            width: 60,
            align: 'center',
            customRender: (t, r, index) => parseInt(index) + 1
          },
          {
            title: '独立需求号',
            dataIndex: 'demandNo',
            width: 150,
            align: 'center'
          },
          {
            title: '需求公司',
            dataIndex: 'company',
            width: 120,
            align: 'center'
          },
          {
            title: '需求工厂',
            dataIndex: 'factory',
            width: 100,
            align: 'center'
          },
          {
            title: '需求供应商',
            dataIndex: 'supplierName',
            width: 150,
            align: 'center'
          },
          {
            title: '需求客户',
            dataIndex: 'customerName',
            width: 150,
            align: 'center'
          },
          {
            title: '需求外部编号',
            dataIndex: 'externalNo',
            width: 150,
            align: 'center'
          },
          {
            title: '需求状态',
            dataIndex: 'demandStatus',
            width: 100,
            align: 'center',
            customRender: (text) => {
              const statusMap = { '0': '草稿', '1': '已提交', '2': '已审批', '3': '已关闭' }
              return statusMap[text] || text
            }
          },
          {
            title: '创建时间',
            dataIndex: 'createTime',
            width: 160,
            align: 'center'
          },
          {
            title: '操作',
            dataIndex: 'action',
            width: 150,
            fixed: 'right',
            align: 'center',
            scopedSlots: { customRender: 'action' }
          }
        ],
        queryParam: {
          demandNo: '',
          company: '',
          supplierName: ''
        }
      }
    },

    methods: {
      loadData(arg) {
        if (!this.url.list) {
          this.$message.error('请设置url.list属性!')
          return
        }
        if (arg === 1) {
          this.ipagination.current = 1
        }
        let params = this.getQueryParams()
        this.loading = true
        getAction(this.url.list, params).then(res => {
          if (res.success) {
            this.dataSource = res.result.records || res.result
            this.ipagination.total = res.result.total || res.result.length
          }
          this.loading = false
        }).catch(err => {
          this.$message.error(err.message)
          this.loading = false
        })
      },

      myHandleAdd() {
        this.$refs.modalForm.add()
        this.$refs.modalForm.title = '新增独立需求'
        this.$refs.modalForm.disableSubmit = false
      },

      myHandleEdit(record) {
        this.$refs.modalForm.edit(record)
        this.$refs.modalForm.title = '编辑独立需求'
        this.$refs.modalForm.disableSubmit = false
      },

      myHandleDetail(record) {
        this.$refs.modalForm.edit(record)
        this.$refs.modalForm.title = '查看独立需求'
        this.$refs.modalForm.disableSubmit = true
      },

      handleDelete(id) {
        deleteAction(this.url.delete, { id: id }).then(res => {
          if (res.success) {
            this.$message.success(res.message)
            this.loadData()
          } else {
            this.$message.warning(res.message)
          }
        })
      }
    }
  }
</script>
