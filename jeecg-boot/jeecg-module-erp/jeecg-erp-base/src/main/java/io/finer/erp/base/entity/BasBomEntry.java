package io.finer.erp.base.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.UnsupportedEncodingException;

/**
 * @Description: BOM明细
 * @Author: jeecg-boot
 * @Date:   2023-12-22
 * @Version: V1.0
 */
@ApiModel(value="bas_bom_entry对象", description="BOM明细")
@Data
@TableName("bas_bom_entry")
public class BasBomEntry implements Serializable {
    private static final long serialVersionUID = 1L;

	/**ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "ID")
    private java.lang.String id;
	/**mid*/
    @ApiModelProperty(value = "mid")
    private java.lang.String mid;
	/**序号*/
	@Excel(name = "序号", width = 15)
    @ApiModelProperty(value = "序号")
    private java.lang.Integer entryNo;
	/**子物料*/
	@Excel(name = "子物料", width = 15, dictTable = "bas_material", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "子物料")
    private java.lang.String materialId;
	/**计量单位*/
	@Excel(name = "计量单位", width = 15, dictTable = "bas_unit", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "计量单位")
    private java.lang.String unitId;
	/**数量*/
	@Excel(name = "数量", width = 15)
    @ApiModelProperty(value = "数量")
    private java.math.BigDecimal qty;
    /**拆卸时成本占比%*/
    @Excel(name = "拆卸时成本占比%", width = 15)
    @ApiModelProperty(value = "拆卸时成本占比%")
    private java.math.BigDecimal costRate;

    //20250406 cfm add
    /**生产损耗率%*/
    @Excel(name = "生产损耗率%", width = 15)
    @ApiModelProperty(value = "生产损耗率%")
    private java.math.BigDecimal lossRate;

	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
	/**版本*/
	@Excel(name = "版本", width = 15)
    @ApiModelProperty(value = "版本")
    private java.lang.Integer version;
}
