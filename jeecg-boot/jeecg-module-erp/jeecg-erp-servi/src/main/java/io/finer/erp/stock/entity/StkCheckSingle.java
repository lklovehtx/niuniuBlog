package io.finer.erp.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 个体
 * @Author:
 * @Date:   2023-10-30
 * @Version: V1.0
 */
@ApiModel(value="stk_check_single对象", description="盘点卡个体")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("stk_check_single")
public class StkCheckSingle {
	private static final long serialVersionUID = 1L;

	/**ID*/
	@TableId(type = IdType.ASSIGN_ID)
	@ApiModelProperty(value = "ID")
	private java.lang.String id;
	/**主表*/
	@ApiModelProperty(value = "主表")
	private String mid;
	/**是否新SN*/
	@Excel(name = "是否新SN", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
	@ApiModelProperty(value = "是否新SN")
	private Integer isNewSn;
	/**仓库*/
	@Excel(name = "仓库", width = 15, dictTable = "bas_warehouse", dicText = "name", dicCode = "id")
	@Dict(dictTable = "bas_warehouse", dicText = "name", dicCode = "id")
	@ApiModelProperty(value = "仓库")
	private String warehouseId;
	/**物料*/
	@Excel(name = "物料", width = 15, dictTable = "bas_material", dicText = "name", dicCode = "id")
	@Dict(dictTable = "bas_material", dicText = "name", dicCode = "id")
	@ApiModelProperty(value = "物料")
	private String materialId;
	/**序列号*/
	@Excel(name = "序列号", width = 15)
	@ApiModelProperty(value = "序列号")
	private java.lang.String sn;
	/**批号*/
	@Excel(name = "批号", width = 15)
	@ApiModelProperty(value = "批号")
	private String batchNo;
	/**计量单位*/
	@Excel(name = "计量单位", width = 15, dictTable = "bas_unit", dicText = "name", dicCode = "id")
	@Dict(dictTable = "bas_unit", dicText = "name", dicCode = "id")
	@ApiModelProperty(value = "计量单位")
	private String unitId;
	/**账存数量*/
	@Excel(name = "账存数量", width = 15)
	@ApiModelProperty(value = "账存数量")
	private java.math.BigDecimal bookQty;
	/**实存数量*/
	@Excel(name = "实存数量", width = 15)
	@ApiModelProperty(value = "实存数量")
	private java.math.BigDecimal qty;
	/**备注*/
	@Excel(name = "备注", width = 15)
	@ApiModelProperty(value = "备注")
	private java.lang.String remark;
	/**自定义1*/
	@Excel(name = "自定义1", width = 15)
	@ApiModelProperty(value = "自定义1")
	private java.lang.String custom1;
	/**自定义2*/
	@Excel(name = "自定义2", width = 15)
	@ApiModelProperty(value = "自定义2")
	private java.lang.String custom2;
}
