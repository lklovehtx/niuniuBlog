package org.jeecg.modules.psi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("psi_independent_demand_item")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "psi_independent_demand_item对象", description = "独立需求行项目表")
public class PsiIndependentDemandItem {

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "抬头表ID")
    private String demandId;

    @ApiModelProperty(value = "独立需求号")
    private String demandNo;

    @Excel(name = "行项目号", width = 10)
    @ApiModelProperty(value = "行项目号")
    private Integer lineNo;

    @ApiModelProperty(value = "需求物料ID")
    private String materialId;

    @Excel(name = "需求物料编码", width = 15)
    @ApiModelProperty(value = "需求物料编码")
    private String materialCode;

    @Excel(name = "需求物料名称", width = 20)
    @ApiModelProperty(value = "需求物料名称")
    private String materialName;

    @Excel(name = "需求工厂", width = 15)
    @ApiModelProperty(value = "需求工厂")
    private String factory;

    @Excel(name = "需求数量", width = 15)
    @ApiModelProperty(value = "需求数量")
    private BigDecimal requiredQty;

    @Excel(name = "单位", width = 10)
    @ApiModelProperty(value = "单位")
    private String unit;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "需求日期", width = 15)
    @ApiModelProperty(value = "需求日期")
    private Date requiredDate;

    @Excel(name = "需求状态", width = 10)
    @ApiModelProperty(value = "需求状态")
    private String demandStatus;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "删除标志")
    private String delFlag;
}