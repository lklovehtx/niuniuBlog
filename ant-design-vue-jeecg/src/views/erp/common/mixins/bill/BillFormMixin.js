import { getAction, putAction } from '@/api/manage'
import {} from "../../utils/util"; //执行Date.prototype.format等

export const BillFormMixin = {
  props: {//表单禁用
    disabled: {
      type: Boolean,
      default: false,
      required: false
    }
  },

  data() {
    return {
      action: '',
      moreStatus: false,
      moreStatus2: false,
      hasBarcode: window.location.href.endsWith("/1"),
      isCalcOutCost: true,

      //20250218 cfm add for 内置BPM
      srcVisible: true, //是否显示源单
    }
  },

  methods: {
    //20250618 cfm add for 内置BPM
    /**
     * 显示单据
     * @param bill 来自BPM的流程变量，无相关的源单数据
     */
    viewBill(bill) {
      this.srcVisible = false;
      this.visible = true;
      this.activeKey = this.refKeys[0];
      this.$refs.form.resetFields();

      this.model = bill;
      if (!!this.entryTable) this.entryTable.dataSource = bill.entryList;
      if (!!this.singleTable) this.singleTable.dataSource = bill.singleList;
    },

    //20250207 cfm add for 内置BPM
    /**
     * 加载单据
     * @param billId 从业务应用后端加载表单数据，包括相关的源单数据
     */
    loadBill(billId) {
      getAction(this.url.queryById, {id: billId}).then(res => {
        if(res.success){
          this.model = res.result;
          this.edit(this.model);
        }else{
          this.$message.warning(res.message)
        }
      });
    },

    handleSave() {
      if (this.validateHFSubForm()) {
        this.switchUrl('save')
        this.handleOk()
      }
    },

    handleSubmit() {
      if (this.validateHFSubForm()) {
        this.switchUrl('submit')
        this.handleOk()
      }
    },

    //校验BillHeader和BillFooter子表单
    validateHFSubForm() {
      let ok = true;
      if (this.$refs.billHeader) {
        this.$refs.billHeader.$refs.form.validate((valid, obj) => {
          ok = valid;
        });
        if (!ok) return false;
      }

      if (this.$refs.billFooter) {
        this.$refs.billFooter.$refs.form.validate((valid, obj) => {
          ok = valid;
        });
      }
      return ok;
    },

    handleCheck() {
      if (!this.model.approvalResultType || this.model.approvalResultType.length === 0 ) {
        this.$warning({ title: '审核', content: "请选择核批结果！" });
        return;
      }
      const that = this;
      putAction(that.url.check,
        {
          id: that.model.id,
          approvalResultType: that.model.approvalResultType,
          approvalRemark: that.model.approvalRemark
        }).then((res) => {
        if (res.success) {
          that.$message.success(res.message);
          that.$emit('ok')
          that.close()
        } else {
          that.$warning({title: '审核单据', content: res.message});
        }
      })
    },

    handleEbpm() {
      if (!this.model.approvalResultType || this.model.approvalResultType.length === 0 ) {
        this.$warning({ title: '审核', content: "请选择核批结果！" })
        return;
      }
      const that = this;
      putAction(that.url.ebpm,
        {
          id: that.model.id,
          approvalResultType: that.model.approvalResultType,
          approvalRemark: that.model.approvalRemark
        }).then((res) => {
        if (res.success) {
          that.$message.success(res.message);
          that.$emit('ok')
          that.close()
        } else {
          that.$warning({title: '结束审批流程', content: res.message});
        }
      })
    },

    handleExecute() {
      const that = this;
      putAction(that.url.execute, {id: that.model.id}).then((res) => {
        if (res.success) {
          that.$message.success(res.message);
          that.$emit('ok')
          that.close()
        } else {
          that.$warning({title: '执行单据', content: res.message});
        }
      })
    },


    handleVoid() {
      const that = this;
      putAction(that.url.void, {id: that.model.id}).then((res) => {
        if (res.success) {
          that.$message.success(res.message);
          that.$emit('ok')
          that.close()
        } else {
          that.$warning({title: '作废单据', content: res.message});
        }
      })
    },

    switchUrl(target) {
      if ((this.url._add || '') === '') {
        this.url._add = this.url.add
      }
      if ((this.url._edit || '') === '') {
        this.url._edit = this.url.edit
      }
      this.url.add = this.url._add + '/' + target
      this.url.edit = this.url._edit + '/' + target
    },

    onOperatorChange(val) {
      if (this.$refs.operatorFmi) this.$refs.operatorFmi.onFieldChange();
      setTimeout(()=>{
        const dictOptions = this.$refs.opDept.dictOptions;
        this.model.opDept = dictOptions.length === 1 ? dictOptions[0].value : '';
        if (this.$refs.opDeptFmi) this.$refs.opDeptFmi.onFieldChange();
      }, 1000);
    },

  }
 }

