import ColumnResizeMixin from './ListColumnResizeMixin'
import {} from "../../common/utils/util"; //执行Date.prototype.format等
import { disabledAuthFilter } from "@/utils/authFilter"

export const ListMixin = {
  mixins: [ColumnResizeMixin],

  computed: {
    importExcelUrl: function(){
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
    },
  },

  methods: {
    //按钮权限控制
    isDisabledAuth(code){
      return disabledAuthFilter(code);
    },

    myHandleAdd() {
      this.$refs.modalForm.action = "add";
      this.handleAdd();
    },
    myHandleEdit(record) {
      this.$refs.modalForm.action = "edit";
      this.handleEdit(record);
    },
    myHandleDetail(record) {
      this.$refs.modalForm.action = "detail";
      this.handleDetail(record);
    },
  }
}