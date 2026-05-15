/**
 * FormModel Validator
 */
export const BillValidatorMixin = {
  methods: {
    rubricFormModelValidator(rule, value, callback) {
      const v = Number(value);
      if (isNaN(v)) {
        callback();
        return;
      }

      const isRubric = Number(this.model.isRubric);
      if (isRubric === 0 && v < 0) {
        callback('蓝字单据不能为负数');
      } else if (isRubric === 1 && v > 0) {
        callback('红字单据不能为正数');
      } else {
        callback();
      }
    },

  }
 }

