/**
 * JvxeTable Validator
 */

import {stringIsEmpty} from "../../utils/util";
import XEUtils from "xe-utils";

export const DetailValidatorMixin = {
  methods: {
    //红蓝单都可使用
    rubricValidator({cellValue, row, column}, callback, target) {
      const v = Number(cellValue);
      if (isNaN(v)) {
        callback();
        return;
      }

      const isRubric = Number(this.model.isRubric);
      if (isRubric === 0 && v < 0) {
        callback(false, '蓝字单${title}不能为负数');
      } else if (isRubric === 1 && v > 0) {
        callback(false, '红字单${title}不能为正数');
      } else {
        callback(true); //true：通过验证
      }
    },

    //只能用于红单，计量单位与蓝单相同
    rubricRangeValidator({cellValue, row, column}, callback, target) {
      const isRubric = Number(this.model.isRubric);
      if (isNaN(isRubric) || isRubric === 0) {
        this.$warning({title: '校验器', content: 'rubricRangeValidator只能用于红字单据！'});
        callback();
        return;
      }

      const rubric = Number(cellValue);
      if (isNaN(rubric)) {
        callback();
        return;
      }

      if (rubric > 0) {
        callback(false, '红字单${title}不能为正数');
        return;
      }

      let rows1 = this.$refs.srcEntryTable.getTableData();
      rows1 = rows1.filter(row0 => row0.id === row.srcEntryId);
      let blue = rows1[0][column.key]
      if (isNaN(blue)) {
        callback();
        return;
      }
      if (-rubric > blue) {
        callback(false, '不能超出蓝字单！');
        return;
      }

      callback(true); //true：通过验证
    },

    taxValidator({cellValue, row, column}, callback, target) {
      const v = Number(cellValue);
      if (isNaN(v)) {
        callback();
        return;
      }

      let diff = v - this.calcTax(row);
      if (diff < -0.01001 || diff > 0.01001) {
        callback(false, '${title}的输入值与计算值相差超过0.01元！');
      } else {
        callback(true); //true：通过验证
      }
    },

    amtValidator({cellValue, row, column}, callback, target) {
      const v = Number(cellValue);
      if (isNaN(v)) {
        callback();
        return;
      }

      let diff = v - this.calcAmt(row);
      if (diff < -0.01001 || diff > 0.01001) {
        callback(false, '${title}的输入值与计算值相差超过0.01元！');
      } else {
        callback(true); //true：通过验证
      }
    },

    settleTaxValidator({cellValue, row, column}, callback, target) {
      const v = Number(cellValue);
      if (isNaN(v)) {
        callback();
        return;
      }

      let diff = v - this.calcSettleTax(row);
      if (diff < -0.01001 || diff > 0.01001) {
        callback(false, '${title}的输入值与计算值相差超过0.01元！');
      } else {
        callback(true); //true：通过验证
      }
    },

    settleAmtValidator({cellValue, row, column}, callback, target) {
      const v = Number(cellValue);
      if (isNaN(v)) {
        callback();
        return;
      }

      let diff = v - this.calcSettleAmt(row);
      if (diff < -0.01001 || diff > 0.01001) {
        callback(false, '${title}的输入值与计算值相差超过0.01元！');
      } else {
        callback(true); //true：通过验证
      }
    },

    //20240923 cfm add
    rubricInQtyValidator({cellValue, row, column}, callback, target) {
      let materialId = row.materialId, unitId = row.unitId, qty = Number(row.qty),
        inventoryUnitId = row.inventoryUnitId, inventoryQty = Number(row.inventoryQty);
      if (row.stockIoDirection!=='1' || stringIsEmpty(materialId) || stringIsEmpty(unitId) || isNaN(qty)) {
        callback();
        return;
      }

      if (stringIsEmpty(inventoryUnitId) || isNaN(inventoryQty)) {
        callback(false, '库存不存在！');
        return;
      }

      if (qty !==0 && inventoryQty !== 0 && unitId !== inventoryUnitId) {
        let rate = this.getUnitRate(materialId, unitId, inventoryUnitId);
        if (!rate) {
          callback(false, '出库单位或库存单位不合法！');
          return;
        }
        qty *= rate;
      }

      if (-qty > inventoryQty)
        callback(false, '不能超出库存数量！');
      else
        callback(true); //true：通过验证
    },

    //20240923 cfm modi: 增加库存不存在的校验
    outQtyValidator({cellValue, row, column}, callback, target) {
      if (this.disabled) return; //20250409 cfm add

      let materialId = row.materialId, unitId = row.unitId, qty = Number(row.qty),
        inventoryUnitId = row.inventoryUnitId, inventoryQty = isNaN(row.inventoryQty) ? 0 : Number(row.inventoryQty);
      if (row.stockIoDirection!=='2' || stringIsEmpty(materialId) || stringIsEmpty(unitId) || isNaN(qty)) {
        callback();
        return;
      }

      if (stringIsEmpty(inventoryUnitId) || isNaN(inventoryQty)) {
        callback(false, '库存不存在！');
        return;
      }

      if (qty !== 0 && inventoryQty !== 0 && unitId !== inventoryUnitId) {
        let rate = this.getUnitRate(materialId, unitId, inventoryUnitId);
        if (!rate) {
          callback(false, '出库单位或库存单位不合法！');
          return;
        }
        qty *= rate;
      }

      if (qty > inventoryQty)
        callback(false, '不能超出库存数量！');
      else
        callback(true); //true：通过验证
    },

    outCostValidator({cellValue, row, column}, callback, target) {
      const v = Number(cellValue);
      if (isNaN(v)) {
        callback();
        return;
      }

      if (v > Number(row.inventoryCost)) {
        callback(false, '${title}不能大于库存金额！');
      } else {
        callback(true); //true：通过验证
      }
    },

    moveQtyValidator({cellValue, row, column}, callback, target) {
      const v = Number(cellValue);
      if (!row.batchNo || row.batchNo.length===0 || isNaN(v)) {
        callback();
        return;
      }

      let rows;
      if (row.stockIoDirection==='2') {
        rows = this.$refs.entryTable.getTableData();
        rows = rows.filter(r => r.stockIoDirection==='2' && r.materialId===row.materialId && r.batchNo===row.batchNo);
        if (XEUtils.sum(rows, 'qty') > row.inventoryQty){
          callback(false, '该仓库批次的调出数量合计不能大于库存数量！');
          return;
        }
      }

      rows = this.$refs.entryTable.getTableData();
      rows = rows.filter(r => r.materialId === row.materialId && r.batchNo === row.batchNo);
      let qty = 0;
      for(let r of rows) {
        qty += r.stockIoDirection==='2' ? r.qty : -r.qty;
      }
      if (Math.abs(qty) > 0.001) {
        callback(false, '该物料批次的调出调入数量不相等！');
        return;
      }

      callback(true); //true：通过验证
    },

    uniqueSingleValidator({cellValue, row, column}, callback, target) {
      if (this.isDuplicateRow(target.getTableData(), row, 'materialId,sn')) {
        callback(false, '“物料+序列号”不能重复！');
      } else {
        callback(true);
      }
    },

    rubricSingleRangeValidator({cellValue, row, column}, callback, target) {
      const rubric = Number(cellValue);
      if (isNaN(rubric)) {
        callback();
        return;
      }

      if (rubric > 0) {
        callback(false, '红字单${title}不能为正数');
        return;
      }

      const rows = this.filterRows(this.$refs.srcSingleTable.getTableData(), row, 'materialId,sn');
      const blue = rows[0][column.key];
      if (isNaN(blue)) {
        callback();
        return;
      }
      if (-rubric > blue) {
        callback(false, '不能超出蓝字单！');
        return;
      }

      callback(true); //true：通过验证
    },

  }
}