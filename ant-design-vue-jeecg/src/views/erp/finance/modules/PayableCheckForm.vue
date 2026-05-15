<template>
  <a-spin :spinning="confirmLoading">
    <!-- 主表单区域 -->
    <a-form-model ref="form" :model="model" :rules="validatorRules">
      <bill-header ref="billHeader" :model="model" :disabled="disabled" :moreStatus.sync="moreStatus" :moreStatus2.sync="moreStatus2"/>

      <a-row>
        <a-col :xl="8" :lg="12" :md="24">
          <a-form-model-item label="供应商" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="supplierId">
            <a-tooltip :title="!disabled && entryCount1+entryCount2>0 ? '有核销明细时不能改变！':''" placement="bottom">
              <j-search-select-tag v-model="model.supplierId" dict="bas_supplier,aux_name,id"
                                   @change="onSupplierChange" :disabled="disabled || entryCount1+entryCount2>0" />
            </a-tooltip>
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

      <!-- 子表区域 -->
      <a-divider v-if="!disabled" style="margin:12px 0 2px 0;"/>
      <split-pane v-if="!disabled" :min-percent='35' :default-percent='50' split="vertical"
                  :style="'width:100%; height:' + splitPaneHeight + 'px'">
        <template slot="paneL">
          <a-row>
            <a-col span="21">
              <br>
              <rp-check-payable-list
               ref="rpCheckPayableList"
               :checkEntryTableRefs="$refs"
               checkEntryTableRefKey="rpCheckEntryTable1"
               :style="'height:'+payableHeight+'px'"
               :rowCount.sync="payableCount"
               :selectedRowCount.sync="selectedPayableCount"/>
            </a-col>

            <a-col span="3">
              <a-button :disabled="selectedPayableCount===0"
                        type="link" @click="handleEntryList1Add" icon="right"
                        style="margin-top: 80px">添加</a-button>
              <a-button :disabled="selectedEntryCount1===0"
                        type="link" @click="handleEntryList1Remove" icon="left"
                        style="margin-top: 10px">移除</a-button>
            </a-col>
          </a-row>

          <a-row>
            <a-col span="21">
              <rp-check-payment-list
               ref="rpCheckPaymentList"
               :checkEntryTableRefs="$refs"
               checkEntryTableRefKey="rpCheckEntryTable2"
               :rowCount.sync="paymentCount"
               :selectedRowCount.sync="selectedPaymentCount"/>
            </a-col>

            <a-col span="3">
              <a-button :disabled="selectedPaymentCount===0"
                        type="link" @click="handleEntryList2Add" icon="right"
                        style="margin-top: 55px">添加</a-button>
              <a-button :disabled="selectedEntryCount2===0"
                        type="link" @click="handleEntryList2Remove" icon="left"
                        style="margin-top: 10px">移除</a-button>
            </a-col>
          </a-row>
        </template>
        <template slot="paneR">
          <a-icon type="column-width" />

          <!--20240404 cfm modi: checkType="101" -> checkType="2" -->
          <rp-check-entry-table
           ref="rpCheckEntryTable1"
           checkType="2"
           checkSide="1"
           :disabled="disabled"
           :style="'height:'+payableHeight+'px'"
           :totalAmt.sync="totalAmt1"
           :selectedRowCount.sync="selectedEntryCount1"
           :rowCount.sync="entryCount1"/>

          <rp-check-entry-table
           ref="rpCheckEntryTable2"
           checkType="2"
           checkSide="2"
           :disabled="disabled"
           :totalAmt.sync ="totalAmt2"
           :selectedRowCount.sync="selectedEntryCount2"
           :rowCount.sync="entryCount2"/>

          <a-alert v-if="totalAmt1!==totalAmt2" message="上下核销金额合计不相等！" type="warning" banner />
        </template>
      </split-pane>
      <template v-else>
        <rp-check-entry-table
         ref="rpCheckEntryTable1"
         checkType="2"
         checkSide="1"
         style="margin-top: 20px"
         :disabled="disabled"
         :totalAmt.sync ="totalAmt1"/>

        <!--20240404 cfm modi: checkType="101" -> checkType="2" -->
        <rp-check-entry-table
         ref="rpCheckEntryTable2"
         checkType="2"
         checkSide="2"
         style="margin-top: 20px"
         :disabled="disabled"
         :totalAmt.sync ="totalAmt2"/>

        <a-alert v-if="totalAmt1!==totalAmt2" message="上下核销金额合计不相等！" type="warning" banner />
      </template>

      <a-divider v-if="action==='check' || action==='ebpm'" style="margin:36px 0 0 0;"/>
      <bill-approval v-if="action==='check' || action==='ebpm'" :model="model" :disabled="disabled" style="margin-top: 16px"/>
    </a-form-model>
  </a-spin>
