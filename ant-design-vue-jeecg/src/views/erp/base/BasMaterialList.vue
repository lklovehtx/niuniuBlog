<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="编码">
              <a-input placeholder="请输入" v-model="queryParam.code"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="名称">
              <a-input placeholder="请输入" v-model="queryParam.name"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="物料分类">
              <j-tree-select
                ref="treeSelect"
                placeholder="请选择"
                v-model="queryParam.categoryId"
                dict="bas_material_category,name,id"
                pidField="pid"
                pidValue="0"
                hasChildField="has_child">
              </j-tree-select>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-col :xl="6" :lg="7" :md="8" :sm="24">
                <a-form-item label="条码">
                  <a-input placeholder="请输入" v-model="queryParam.barcode"/>
                </a-form-item>
              </a-col>
              <a-form-item label="税控编码">
                <a-input placeholder="请输入" v-model="queryParam.taxCode"/>
              </a-form-item>
            </a-col>
          </template>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button :disabled="isDisabledAuth('BasMaterial:add')" @click="myHandleAdd" type="link" icon="plus">新增</a-button>
      <a-button :disabled="isDisabledAuth('BasMaterial:add')" type="link" icon="download" @click="handleExportXls('物料')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button :disabled="isDisabledAuth('BasMaterial:add')" type="link" icon="import">导入</a-button>
      </a-upload>

      <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
      <a v-if="selectedRowKeys.length > 0" style="margin-left: 12px" @click="onClearSelected">清空</a>

      <table-columns-setter v-model="columns" style="float: right;"/>
    </div>

    <!-- table区域-begin -->
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
        class="j-table-force-nowrap"
        :components="drag(columns)"
        @change="handleTableChange">

        <a slot="code" @click="myHandleDetail(record)" slot-scope="text, record">{{text}}</a>

        <!-- 20250408 cfm add -->
        <template slot="image" slot-scope="text, record, index">
          <div style="height:48px; width:48px">
            <img style="height: 100%; " :src="getImage(record.image)" :preview="record.id">
          </div>
        </template>

        <span slot="action" slot-scope="text, record">
          <a :disabled="isDisabledAuth('BasMaterial:edit')" @click="myHandleEdit(record)">编辑</a>
          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item :disabled="isDisabledAuth('BasMaterial:delete')" key="1">
                <a-popconfirm :disabled="isDisabledAuth('BasMaterial:delete')" title="确定删除吗?" @confirm="() => handleDelete(record.id)">删除</a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
      <p style="float: right; color: red">注意：物料增删改后，需要点击【浏览器刷新】页面或重新登录才能在单据中看到最新的！</p>
    </div>

    <basMaterial-modal ref="modalForm" @ok="modalFormOk"></basMaterial-modal>
  </a-card>
</template>

<script>
  import '@/assets/less/TableExpand.less'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { ListMixin } from '../common/mixins/ListMixin'
  import BasMaterialModal from './modules/BasMaterialModal'
  import TableColumnsSetter from "../common/components/TableColumnsSetter";
  import XEUtils from "xe-utils";
  import {getFileAccessHttpUrl} from '@/api/manage'; //20250408 cfm add

  export default {
    name: "BasMaterialList",
    mixins:[JeecgListMixin, ListMixin],
    components: {TableColumnsSetter, BasMaterialModal},

    data () {
      return {
        description: '物料',
        previewVisible: false,

        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            fixed:"left",
            width:60,
            align:"center",
            customRender: (t,r,index)=>parseInt(index)+1,
          },
          {
            title:'编码',
            fixed:"left",
            width:160,
            align:"left",
            dataIndex: 'code',
            scopedSlots: { customRender: 'code' },
            sorter: true
          },
          {
            title:'名称',
            align:"left",
            dataIndex: 'name',
            sorter: true
          },
          {
            title:'助记名',
            align:"left",
            dataIndex: 'auxName',
            sorter: true
          },
          {
            title:'条码',
            align:"left",
            dataIndex: 'barcode',
            sorter: true
          },
          {
            title:'分类',
            width:160,
            align:"left",
            dataIndex: 'categoryId_dictText',
            sorter: true
          },
          {
            title:'规格型号',
            dataIndex: 'model'
          },
          {
            title:'主单位',
            width:80,
            align:"center",
            dataIndex: 'unitId_dictText'
          },
          {//20250408 cfm add
            title: '图片',
            align: "center",
            width: 80,
            dataIndex: 'image',
            scopedSlots: {customRender: "image"}
          },
          {
            title:'销售价格',
            width:100,
            align:"right",
            dataIndex: 'salePrice',
            customRender: t => XEUtils.commafy(t,{digits: 2})
          },
          {
            title:'税控编码',
            align:"center",
            dataIndex: 'taxCode'
          },
          {
            title:'启用',
            width:75,
            align:"center",
            dataIndex: 'isEnabled_dictText'
          },
          {
            title:'备注',
            align:"left",
            ellipsis: true,
            dataIndex: 'remark'
          },
          {
            title:'创建时间',
            width:100,
            align:"center",
            dataIndex: 'createTime',
            customRender: t => !t ? "" : (t.length > 10 ? t.substr(0, 10) : t)
          },
          {
            title:'创建人',
            width:100,
            align:"center",
            dataIndex: 'createBy_dictText'
          },
          {
            title:'修改时间',
            width:100,
            align:"center",
            dataIndex: 'updateTime',
            customRender: t => !t ? "" : (t.length > 10 ? t.substr(0, 10) : t)
          },
          {
            title:'修改人',
            width:100,
            align:"center",
            dataIndex: 'updateBy_dictText'
          },
          {
            title: '操作',
            dataIndex: 'action',
            fixed: "right",
            width:120,
            align:"center",
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          list: "/base/basMaterial/list",
          delete: "/base/basMaterial/delete",
          deleteBatch: "/base/basMaterial/deleteBatch",
          exportXlsUrl: "/base/basMaterial/exportXls",
          importExcelUrl: "base/basMaterial/importExcel",
        },
        dictOptions:{},
      }
    },

     methods: {
       initDictConfig(){
       },

       //20250408 cfm add
       getImage: function (image) {
         if (!image || image.length === 0) return;
         return getFileAccessHttpUrl(image.split(',')[0]);
       },

    }
  }
</script>

<style lang="less" scoped>
  @import '~@assets/less/common.less';
  @import '../common/less/List.less';
</style>
