<template>
  <a-card :bordered="false">
    <a-spin :spinning="loading">
      <div style="margin-top: 24px">
        <a-form-model ref="form" :model="model" :rules="validatorRules">
          <a-form-model-item label="起用年度" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="beginYear">
            <j-year-picker placeholder="请选择" v-model="model.beginYear" :allow-clear="false"/>
          </a-form-model-item>
          <a-form-model-item label="起用月度" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="beginMonth">
            <a-select placeholder="请选择" v-model="model.beginMonth" :allow-clear="false"
                      :options="[{value:1,label:1},{value:2,label:2},{value:3,label:3},{value:4,label:4},
                        {value:5,label:5},{value:6,label:6},{value:7,label:7},{value:8,label:8},
                        {value:9,label:9},{value:10,label:10},{value:11,label:11},{value:12,label:12}]"/>
          </a-form-model-item>
          <a-form-model-item :wrapperCol="{span: 24}" style="text-align: center">
            <a-button @click="handleOk" type="primary" >确定</a-button>
            <a-button @click="handleCancel" style="margin-left: 24px">取消</a-button>
          </a-form-model-item>
          <a-form-model-item :wrapperCol="{span: 24}" style="text-align: center">
            <a-textarea v-model="helpMsg" :read-only="true"  :auto-size="{minRows: 5, maxRows: 5}" style="width:540px; border: none"/>
          </a-form-model-item>
        </a-form-model>
      </div>
    </a-spin>
  </a-card>
</template>

<script>
  import { getAction, putAction } from '@/api/manage'
  import JYearPicker from '../common/components/JYearPicker'

  export default {
    name: "BasBizPeriod",
    inject:['closeCurrent'],//关闭当前modal,参考：src/views/jeecg/SelectDemo.vue
    components: {JYearPicker},
    data () {
      return {
        model: {beginYear:'', beginMonth: ''},
        validatorRules: {
          beginYear: [{ required: true, message: '请输入起用年度!'},],
          beginMonth: [{ required: true, message: '请输入起用月度!'},],
        },

        helpMsg: '说明\n' +
          '· 如果使用单据初始化结余，则起用月度比实际起用月度提前一个月。\n' +
          '· 如：从2023年1月开始使用本系统，则设置起用月度为2022年12月。\n' +
          '· 初始化结余内容：即时库存结余、库存月末结余、应收月末结余、应付月末结余。\n' +
          '· 初始化结余的单据：其他入库单、其他应收单、其他应付单。',

        labelCol: {
          xl: { span: 10 },
          lg: { span: 10 },
          md: { span: 10 },
          sm: { span: 10 },
          xs: { span: 24 },
        },
        wrapperCol: {
          xl: { span: 5 },
          lg: { span: 6 },
          md: { span: 8 },
          sm: { span: 8 },
          xs: { span: 24 },
        },

        loading: false,
        disabled: false,
        url: {
          query: "/base/basBizPeriod/query",
          edit: "/base/basBizPeriod/edit",
        }
      }
    },

    created () {
      this.loading = true;
      getAction(this.url.query, {}).then(res => {
        if (res.success) {
          this.model.id = res.result.id;
          this.model.beginYear = res.result.beginYear.toString();
          this.model.beginMonth = res.result.beginMonth.toString();
        }
        else{
          this.$message.warning(res.message)
        }
      }).finally(() => {
        this.loading = false
      })
    },

    methods: {
      handleOk() {
        const that = this;
        // 触发表单验证
        this.$refs.form.validate(valid => {
          if (valid) {
            that.$confirm({
              title:'起用月度设置',
              content: `起用月度设为${that.model.beginYear}年${that.model.beginMonth}月, 确定？`,
              onOk: () => that.submit(),
            });
          }else{
            return false
          }
        })
      },

      submit() {
        this.loading = true;
        this.model.year = this.model.beginYear;
        this.model.month = this.model.beginMonth;
        putAction(this.url.edit, this.model).then((res)=>{
          if(res.success){
            this.$store.commit('SET_BIZ_PERIOD', this.model);
            this.$info({
              title: '起用月度设置',
              content: '起用月度设置成功！',
              okText: '确定',
              onOk: () => this.closeCurrent(),
            });
          }else{
            this.$warning({
              title: '起用月度设置',
              content: res.message,
            });
          }
        }).finally(() => {
          this.loading = false;
        })
      },

      handleCancel () {
        this.closeCurrent();
      },

    }
  }
</script>