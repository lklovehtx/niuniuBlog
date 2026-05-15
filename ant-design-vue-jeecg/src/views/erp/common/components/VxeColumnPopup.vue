<template>
  <div>
    <a-button  @click="$refs.popup.openModal()" type="link" icon="cluster" size="small"/>
    <j-popup v-show="false" ref="popup"
             :code="props.column.popupCode" :param="param"
             :org-fields="props.column.orgFields" :dest-fields="props.column.destFields"
             :multi="false" @input="onInput"/>
  </div>
</template>

<script>
  export default {
    name: "VxeColumnPopup",

    props: {
      props:{
        type: Object,
        required: true,
      },
    },

    watch: {
      props: {
        immediate: true,
        handler: function () {
          const col = this.props.column;
          this.param = !col.param ? null : {...col.param};

          if (!col.paramFields) return;
          const paramFieldArr = col.paramFields.split(',');
          if (paramFieldArr.length === 0) return;

          const destFieldArr = col.destFields.split(',');
          const orgFieldArr = col.orgFields.split(',');
          for (let i = 0; i < paramFieldArr.length; i++) {
            for (let j = 0; j < destFieldArr.length; j++) {
              if (destFieldArr[j] === paramFieldArr[i])
                this.param[orgFieldArr[j]] = this.props.row[paramFieldArr[i]];
            }
          }
        },
      },

    },

    data() {
      return {
        param: null,
      }
    },

    methods: {
      onInput(val, row){
        if (!row || Object.keys(row).length === 0) return;

        const oldValues = {}, values = {...row};
        Object.keys(row).forEach(key => oldValues[key] = this.props.row[key]);

        this.props.target.setValues([{rowKey: this.props.rowId, values: row}]);

        this.$emit("valuesChange", {
          values: values,
          oldValues: oldValues,
          row: this.props.target.getIfRowById(this.props.rowId).row,
          target: this.props.target
        });
      },

    },

  }
</script>
