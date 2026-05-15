<template>
  <a-drawer
    :title="title"
    :width="1200"
    placement="right"
    :closable="disableSubmit"
    :maskClosable="false"
    @close="close"
    destroyOnClose
    :visible="visible">

    <a-tabs default-active-key="1" @change="tabChange">
      <a-tab-pane key="1" tab="基本信息">
        <psi-independent-demand-form ref="headerForm" :disabled="disableSubmit" :isEdit="isEdit"/>
      </a-tab-pane>
      <a-tab-pane key="2" tab="行项目" v-if="demandId">
        <psi-independent-demand-items ref="itemsForm" :demandId="demandId" :disabled="disableSubmit"/>
      </a-tab-pane>
    </a-tabs>

    <div class="drawer-footer">
      <a-button v-if="!disableSubmit" @click="handleOk" type="primary" style="margin-bottom: 0;">确定</a-button>
      <a-button @click="handleCancel" style="margin-bottom: 0;">{{ disableSubmit ? '关闭' : '取消' }}</a-button>
    </div>
  </a-drawer>
</template>

<script>
  import PsiIndependentDemandForm from './PsiIndependentDemandForm'
  import PsiIndependentDemandItems from './PsiIndependentDemandItems'

  export default {
    name: 'PsiIndependentDemandModal',
    components: {
      PsiIndependentDemandForm,
      PsiIndependentDemandItems
    },

    data() {
      return {
        title: '新增独立需求',
        visible: false,
        disableSubmit: false,
        confirmLoading: false,
        isEdit: false,
        demandId: null
      }
    },

    methods: {
      add() {
        this.title = '新增独立需求'
        this.demandId = null
        this.isEdit = false
        this.visible = true
        this.$nextTick(() => {
          this.$refs.headerForm.initForm()
        })
      },

      edit(record) {
        this.title = '编辑独立需求'
        this.demandId = record.id
        this.isEdit = true
        this.visible = true
        this.$nextTick(() => {
          this.$refs.headerForm.loadData(record.id)
        })
      },

      tabChange(key) {
        if (key === '2' && this.demandId) {
          this.$nextTick(() => {
            this.$refs.itemsForm.loadItems()
          })
        }
      },

      handleOk() {
        this.$refs.headerForm.submitForm().then(res => {
          if (res) {
            this.demandId = res.id
            if (this.$refs.itemsForm) {
              this.$refs.itemsForm.demandId = res.id
              this.$refs.itemsForm.submitItems()
            }
            this.$message.success('保存成功!')
            this.$emit('ok')
            this.close()
          }
        }).catch(err => {
          this.$message.error(err.message)
        })
      },

      handleCancel() {
        this.close()
      },

      close() {
        this.visible = false
        this.$emit('close')
      }
    }
  }
</script>

<style lang="less" scoped>
  .drawer-footer {
    position: absolute;
    bottom: 0;
    width: 100%;
    padding: 10px 16px;
    text-align: right;
    background: #fff;
    border-top: 1px solid #e8e8e8;
    z-index: 100;
  }
</style>
