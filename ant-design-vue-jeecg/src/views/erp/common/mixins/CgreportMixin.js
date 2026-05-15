import { getAction } from '@/api/manage'
import { filterMultiDictText } from '@/components/dict/JDictSelectUtil'

export const CgreportMixin = {
  data(){
    return {
      dictOptions: {},
      url: {
        getColumns: '/online/cgreport/api/getColumns/',
        getData: '/online/cgreport/api/getData/',
        exportXls: "/online/cgreport/api/exportManySheetXls/",
      },
    }
  },

  methods:{
    initReport() {
      if(!this.cgreportId) return;

      this.url.list = `${this.url.getData}${this.cgreportId}`;
      this.url.exportXlsUrl = `${this.url.exportXls}${this.cgreportId}`;
      this.ipagination.pageSizeOptions.push('40');

      getAction(`${this.url.getColumns}${this.cgreportId}`, {}).then((res) => {
        if (res.success) {
          let { columns, dictOptions } = res.result;
          this.dictOptions = dictOptions || {};
          for(let i = 0; i < columns.length; i++){
            if(columns[i].customRender){
              let fieldName = columns[i].customRender;
              let column = this.columns.find(c => c.dataIndex === fieldName);
              if (!!column) column.customRender = (t => t ? filterMultiDictText(this.dictOptions[fieldName], t + '') : t);
            }
          }
          this.loadData(); //20240405 cfm add: 本处增加loadData；各cgreport报表created中不再调用loadData
        }else{
          this.$message.warning(res.message)
        }
      });
    },

  }

}