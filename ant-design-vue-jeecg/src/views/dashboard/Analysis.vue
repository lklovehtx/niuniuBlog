<template>
  <div>
    <a-row :gutter="[8, 8]">
      <a-col :xl="12" :lg="12" :md="24" :sm="24" :xs="24">
        <a-card title="业务概况" :loading="summary.loading" :bordered="false" >
          <template #extra>
            <a-button type="link" size="small" @click="loadReport(summary)" icon="reload"/>
          </template>
          <a-row>
            <template v-for="rec in summary.dataSource">
              <a-col v-if="isMobile()" :span="24">
                <head-info :title="rec.label" :content="formatAmt(rec.value)" :center="true" style="margin-bottom: 16px"/>
              </a-col>
              <a-col v-else :span="rec.width">
                <head-info :title="rec.label" :content="formatAmt(rec.value)" :center="true" />
              </a-col>
            </template>
          </a-row>
        </a-card>
      </a-col>
      <a-col :xl="6" :lg="6" :md="12" :sm="24" :xs="24">
        <a-card title="客户" :loading="customer.loading" :bordered="false" >
          <template #extra>
            <a-button type="link" size="small" @click="loadReport(customer)" icon="reload"/>
          </template>
          <a-row>
            <template v-for="rec in customer.dataSource">
              <a-col :span="rec.width">
                <head-info :title="rec.label" :content="formatInt(rec.value)" :center="true" />
              </a-col>
            </template>
          </a-row>
        </a-card>
      </a-col>
      <a-col :xl="6" :lg="6" :md="12" :sm="24" :xs="24">
        <a-card title="供应商" :loading="supplier.loading" :bordered="false" >
          <template #extra>
            <a-button type="link" size="small" @click="loadReport(supplier)" icon="reload"/>
          </template>
          <a-row>
            <template v-for="rec in supplier.dataSource">
              <a-col :span="rec.width">
                <head-info :title="rec.label" :content="formatInt(rec.value)" :center="true" />
              </a-col>
            </template>
          </a-row>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="[8, 8]">
      <a-col :xl="12" :lg="24" :md="24" :sm="24" :xs="24">
        <a-row :gutter="[8, 8]">
          <a-col span="24">
            <a-card title="销售" :loading="sale.loading" :bordered="false" :body-style="{padding: 0}">
              <template #extra>
                <a-button type="link" size="small" @click="loadReport(sale)" icon="reload"/>
              </template>
              <a-row>
                <template v-for="rec in sale.dataSource">
                  <a-col :xl="12" :lg="12" :md="12" :sm="24" :xs="24">
                    <a-card-grid style="width: 100%" :bordered="true" :body-style="{padding: '0'}">
                      <h4>{{rec.label}}</h4>
                      <a-row>
                        <a-col :span="10" :offset="1">
                          <head-info title="今日笔数" :content="formatInt(rec.t_count)" :center="false"/>
                          <div style="margin-top: 16px">本周：{{formatInt(rec.w_count)}}</div>
                          <div>本月：{{formatInt(rec.m_count)}}</div>
                        </a-col>
                        <a-col :span="13">
                          <head-info title="今日金额" :content="formatAmt(rec.t_amt)" :center="false"/>
                          <div style="margin-top: 16px">本周：{{formatAmt(rec.w_amt)}}</div>
                          <div>本月：{{formatAmt(rec.m_amt)}}</div>
                        </a-col>
                      </a-row>
                    </a-card-grid>
                  </a-col>
                </template>
              </a-row>
            </a-card>
          </a-col>

          <a-col span="24">
            <a-card title="采购" :loading="purchase.loading" :bordered="false" :body-style="{padding: 0}">
              <template #extra>
                <a-button type="link" size="small" @click="loadReport(purchase)" icon="reload"/>
              </template>
              <a-row>
                <template v-for="rec in purchase.dataSource">
                  <a-col :xl="12" :lg="12" :md="12" :sm="24" :xs="24">
                    <a-card-grid style="width: 100%" :bordered="true" :body-style="{padding: '0'}">
                      <h4>{{rec.label}}</h4>
                      <a-row>
                        <a-col :span="10" :offset="1">
                          <head-info title="今日笔数" :content="formatInt(rec.t_count)" :center="false"/>
                          <div style="margin-top: 16px">本周：{{formatInt(rec.w_count)}}</div>
                          <div>本月：{{formatInt(rec.m_count)}}</div>
                        </a-col>
                        <a-col :span="13">
                          <head-info title="今日金额" :content="formatAmt(rec.t_amt)" :center="false"/>
                          <div style="margin-top: 16px">本周：{{formatAmt(rec.w_amt)}}</div>
                          <div>本月：{{formatAmt(rec.m_amt)}}</div>
                        </a-col>
                      </a-row>
                    </a-card-grid>
                  </a-col>
                </template>
              </a-row>
            </a-card>
          </a-col>
        </a-row>
      </a-col>

      <a-col :xl="6" :lg="12" :md="12" :sm="24" :xs="24">
        <a-row :gutter="[8, 8]">
          <a-col :span="24">
            <a-card title="销售金额" :loading="saleAmt.loading" :bordered="false" :body-style="{padding: '0'}">
              <template #extra>
                <a-button type="link" size="small" @click="loadReport(saleAmt)" icon="reload"/>
              </template>
              <bar :dataSource="saleAmt.dataSource" :height="124" style="padding: 0 8px 0 8px"/>
            </a-card>
          </a-col>
          <a-col :span="24">
            <a-card title="毛利润" :loading="saleProfit.loading" :bordered="false" :body-style="{padding: '0'}">
              <template #extra>
                <a-button type="link" size="small" @click="loadReport(saleProfit)" icon="reload"/>
              </template>
              <bar :dataSource="saleProfit.dataSource" :height="124" style="padding: 0 8px 0 8px"/>
            </a-card>
          </a-col>
          <a-col :span="24">
            <a-card title="采购金额" :loading="purchaseAmt.loading" :bordered="false" :body-style="{padding: '0'}">
              <template #extra>
                <a-button type="link" size="small" @click="loadReport(purchaseAmt)" icon="reload"/>
              </template>
              <bar :dataSource="purchaseAmt.dataSource":height="124" style="padding: 0 8px 0 8px"/>
            </a-card>
          </a-col>
          <a-col :span="24">
            <a-card title="库存月度结存金额" :loading="stockBalCost.loading" :bordered="false" :body-style="{padding: '0'}">
              <template #extra>
                <a-button type="link" size="small" @click="loadReport(stockBalCost)" icon="reload"/>
              </template>
              <bar :dataSource="stockBalCost.dataSource" :height="124" style="padding: 0 8px 0 8px"/>
            </a-card>
          </a-col>
        </a-row>
      </a-col>

      <a-col :xl="6" :lg="12" :md="12" :sm="24" :xs="24">
        <a-row :gutter="[8, 8]">
          <a-col :span="24">
            <a-card title="处理中主要单据" :loading="doingBill.loading" :bordered="false" :body-style="{padding: 0}" style="min-height: 824px">
              <template #extra>
                <a-button type="link" size="small" @click="loadReport(doingBill)" icon="reload"/>
              </template>
              <a-table
                size="small"
                :bordered="false"
                rowKey="name"
                :scroll="{x: true}"
                :columns="doingBill.columns"
                :dataSource="doingBill.dataSource"
                :pagination="false"
                class="j-table-force-nowrap"/>
            </a-card>
          </a-col>
        </a-row>
      </a-col>
    </a-row>
  </div>
