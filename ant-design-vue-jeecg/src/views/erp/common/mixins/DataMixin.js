import {getAction} from '@/api/manage'
import {stringIsEmpty} from "../utils/util";
import {mapGetters} from "vuex";

export const DataMixin = {

  methods: {
    ...mapGetters(["materialList", "unitList"]),

    // 加载子表数据：明细、源单据、源明细
    requestSubDatas(entryTable, srcTable, srcEntryTable, success, srcBillType, srcBillIds) {
      let that = this;
      if (entryTable)
        that.requestSubTableData(entryTable.url.list, {id: this.model.id}, entryTable, success1);
      else{
        entryTable = this.entryTable;
        success1();
      }

      function success1(){
        let recs = entryTable.dataSource;
        entryTable.rowCount = recs.length;

        if (!srcBillIds && recs.length > 0) {
          if (srcBillType && srcBillType.length > 0) recs = recs.filter(r => r.srcBillType === srcBillType);
          srcBillIds = that.getIds(recs, 'srcBillId');
        }

        if (srcTable && srcBillIds && srcBillIds.length > 0)
          that.requestSubTableData(srcTable.url.list, {ids: srcBillIds}, srcTable, success2);
        else
          success2();
      }

      function success2(){
        if (srcEntryTable && srcBillIds && srcBillIds.length > 0)
          that.requestSubTableData(srcEntryTable.url.list, {ids: srcBillIds}, srcEntryTable, success);
        else
          typeof success === 'function' ? success() : '';
      }
    },

    // 加载子表增量数据：源单据、源明细
    requestSrcDeltas(srcBillIds, srcTable, srcEntryTable, success) {
      if(!srcBillIds || srcBillIds.length ===0) return;

      srcTable = srcTable || this.srcTable;
      srcEntryTable = srcEntryTable || this.srcEntryTable; //this.srcEntryTable也可会undefined

      let ids0 = [];
      for(let rec of srcTable.dataSource) ids0.push(rec.id);

      //去掉已存在的id
      srcBillIds = srcBillIds.split(',');
      let ids1 = [];
      for(let id of srcBillIds) {
        if (!ids0.includes(id)) ids1.push(id);
      }
      srcBillIds = ids1.toString();

      let that = this;
      let deltas = {};
      if (srcTable && srcBillIds && srcBillIds.length > 0)
        that.requestAppendData(srcTable, {ids: srcBillIds}, success1);
      else
        success1(srcTable ? [] : undefined);

      function success1(srcBills){
        deltas.srcBills = srcBills;
        if (srcEntryTable && srcBillIds && srcBillIds.length > 0)
          that.requestAppendData(srcEntryTable, {ids: srcBillIds}, success2);
        else
          success2(srcEntryTable ? [] : undefined);
      }

      function success2(srcEntries){
        deltas.srcEntries = srcEntries;
        typeof success === 'function' ? success(deltas) : '';
      }
    },

    requestDelta(ids, tab, success) {
      //去掉已存在的id
      let ids1 = [];
      for(let id of ids.split(',')) {
        if (!tab.dataSource.find(r => r.id === id)) ids1.push(id);
      }
      ids = ids1.toString();

      this.requestAppendData(tab, {ids: ids}, success);
    },

    requestAppendData(tab, params, success) {
      tab.loading = true;
      getAction(tab.url.list, params).then(res => {
        let { result } = res;
        let recs = [];
        if (result) {
          if (Array.isArray(result)) {
            recs = result;
          } else if (Array.isArray(result.records)) {
            recs = result.records;
          }
        }
        if (recs.length > 0)  tab.dataSource = [...tab.dataSource, ...recs];

        typeof success === 'function' ? success(recs) : '';
      }).finally(() => {
        tab.loading = false
      })
    },

    getIds(records, idName = 'id') {
      let ids = [];
      for(let r of records) if (!ids.includes(r[idName])) ids.push(r[idName]);
      return ids.toString();
    },

    getUnit(unitId) {
      if (stringIsEmpty(unitId)) return null;

      const unit = this.unitList().find(u => u.isEnabled === 1 && u.id === unitId);
      if (!unit) {
        this.$warning({title: "计量单位", content: `【${unitId}】计量单位未找到，可能：1、新建单位，保存录入后尝试点击【浏览器刷新】页面；2、已被删除或禁用！`});
        return null;
      }
      return unit;
    },

    getConvertibleUnits(unitId) {
      if (stringIsEmpty(unitId)) return null;

      const unit = this.getUnit(unitId);
      if (!unit) return null;

      const pid = (unit.pid === '0' || stringIsEmpty(unit.pid)) ? unitId : unit.pid;
      const units = this.unitList().filter(u => u.isEnabled === 1 && u.id !== unitId && (u.pid === pid || u.id === pid));

      let result = [];
      result.push(unit); //放第一个
      if (!!units && units.length > 0) result.push(...units);
      return result;
    },

    getMaterial(materialId) {
      if (stringIsEmpty(materialId)) return null;

      const material = this.materialList().find(m => m.id === materialId);
      if (!material) {
        this.$warning({title: "物料", content: `【${materialId}】物料未找到，可能：1、新建物料，保存录入后尝试点击【浏览器刷新】页面；2、已被删除或禁用！`});
        return null;
      }
      return material;
    },

    getMaterialModel(materialId) {
      if (stringIsEmpty(materialId)) return null;

      const material = this.getMaterial(materialId);
      return !material ? null : material.model;
    },

    getMaterialUnits(materialId) {
      if (stringIsEmpty(materialId)) return null;

      const material = this.getMaterial(materialId);
      if (!material) return null;

      const units = this.getConvertibleUnits(material.unitId);
      if (!units) {
        this.$warning({title: "物料主单位", content: `【${material.name}】主单位不合法，可能：1、新设置的主单位，保存录入后尝试点击【浏览器刷新】页面；2、主单位已被删除或禁用！`});
        return null;
      }
      return units;
    },

    getUnitRate(materialId, fromUnitId, toUnitId) {
      if (stringIsEmpty(materialId) || stringIsEmpty(fromUnitId) || stringIsEmpty(toUnitId)) return null;
      if (fromUnitId === toUnitId) return 1;

      const material = this.getMaterial(materialId);
      if (!material) return null;

      const units = this.getMaterialUnits(materialId);
      if (!units) return null;

      const fromUnit = units.find(u => u.id === fromUnitId);
      const msg1 = `非【${material.name}】物料的合法单位！`;
      if (!fromUnit) {
        this.$warning({title: "物料单位", content: '原单位' + msg1});
        return null;
      }
      const msg2 = "不合法，转换系数必须为正数！";
      if (fromUnit.factor <= 0) {
        this.$warning({title: "物料单位", content: '原单位' + msg2}); //20250410 cfm modi: msg1 改成 msg2
        return null;
      }

      const toUnit = units.find(u => u.id === toUnitId);
      if (!toUnit) {
        this.$warning({title: "物料单位", content: '新单位' + msg1}); //20250410 cfm modi: msg2 改成msg1
        return null;
      }
      if (toUnit.factor <= 0) {
        this.$warning({title: "物料单位", content: '新单位' + msg2});
        return null;
      }

      return fromUnit.factor / toUnit.factor;
    },

    //通过callback返回结果
    getInventory(params, callback) {
      if (typeof callback !== 'function') return; //如果不是函数，则无法返回结果，那么get无意义

      if (stringIsEmpty(params.materialId) || stringIsEmpty(params.warehouseId) || stringIsEmpty(params.batchNo)) {
        callback();
        return;
      }

      const url = "/stock/stkInventory/list";
      getAction(url, {...params, isClosed: 0, pageSize: 10}).then(res => {
        if (!res.success || res.result.records.length === 0)
          callback();
        else
          callback(res.result.records[0]);
      });
    },

    //通过callback返回结果
    getInventories(params, callback) {
      if (typeof callback !== 'function') return;//如果不是函数，则无法返回结果，那么get无意义

      const url = "/stock/stkInventory/list";
      getAction(url, {...params, isClosed: 0, pageSize: 10000}).then(res => {
        if (!res.success || res.result.records.length === 0)
          callback();
        else
          callback(res.result.records);
      });
    },

    //通过callback返回结果
    getSingleInventory(params, callback) {
      if (typeof callback !== 'function') return;//如果不是函数，则无法返回结果，那么get无意义

      const url = "/stock/stkInventorySingle/queryBySn";
      getAction(url, params).then(res => {
        if (!res.success)
          callback();
        else
          callback(res.result);
      });
    },

  }
}