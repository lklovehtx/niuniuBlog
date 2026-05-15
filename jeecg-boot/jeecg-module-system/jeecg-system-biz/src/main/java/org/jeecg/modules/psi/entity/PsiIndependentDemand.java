package org.jeecg.modules.psi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@TableName("psi_independent_demand")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "psi_independent_demand对象", description = "独立需求抬头表")
public class PsiIndependentDemand {

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

    @Excel(name = "独立需求号", width = 20)
    @ApiModelProperty(value = "独立需求号")
    private String demandNo;

    @Excel(name = "需求公司", width = 15)
    @ApiModelProperty(value = "需求公司")
    private String company;

    @Excel(name = "需求工厂", width = 15)
    @ApiModelProperty(value = "需求工厂")
    private String factory;

    @ApiModelProperty(value = "需求供应商ID")
    private String supplierId;

    @Excel(name = "需求供应商", width = 20)
    @ApiModelProperty(value = "需求供应商名称")
    private String supplierName;

    @ApiModelProperty(value = "需求客户ID")
    private String customerId;

    @Excel(name = "需求客户", width = 20)
    @ApiModelProperty(value = "需求客户名称")
    private String customerName;

    @Excel(name = "需求外部编号", width = 20)
    @ApiModelProperty(value = "需求外部编号")
    private String externalNo;

    @Dict(dicCode = "demand_status")
    @Excel(name = "需求状态", width = 10)
    @ApiModelProperty(value = "需求状态（0草稿 1已提交 2已审批 3已关闭）")
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

    @Excel(name = "备注", width = 30)
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "行项目列表")
    private List<PsiIndependentDemandItem> items;
}