</template>

<script>
  import Bar from '@/components/chart/Bar'
  import HeadInfo from '@/components/tools/HeadInfo'
  import { getAction} from '@/api/manage'
  import { mixinDevice } from '@/utils/mixin.js'
  import XEUtils from "xe-utils";

  export default {
    name: "Analysis",
    mixins: [ mixinDevice ],
    components: {HeadInfo, Bar},

    data() {
      return {
        //20260203 cfm modi：下面各部分的 rowCount 改为 ipagination
        summary: {
          cgreportId: '1579848144423751681',
          loading: false,
          queryParam: {},
          ipagination: { current: 1, pageSize: 3, total: 0 },
          isorter: { column: null, order: null },
          dictOptions: {},
          dataSource: [],
        },

        customer: {
          cgreportId: '1580740609854935042',
          loading: false,
          queryParam: {},
          ipagination: { current: 1, pageSize: 4, total: 0 },
          isorter: { column: null, order: null },
          dictOptions: {},
          dataSource: [],
        },

        supplier: {
          cgreportId: '1580740795561938946',
          loading: false,
          queryParam: {},
          ipagination: { current: 1, pageSize: 4, total: 0 },
          isorter: { column: null, order: null },
          dictOptions: {},
          dataSource: [],
        },

        sale: {
          cgreportId: '1579758928603910145',
          loading: false,
          queryParam: {},
          ipagination: { current: 1, pageSize: 8, total: 0 },
          isorter: { column: null, order: null },
          dictOptions: {},
          dataSource: [],
        },

        purchase: {
          cgreportId: '1579760837406494722',
          loading: false,
          queryParam: {},
          ipagination: { current: 1, pageSize: 8, total: 0 },
          isorter: { column: null, order: null },
          dictOptions: {},
          dataSource: [],
        },

        saleAmt: {
          cgreportId: '1580915783778701314',
          loading: false,
          queryParam: {},
          ipagination: { current: 1, pageSize: 12, total: 0 },
          isorter: { column: null, order: null },
          dictOptions: {},
          dataSource: [],
        },

        saleProfit: {
          cgreportId: '1580918054310645761',
          loading: false,
          queryParam: {},
          ipagination: { current: 1, pageSize: 12, total: 0 },
          isorter: { column: null, order: null },
          dictOptions: {},
          dataSource: [],
        },

        purchaseAmt: {
          cgreportId: '1580918550912045057',
          loading: false,
          ipagination: { current: 1, pageSize: 12, total: 0 },
          isorter: { column: null, order: null },
          dictOptions: {},
          dataSource: [],
        },

        stockBalCost: {
          cgreportId: '1580923428333948929',
          loading: false,
          ipagination: { current: 1, pageSize: 12, total: 0 },
          isorter: { column: null, order: null },
          dictOptions: {},
          dataSource: [],
        },

        doingBill: {
          cgreportId: '1582194686237454338',
          loading: false,
          ipagination: { current: 1, pageSize: 100, total: 0 },
          isorter: { column: null, order: null },

          dictOptions: {},
          dataSource: [{class:'销售订单'},{class:'采购订单'},{class:'出库单'},{class:'入库单'}],
          columns: [
            {
              title:'',
              key: 'name',
              align:"center",
              dataIndex: 'name',
            },
            {
              title:'编制中',
              align:"center",
              dataIndex: 'edit',
            },
            {
              title:'待核批',
              align:"center",
              dataIndex: 'appr',
            },
            {
              title:'执行中',
              align:"center",
              dataIndex: 'exec',
            },
          ],
        }
      }
    },

    created() {
      this.loadReport(this.summary);
      this.loadReport(this.customer);
      this.loadReport(this.supplier);
      this.loadReport(this.sale);
      this.loadReport(this.purchase);
      this.loadReport(this.saleAmt);
      this.loadReport(this.saleProfit);
      this.loadReport(this.purchaseAmt);
      this.loadReport(this.stockBalCost);
      this.loadReport(this.doingBill);
    },

    methods: {
      //20260203 cfm modi
      loadReport(def, success) {
        let param = Object.assign({}, def.queryParam, def.isorter);
        param.pageNo = def.ipagination.current;
        param.pageSize = def.ipagination.pageSize;

        def.loading = true;
        getAction('/online/cgreport/api/getColumnsAndData/' + def.cgreportId, param).then(res => {
          if (!res.success) return;

          if (!!def.columns) {
            let { columns, dictOptions } = res.result;
            def.dictOptions = dictOptions || {};
            for(let i = 0; i < columns.length; i++){
              if(columns[i].customRender){
                let fieldName = columns[i].customRender;
                let column = def.columns.find(c => c.dataIndex === fieldName);
                if (!!column) column.customRender = (t => t ? filterMultiDictText(def.dictOptions[fieldName], t + '') : t);
              }
            }
          }
          def.dataSource =  res.result.data.records;

          typeof success === 'function' ? success() : ''
        }).finally(() => def.loading = false);
      },

      formatAmt(amt) {
        return XEUtils.commafy(amt, {digits: 2}).toString();
      },

      formatInt(i) {
        return XEUtils.commafy(i, {digits: 0}).toString();
      }
    }
  }
</script>

<style lang="less" scoped>
  /deep/ .ant-card-head {
    height: 52px;
  }
</style>