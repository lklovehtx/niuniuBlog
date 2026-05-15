package io.finer.erp.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.finer.erp.common.entity.Entry;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 出入库单个体
 * @Author:
 * @Date:   2023-09-17
 * @Version: V1.0
 */
@ApiModel(value="stk_io_single对象", description="出入库单个体")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("stk_io_single")
public class StkIoSingle {
	private static final long serialVersionUID = 1L;

	/**ID*/
	@TableId(type = IdType.ASSIGN_ID)
	@ApiModelProperty(value = "ID")
	private java.lang.String id;
	/**主表*/
	@ApiModelProperty(value = "主表")
	private String mid;
	/**物料*/
	@Excel(name = "物料", width = 15, dictTable = "bas_material", dicText = "name", dicCode = "id")
	@Dict(dictTable = "bas_material", dicText = "name", dicCode = "id")
	@ApiModelProperty(value = "物料")
	private String materialId;
	/**序列号*/
	@Excel(name = "序列号", width = 15)
	@ApiModelProperty(value = "序列号")
	private java.lang.String sn;
	/**批次号*/
	@Excel(name = "批次号", width = 15)
	@ApiModelProperty(value = "批次号")
	private java.lang.String batchNo;
	/**仓库*/
	@Excel(name = "仓库", width = 15, dictTable = "bas_warehouse", dicText = "name", dicCode = "id")
	@Dict(dictTable = "bas_warehouse", dicText = "name", dicCode = "id")
	@ApiModelProperty(value = "仓库")
	private java.lang.String warehouseId;
	/**出入方向*/
	@Excel(name = "出入方向", width = 15, dicCode = "x_stock_io_direction")
	@Dict(dicCode = "x_stock_io_direction")
	@ApiModelProperty(value = "出入方向")
	private java.lang.String stockIoDirection;
	/**计量单位*/
	@Excel(name = "计量单位", width = 15, dictTable = "bas_unit", dicText = "name", dicCode = "id")
	@Dict(dictTable = "bas_unit", dicText = "name", dicCode = "id")
	@ApiModelProperty(value = "计量单位")
	private String unitId;
	/**数量*/
	@Excel(name = "数量", width = 15)
	@ApiModelProperty(value = "数量")
	private java.math.BigDecimal qty;
	/**结算金额*/
	@Excel(name = "结算金额", width = 15)
	@ApiModelProperty(value = "结算金额")
	private java.math.BigDecimal settleAmt;
	/**计入成本费用*/
	@Excel(name = "计入成本费用", width = 15)
	@ApiModelProperty(value = "计入成本费用")
	private java.math.BigDecimal expense;
	/**成本*/
	@Excel(name = "成本", width = 15)
	@ApiModelProperty(value = "成本")
	private java.math.BigDecimal cost;
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

	/**即时库存计量单位*/
	@TableField(exist = false)
	private java.lang.String inventoryUnitId;
	/**即时库存数量*/
	@TableField(exist = false)
	private java.math.BigDecimal inventoryQty;
	/**即时库存成本*/
	@TableField(exist = false)
	private java.math.BigDecimal inventoryCost;
}
