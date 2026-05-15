<template>
  <a-spin :spinning="confirmLoading">
    <a-form-model ref="form" :model="model" :rules="validatorRules">
      <a-row>
        <a-col :span="12">
          <a-form-model-item label="独立需求号" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="demandNo">
            <a-input v-model="model.demandNo" placeholder="系统自动生成或手动输入" :disabled="disabled || isEdit"/>
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
          <a-form-model-item label="需求公司" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="company">
            <a-input v-model="model.company" placeholder="请输入" :disabled="disabled"/>
          </a-form-model-item>
        </a-col>
      </a-row>

      <a-row>
        <a-col :span="12">
          <a-form-model-item label="需求工厂" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="factory">
            <a-input v-model="model.factory" placeholder="请输入" :disabled="disabled"/>
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
          <a-form-model-item label="需求供应商" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="supplierName">
            <a-input v-model="model.supplierName" placeholder="请输入" :disabled="disabled"/>
          </a-form-model-item>
        </a-col>
      </a-row>

      <a-row>
        <a-col :span="12">
          <a-form-model-item label="需求客户" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="customerName">
            <a-input v-model="model.customerName" placeholder="请输入" :disabled="disabled"/>
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
          <a-form-model-item label="需求外部编号" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="externalNo">
            <a-input v-model="model.externalNo" placeholder="请输入" :disabled="disabled"/>
          </a-form-model-item>
        </a-col>
      </a-row>

      <a-row>
        <a-col :span="12">
          <a-form-model-item label="需求状态" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="demandStatus">
            <j-dict-select-tag v-model="model.demandStatus" dictCode="demand_status" placeholder="请选择" :disabled="disabled"/>
          </a-form-model-item>
        </a-col>
      </a-row>

      <a-row>
        <a-col :span="24">
          <a-form-model-item label="备注" :labelCol="{span: 3}" :wrapperCol="{span: 21}" prop="remark">
            <a-textarea v-model="model.remark" placeholder="请输入备注" :rows="3" :disabled="disabled"/>
          </a-form-model-item>
        </a-col>
      </a-row>
    </a-form-model>
  </a-spin>
</template>

<script>
  import { getAction, putAction, postAction } from '@/api/manage'

  export default {
    name: 'PsiIndependentDemandForm',

    props: {
      disabled: {
        type: Boolean,
        default: false
      },
      isEdit: {
        type: Boolean,
        default: false
      }
    },

    data() {
      return {
        confirmLoading: false,
        labelCol: { span: 6 },
        wrapperCol: { span: 18 },
        model: {
          demandNo: '',
          company: '',
          factory: '',
          supplierId: '',
          supplierName: '',
          customerId: '',
          customerName: '',
          externalNo: '',
          demandStatus: '0',
          remark: ''
        },
        validatorRules: {
          demandNo: [{ required: true, message: '请输入独立需求号!' }]
        },
        url: {
          add: '/psi/independentDemand/add',
          edit: '/psi/independentDemand/edit',
          queryById: '/psi/independentDemand/queryById'
        }
      }
    },

    methods: {
      initForm() {
        this.model = {
          demandNo: '',
          company: '',
          factory: '',
          supplierId: '',
          supplierName: '',
          customerId: '',
          customerName: '',
          externalNo: '',
          demandStatus: '0',
          remark: ''
        }
        if (this.$refs.form) {
          this.$refs.form.resetFields()
        }
      },

      loadData(id) {
        this.confirmLoading = true
        getAction(this.url.queryById, { id: id }).then(res => {
          if (res.success) {
            this.model = res.result
          } else {
            this.$message.warning(res.message)
          }
        }).finally(() => {
          this.confirmLoading = false
        })
      },

      submitForm() {
        return new Promise((resolve, reject) => {
          this.$refs.form.validate(valid => {
            if (valid) {
              this.confirmLoading = true
              const action = this.isEdit ? putAction : postAction
              const url = this.isEdit ? this.url.edit : this.url.add

              if (this.isEdit) {
                this.model.id = this.model.id
              }
             
              action(url, this.model).then(res => {
                if (res.success) {
                  resolve(res.result)
                } else {
                  this.$message.warning(res.message)
                  reject(res)
                }
              }).finally(() => {
                this.confirmLoading = false
              })
            } else {
              reject(new Error('表单验证失败'))
            }
          })
        })
      }
    }
  }
</script>
