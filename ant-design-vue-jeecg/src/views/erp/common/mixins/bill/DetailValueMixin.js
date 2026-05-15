import {mapGetters} from "vuex";

export const DetailValueMixin = {

  methods: {
    ...mapGetters(["bizOptions"]),

    generateBatchNo(row) {
      let result;
      switch (this.bizOptions().batchMode) {
        case '0':
          result = '0';
          break;
        case '1':
          result = '';
          break;
        case '2':
        case '3':
          // 根据需要修改批次号的生成
          // result = this.model.billNo + "-" + row.entryNo;
          result =  new Date().format('yyyyMMdd');
          break;
      }

      return result;
    },

    convertUnit(qty, materialId, fromUnitId, toUnitId) {
      if (!qty || isNaN(qty) || fromUnitId === toUnitId) return qty; // !0 = true

      const rate = this.getUnitRate(materialId, fromUnitId, toUnitId);
      return !rate ? null : Number(qty) * rate;
    },

    calcTax(row) {
      let tax = row.qty * (row.price * row.discountRate/100) * Number(row.taxRate) / (100 + Number(row.taxRate));
      return tax.toFixed(2);
    },

    calcAmt(row) {
      let amt = row.qty * (row.price * row.discountRate/100);
      return amt.toFixed(2);
    },

    calcSettleTax(record, newValues) {
      if (!!newValues && Object.keys(newValues).length > 0) {
        const values = {...record};
        Object.keys(newValues).forEach(key => values[key] = newValues[key]);
        return this.calcSettleTax(values);
      }

      const settleQty = Number(record.settleQty), price = Number(record.price),
        discountRate = Number(record.discountRate), taxRate = Number(record.taxRate);
      const tax = settleQty * (price * discountRate / 100) * taxRate / (100 + taxRate);
      return Number(tax.toFixed(2));
    },

    calcSettleAmt(record, newValues) {
      if (!!newValues && Object.keys(newValues).length > 0) {
        const values = {...record};
        Object.keys(newValues).forEach(key => values[key] = newValues[key]);
        return this.calcSettleAmt(values);
      }

      const settleQty = Number(record.settleQty), price = Number(record.price), discountRate = Number(record.discountRate);
      let settleAmt = settleQty * (price * discountRate / 100);
      return Number(settleAmt.toFixed(2));
    },

    calcOutCost(record, newValues) {
      if (!!newValues && Object.keys(newValues).length > 0) {
        const values = {...record};
        Object.keys(newValues).forEach(key => values[key] = newValues[key]);
        return this.calcOutCost(values);
      }

      const rate = this.getUnitRate(record.materialId, record.unitId, record.inventoryUnitId);
      if (!rate) return null;

      const inventoryQty = Number(record.inventoryQty);
      if (isNaN(inventoryQty) || inventoryQty === 0) return 0;

      const inventoryCost = Number(record.inventoryCost), qty = Number(record.qty);
      const cost = inventoryCost * qty * rate / inventoryQty;
      return Number(cost.toFixed(2));
    },

  }
}