</template>

<script>
  import {getRefPromise} from '@/components/jeecg/JVxeTable/utils/vxeUtils.js'
  import {JVxeTableModelMixin} from '@/mixins/JVxeTableModelMixin'
  import {BillFormMixin} from '../../common/mixins/bill/BillFormMixin'
  import {BillFormGridMixin} from '../../common/mixins/bill/BillFormGridMixin'
  import {DetailMixin} from '../../common/mixins/bill/DetailMixin'
  import BillHeader from "../../common/components/BillHeader"
  import BillApproval from "../../common/components/BillApproval"
  import splitPane from 'vue-splitpane';
  import RpCheckEntryTable from "./RpCheck/RpCheckEntryTable";
  import RpCheckPayableList from "./RpCheck/RpCheckPayableList";
  import RpCheckPaymentList from "./RpCheck/RpCheckPaymentList";
  import { getAction } from '@api/manage'

  export default {
    name: 'PayableCheckForm',
    mixins: [JVxeTableModelMixin, BillFormMixin, BillFormGridMixin, DetailMixin],
    components: {BillHeader, BillApproval, splitPane, RpCheckPayableList, RpCheckPaymentList, RpCheckEntryTable},

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
          payableCheckType: '2',//应付核销
        },

        validatorRules: {
          supplierId: [{required: true, message: '请输入供应商!'}],
        },

        addDefaultRowNum: 0,
        refKeys:[], //JvxeTableModelMinix中用到

        totalAmt1: 0,
        entryCount1: 0,
        payableCount: 0,
        selectedEntryCount1: 0,
        selectedPayableCount: 0,
        totalAmt2:0,
        entryCount2: 0,
        paymentCount: 0,
        selectedEntryCount2: 0,
        selectedPaymentCount: 0,

        url: {
          add: "/finance/finPayableCheck/add",
          edit: "/finance/finPayableCheck/edit",
          check: "/finance/finPayableCheck/check",
          ebpm: "/finance/finPayableCheck/bpm/end",
          execute: "/finance/finPayableCheck/execute",
          void: "/finance/finPayableCheck/void",
          finRpCheckEntry: {
            list: '/finance/finPayableCheck/queryEntryByMainId',
          },
          queryById: "/finance/finPayableCheck/queryById", //20251101 cfm add for 内置BPM
        },
      }
    },

    watch: {
      // begin-20240822 cfm modi
      // totalAmt1() {
      //   this.$emit('update:canSubmit', this.canSubmit);
      // },
      // totalAmt2() {
      //   this.$emit('update:canSubmit', this.canSubmit);
      // }
      totalAmt1: {
        immediate: true,
        handler() {
          this.$emit('update:canSubmit', this.canSubmit);
        }
      },
      totalAmt2: {
        immediate: true,
        handler() {
          this.$emit('update:canSubmit', this.canSubmit);
        }
      },
      entryCount1: {
        immediate: true,
        handler() {
          this.$emit('update:canSubmit', this.canSubmit);
        }
      },
      entryCount2: {
        immediate: true,
        handler() {
          this.$emit('update:canSubmit', this.canSubmit);
        }
      },
      // end-20240822 cfm modi
    },

    computed: {
      /**
       * @return {number}
       */
      payableHeight() {
        let a = this.payableCount === 0 ? 5:this.payableCount+3.5,
            b = this.entryCount1 === 0 ? 5:this.entryCount1+3.5;
        return (a > b ? a : b) * 38;
      },

      splitPaneHeight() {
        let a = this.paymentCount === 0 ? 5:this.paymentCount+3.5,
          b = this.entryCount2 === 0 ? 5:this.entryCount2+3.5;
        return this.payableHeight + (a > b ? a : b) * 38;
      },

      canSubmit(){
        return this.entryCount1+this.entryCount2!==0 && this.totalAmt1===this.totalAmt2;
      }
    },

    methods: {
      addBefore(){
        this.$nextTick(() => {
          this.$refs.rpCheckEntryTable1.dataSource = [];
          this.$refs.rpCheckEntryTable2.dataSource = [];
        });
      },

       addAfter() {
        this.$refs.billHeader.fillBillNo('fin_yfhx_bill_no');
      },

      getAllTable() {
        let values = [
          getRefPromise(this.$refs.rpCheckEntryTable1, 'table'),
          getRefPromise(this.$refs.rpCheckEntryTable2, 'table')];
        return Promise.all(values)
      },

      editBefore() {
        this.entryCount1 = 0;
        this.selectedEntryCount1= 0;
        this.selectedPayableCount = 0;
        this.entryCount2 = 0;
        this.selectedEntryCount2 = 0;
        this.selectedPaymentCount = 0;
        if (!this.disabled) {
          this.$nextTick(() => {
            this.$refs.rpCheckPayableList.init();
            this.$refs.rpCheckPaymentList.init();
          });
        }
      },

      editAfter() {
        // 加载子表数据
        this.$nextTick(() => { if (this.model.id) requestSubTableData(this); });

        function requestSubTableData(that) {
          const table1 = that.$refs.rpCheckEntryTable1,
                table2 = that.$refs.rpCheckEntryTable2;

          //核销明细
          let url = that.url.finRpCheckEntry.list;
          let params = { id: that.model.id };
          table1.loading = true;
          table2.loading = true;
          that.$emit("update:loading", true);
          getAction(url, params).then(res => {
            let rows1 = [], rows2 = [];
            for (let row of res.result || []) {
              if (row.checkSide === "1")
                rows1.push(row)
              else
                rows2.push(row);
            }
            table1.dataSource = rows1;
            table2.dataSource = rows2;
            //查询应付单、付款单
            that.onSupplierChange(that.model.supplierId);
          }).finally(() => {
            table1.loading = false;
            table2.loading = false;
            that.$emit("update:loading", false);
          });
        }
      },

      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
          finPayableCheckEntryList:
            this.$refs.rpCheckEntryTable1.tableData.concat(this.$refs.rpCheckEntryTable2.tableData),
        }
      },

      onSupplierChange(val){
        if (!this.disabled && val) {
          let params = {supplierId: val};
          this.$refs.rpCheckPayableList.queryParam = params;
          this.$refs.rpCheckPayableList.loadData(1);
          this.$refs.rpCheckPaymentList.queryParam = params; //20241015 cfm add
          this.$refs.rpCheckPaymentList.loadData(1);
        }
      },

      handleEntryList1Add() {
        const list = this.$refs.rpCheckPayableList,
              table = this.$refs.rpCheckEntryTable1;

        let maxEntryNo = 100; //分录号最小为101
        for(let row of table.tableData) {
          if (maxEntryNo < row.entryNo)  maxEntryNo = row.entryNo;
        }
        if (maxEntryNo >= 199) {
          this.$warning({title: "应付核销单", content: "应付核销明细最多99条！"});
          return;
        }

        let rows = [];
        for (let row of list.selectionRows) {
          rows.push({
            entryNo: ++maxEntryNo,
            checkSide: "1",
            srcBillType: row.billType,
            srcBillId: row.id,
            srcNo: row.billNo,
            uncheckedAmt: row.uncheckedAmt,
            amt: row.uncheckedAmt
          });
        }
        table.addRows(rows);

        list.dataSource = [...list.dataSource];
        list.selectedRowKeys = [];
        list.selectionRows = [];
        this.selectedPayableCount = 0;
      },

      handleEntryList1Remove() {
        const table = this.$refs.rpCheckEntryTable1,
              list = this.$refs.rpCheckPayableList;

        table.removeSelectedRows(table.$refs.table.selectedRows);
        list.dataSource = [...list.dataSource];
      },

      handleEntryList2Add() {
        const list = this.$refs.rpCheckPaymentList,
              table = this.$refs.rpCheckEntryTable2;

        let maxEntryNo = 200;//分录号最小为201
        for(let row of table.tableData) {
          if (maxEntryNo < row.entryNo)  maxEntryNo = row.entryNo;
        }

        let rows = [];
        for (let row of list.selectionRows) {
           rows.push({
            entryNo: ++maxEntryNo,
            checkSide: "2",
            srcBillType: row.billType,
            srcBillId: row.id,
            srcNo: row.billNo,
            uncheckedAmt: row.uncheckedAmt,
            amt: row.uncheckedAmt
          });
        }
        table.addRows(rows);

        list.dataSource = [...list.dataSource];
        list.selectedRowKeys = [];
        list.selectionRows = [];
        this.selectedPaymentCount = 0;
      },

      handleEntryList2Remove() {
        const list = this.$refs.rpCheckPaymentList,
              table = this.$refs.rpCheckEntryTable2;

        table.removeSelectedRows(table.$refs.table.selectedRows);
        list.dataSource = [...list.dataSource];
      },

      handleMySubmit() {
        if (this.totalAmt1 !== this.totalAmt2)
          this.$warning({title: "应付核销提交", content: "无核销或上下合计不相等，不能提交！"});
        else
          this.handleSubmit();
      },

    }
  }
</script>

<style lang="less" scoped>
  @import "../../common/less/BillForm.less";
</style>
