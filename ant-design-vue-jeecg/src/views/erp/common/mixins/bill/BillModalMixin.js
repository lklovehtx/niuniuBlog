import { disabledAuthFilter } from "@/utils/authFilter"

export const BillModalMixin = {
  data() {
    return {
      action: '',
      title:'',
      visible: false,
      disableSubmit: false,

      width: Math.min(1300, window.innerWidth),
      loading: false, //用于判断：加载时不能保存或提交
      entryCount: 0,  //用于判断：无明细不能提交
      singleCount: 0  //用于判断：无个体不能提交
    };
  },

  methods: {
    //按钮权限控制
    isDisabledAuth(code){
      return disabledAuthFilter(code);
    },

    add () {
      this.visible=true
      this.$nextTick(()=>{
        this.$refs.realForm.action = this.action;
        this.$refs.realForm.add();
      })
    },
    edit (record) {
      this.visible=true
      this.$nextTick(()=>{
        this.$refs.realForm.action = this.action;
        this.$refs.realForm.edit(record);
      })
    },
    close () {
      //20240718 cfm add
      if (this.action === 'add' || this.action === 'edit') {
        const that = this;
        this.$confirm({
          title: this.title,
          content: `保存${this.title}吗？`,
          okText: '保存',
          cancelText: '放弃保存',
          onOk: () => that.handleSave(),
          onCancel: () => {that.$emit('close'); that.visible = false;}
        });
        return;
      }

      this.$emit('close');
      this.visible = false;
    },
    submitCallback(){
      this.$emit('ok');
      this.visible = false;
    },
    handleOk () {
      this.$refs.realForm.handleOk();
    },
    handleCancel () {
      this.close()
    },
    handleSave() {
      this.$refs.realForm.handleSave();
    },

    handleSubmit() {
      this.$refs.realForm.handleSubmit();
    },
    handleCheck() {
      this.$refs.realForm.handleCheck();
    },
    handleEbpm() {
      this.$refs.realForm.handleEbpm();
    },
    handleExecute() {
      this.$refs.realForm.handleExecute();
    },
    handleClose() {
      this.$refs.realForm.handleClose();
    },
    handleUnclose() {
      this.$refs.realForm.handleUnclose();
    },
    handleVoid() {
      this.$refs.realForm.handleVoid();
    },
    handlePrint(){
      this.$emit('print', this.$refs.realForm.model.id);
    },

  }
}