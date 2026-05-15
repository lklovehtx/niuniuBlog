import {getAction} from '@/api/manage'
import {JVXETypes} from '@/components/jeecg/JVxeTable';
import {emitColumnsChange, stringIsEmpty} from "../../utils/util"; //20231121 cfm modi
import {mapGetters} from "vuex";
import { mixinDevice } from '@/utils/mixin.js'

export const DetailMixin = {
  mixins: [mixinDevice],

  methods: {
    ...mapGetters(["bizOptions", "materialList"]),

    // columns utils
    //------------------------------------------------

    filterColumns(tables) {
      if (Array.isArray(tables)) {
        tables.forEach(table => this.filterColumns(table));
        return;
      }

      const table = tables || this.entryTable;
      if (!table || !table.columns) return;

      let exkeys = this.disabled ? table.exKeysWhenDetail : table.exKeysWhenEdit;
      if (!this.hasBarcode) exkeys = !exkeys ? "barcode" : exkeys + ",barcode";
      if (stringIsEmpty(exkeys)) return;

      exkeys = exkeys.split(',');
      table.columns = table.columns.filter(column => !exkeys.includes(column.key));
    },

    initBatchNoColumn(tables) {
      if (Array.isArray(tables)) {
        tables.forEach(table => this.initBatchNoColumn(table));
        return;
      }

      const table = tables || this.entryTable;
      if (!table || !table.columns) return;

      const column = table.columns.find(column => column.key === 'batchNo');
      if (!column) return;

      if (this.bizOptions().batchMode === '0') {
        column.defaultValue = '0';
        column.type = JVXETypes.hidden;
      }
      else if (this.bizOptions().batchMode === '2') {
        column.disabled = true;
      }
    },

    //取消列的fixed，使得unitId、qty排在前面，其它列可以左右滑动，以便手机端时查看。
    initColumnsForMobile(tables) {
      if (!this.isMobile()) return;
      if (Array.isArray(tables)) {
        tables.forEach(table => this.initColumnsForMobile(table));
        return;
      }

      const table = tables || this.entryTable;
      if (!table || !table.columns) return;

      table.columns.forEach(column => delete column.fixed);
    },

    emptyColumns(row, columnKeys, jvxeTable) {
      if (stringIsEmpty(columnKeys)) return;
      jvxeTable = jvxeTable || this.$refs.entryTable;

      const values = {};
      const keys = Array.isArray(columnKeys) ? columnKeys : columnKeys.split(',');
      for(let key of keys) values[key] = '';
      jvxeTable.setValues([{rowKey: row.id, values: values}]);
    },

    // rows utils
    //------------------------------------------------

    filterRows(rows, row, columnKeys) {
      if (rows.length===0) return rows;

      let keys = [];
      if (!stringIsEmpty(columnKeys)) {
        keys = Array.isArray(columnKeys) ? columnKeys : columnKeys.split(',');
      }
      else {
        for(let k in row) {
          keys.push(k);
        }
      }

      return rows.filter(r => {
        let res = true;
        for (let key of keys) {
          res = res && r[key] === row[key];
        }
        return res;
      });
    },

    isDuplicateRow(rows, row, columnKeys) {
      return this.filterRows(rows, row, columnKeys).length > 1;
    },

    findRow(rows, row, columnKeys) {
      let keys = [];
      if (!stringIsEmpty(columnKeys)) {
        keys = Array.isArray(columnKeys) ? columnKeys : columnKeys.split(',');
      }
      else {
        for(let k in row) {
          keys.push(k);
        }
      }

      return rows.find(r => {
        let res = true;
        for (let key of keys) {
          res = res && r[key] === row[key];
        }
        return res;
      });
    },

    // event utils
    //------------------------------------------------

    onEntryAdded(event) {
      const {row, target} = event
      let rows = target.getTableData();
      let maxEntryNo = 0;
      for (let row of rows) {
        let entryNo = Number(row['entryNo']);
        if (!isNaN(entryNo) && entryNo > maxEntryNo) maxEntryNo = entryNo;
      }

      maxEntryNo += this.entryNoStep || 10;
      target.setValues([{rowKey: row.id, values: {entryNo: maxEntryNo}}]);
    },

    onInEntryAdded(event){
      this.onEntryAdded(event);

      const { row, target } = event;
      if (stringIsEmpty(row.batchNo)) target.setValues([{rowKey: row.id, values: {batchNo: this.generateBatchNo(row)}}]);
    },


    // specific column utils
    //------------------------------------------------

    //vue created时，删除物料列的dictCode，加载相关数据，设置物料列的options
    // Deprecated
    setMaterialOptions(entryTable) {
      if (this.disabled) return;
      entryTable = entryTable || this.entryTable;

      const col = entryTable.columns.find(c => c.key === 'materialId');
      if (!col || col.disabled) return;
      delete col.dictCode;

      if (this.materialList().length === 0) return;
      col.options = this.materialList().map(m => ({value: m.id, text: m.auxName}));
      emitColumnsChange(entryTable.columns);
    },

    //物料列valueChange时，设置单位列，设置物料规格型号, 清空其他关联列值
    handleMaterialChange(row, entryJVxeTable, emptyColumnKeys) {
      entryJVxeTable = entryJVxeTable || this.$refs.entryTable;

      let material = null;
      if (!stringIsEmpty(row.materialId)) material = this.getMaterial(row.materialId);

      //设置物料编码、规格型号、条码
      let values = {};
      if (entryJVxeTable.vxeColumns.find(c => c.key === 'materialCode')) values.materialCode = !material ? '' : material.code;
      if (entryJVxeTable.vxeColumns.find(c => c.key === 'materialModel')) values.materialModel = !material ? '' : material.model;
      if (entryJVxeTable.vxeColumns.find(c => c.key === 'barcode')) values.barcode = !material ? '' : material.barcode;
      if (Object.keys(values).length > 0) entryJVxeTable.setValues([{rowKey: row.id, values: values}]);

      //设置单位列的options、value
      const col = entryJVxeTable.vxeColumns.find(c => c.key === 'unitId');
      if (!!col && !col.disabled) {
        col.options = [];
        if (!!material) {
          const units = this.getMaterialUnits(material.id);
          if (!!units) col.options = units.map(u => ({value: u.id, text: u.name}));
        }

         //出库不使用主单位设置单位（而是在获得库存信息处使用库存单位设置）
        if (!row.stockIoDirection || row.stockIoDirection !== '2') {
          values = {};
          values.unitId = !material ? '' : material.unitId ;
          entryJVxeTable.setValues([{rowKey: row.id, values: values}]);
        }
      }

      //清空其他关联列值
      this.emptyColumns(row, emptyColumnKeys, entryJVxeTable);
    },

    //删除单位列的dictCode，设置单位列的options
    setMaterialUnitOptions(row, entryJVxeTable) {
      if (this.disabled) return;
      entryJVxeTable = entryJVxeTable || this.$refs.entryTable;

      const col = entryJVxeTable.vxeColumns.find(c => c.key === 'unitId');
      if (!col || col.disabled) return;

      //删除单位列的dictCode，设置单位列的options
      delete col.dictCode;

      let material = null;
      if (!stringIsEmpty(row.materialId)) material = this.materialList().find(m => m.id === row.materialId);
      if (!material) return;

      const units = this.getMaterialUnits(material.id);
      col.options = [];
      if (!!units) col.options = units.map(u => ({value: u.id, text: u.name}));
    },

    //物料不分批次：JVxeTable editActived时、物料列valueChange时，用物料库存的仓库设置仓库选项
    // 1) 删除仓库列的dictCode，设置options;
    // 2) 设置仓库列及相关列（含重新计算出库金额）
    setWarehouseOptions(row, entryJVxeTable) {
      if (this.bizOptions().batchMode!=='0') {
        this.$warning({title: '软件错误', content: '有批次时，不能调用 setWarehouseOptions 方法！'});
        return;
      }

      if (this.disabled) return;
      entryJVxeTable = entryJVxeTable || this.$refs.entryTable;
      if (stringIsEmpty(row.materialId)) {
        entryJVxeTable.setValues([{rowKey: row.id, values: {warehouseId: ''}}]);
        return;
      }

      const col = entryJVxeTable.vxeColumns.find(c => c.key === 'warehouseId');
      if (!col || col.disabled || !!col.lastMaterialId && col.lastMaterialId === row.materialId) return;
      col.lastMaterialId = row.materialId;

      const that = this;
      const params = {materialId: row.materialId, batchNo: '0'};
      this.getInventories(params, function (invs) {
        invs = invs || [];

        //删除仓库列的dictCode，设置options
        delete col.dictCode;
        col.options = invs.length > 0 ? invs.map(inv => ({value: inv.warehouseId,text: inv.warehouseId_dictText})) : [];

        //设置仓库列及相关列
        if (invs.length === 1) {
          entryJVxeTable.setValues([{rowKey: row.id, values: {warehouseId: invs[0].warehouseId}}]);
          that.setInventoryValues(row, entryJVxeTable, invs[0]);
        } else if (!col.options.find(o => o.value === row.warehouseId)) {
          entryJVxeTable.setValues([{rowKey: row.id, values: {warehouseId: ''}}]);
          that.setInventoryValues(row, entryJVxeTable, {});
        }
      });
    },

    //物料不分批次，仓库列valueChange时，设置库存相关列值（含重新计算出库金额）
    handleWarehouseChange(row, entryJVxeTable, emptyColumnKeys) {
      if (this.bizOptions().batchMode!=='0') {
        this.$warning({title: '软件错误', content: '有批次时，不能调用 handleWarehouseChange 方法！'});
        return;
      }

      entryJVxeTable = entryJVxeTable || this.$refs.entryTable;
      this.emptyColumns(row, emptyColumnKeys, entryJVxeTable);
      if (stringIsEmpty(row.materialId) || stringIsEmpty(row.warehouseId)) return;

      const that = this;
      const params = {materialId: row.materialId, warehouseId: row.warehouseId, batchNo: '0'};
      this.getInventory(params, function (inv) {
        that.setInventoryValues(row, entryJVxeTable, inv || {unitId: '', qty: null, cost: null});
      });
    },

    setInventoryValues(row, entryJVxeTable, inv) {
      let values = {};
      values.inventoryUnitId = inv.unitId;
      values.inventoryQty = inv.qty;
      values.inventoryCost = inv.cost;
      if (stringIsEmpty(row.unitId)) values.unitId = values.inventoryUnitId;
      if (this.isCalcOutCost) values.cost = this.calcOutCost(row, values);
      entryJVxeTable.setValues([{rowKey: row.id, values: values}]);
    },

    //20240923 cfm modi：增加库存不存在则清空库存列
    setEntryInventory(entryJVxeTable, entry, callback) {
      let values = {inventoryUnitId: '', inventoryQty: 0, inventoryCost: 0};
      if (stringIsEmpty(entry.materialId) || stringIsEmpty(entry.warehouseId) || stringIsEmpty(entry.batchNo)) {
        entryJVxeTable.setValues([{rowKey: entry.id, values: values}]);
        typeof callback === 'function' ? callback(entryJVxeTable, Object.assign(entry, values)) : ''
        return;
      }

      const url = "/stock/stkInventory/list";
      const params = {materialId: entry.materialId, warehouseId: entry.warehouseId, batchNo: entry.batchNo, isClosed: 0, pageSize: 10};
      getAction(url, params).then(res => {
         if (res.success && res.result.records.length > 0) {
           const rec = res.result.records[0];
           values = {inventoryUnitId: rec.unitId, inventoryQty: rec.qty, inventoryCost: rec.cost};
         }
        entryJVxeTable.setValues([{rowKey: entry.id, values: values}]);
        typeof callback === 'function' ? callback(entryJVxeTable, Object.assign(entry, values)) : ''
      })
    },

    // source bill utils
    //------------------------------------------------

    removeFreeSrcBills(referrers, srcTable, srcEntryTable) {
      referrers = referrers || this.$refs.entryTable.getTableData();
      srcTable = srcTable || this.srcTable;
      srcEntryTable = srcEntryTable || this.srcEntryTable;

      let ds = [];
      if (srcTable) {
        for (let b of srcTable.dataSource){
          if (referrers.find(row => row.srcBillId === b.id)) ds.push(b);
        }
        srcTable.dataSource = ds;
      }

      ds = [];
      if (srcEntryTable) {
        for (let e of srcEntryTable.dataSource){
          if (srcTable.dataSource.find(b => b.id === e.mid)) ds.push(e);
        }
        srcEntryTable.dataSource = ds;
      }
    },

    removeFromSrcTable(jxSrcTable, srcTable, srcEntryTable) {
      jxSrcTable = jxSrcTable || this.$refs.srcTable;
      srcTable = srcTable || this.srcTable;
      srcEntryTable = srcEntryTable || this.srcEntryTable;
      let ids = jxSrcTable.selectedRowIds;
      let dstRows = this.$refs.entryTable.getTableData();

      //id被引用的记录不能移除
      let ids2 = [];
      for (let id of ids)
        if (!dstRows.find(r => r.srcBillId === id)) ids2.push(id);

      //留下主表记录未删除的
      if (ids2.length > 0 && srcEntryTable) {
        let ds = [];
        for(let r of srcEntryTable.dataSource)
          if (!ids2.includes(r.mid)) ds.push(r);
        srcEntryTable.dataSource = ds;
      }

      //留下未删除的
      if (ids2.length > 0) {
        let ds = [];
        for(let r of srcTable.dataSource)
          if (!ids2.includes(r.id)) ds.push(r);
        srcTable.dataSource = ds;
      }

      jxSrcTable.clearSelection();
    },

    srcBillCheckboxMethod({row}) {
      let dstRows = this.$refs.entryTable.getTableData();
      return !dstRows.find(dstRow => dstRow.srcBillId === row.id);
    },

    srcEntryCheckboxMethod({row}) {
      let dstRows = this.$refs.entryTable.getTableData();
      return !dstRows.find(dstRow => dstRow.srcEntryId === row.id);
    },

    srcSingleCheckboxMethod({row}) {
      let dstRows = this.$refs.singleTable.getTableData();
      return !dstRows.find(dstRow => dstRow.materialId === row.materialId && dstRow.sn === row.sn);
    },

  }
}