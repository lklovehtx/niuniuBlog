import XEUtils from "xe-utils";

export const DetailFormatMixin = {
  methods: {
    // JVxeTable column formatter
    // 参考:
    //  1) src/views/jeecg/JVxeDemo/layout-demo/Template1.vue
    //  2) https://xuliangzhan_admin.gitee.io/vxe-table/#/table/base/format

    // 四舍五入金额，每隔3位逗号分隔，默认2位小数
    formatAmt ({ cellValue, row, column}, digits = 2) {
      return Number(cellValue) ? XEUtils.commafy(Number(cellValue), { digits }) : cellValue;
    },

    // 四舍五入数量，每隔3位逗号分隔，默认3位小数
    formatQty ({ cellValue, row, column}, digits = 3) {
      return this.formatAmt({ cellValue, row, column}, digits);
    },

  }
}