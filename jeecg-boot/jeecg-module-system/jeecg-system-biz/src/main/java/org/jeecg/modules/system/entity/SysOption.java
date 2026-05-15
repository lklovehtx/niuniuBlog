package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * @Description: 系统配置
 * @Author: cfm
 * @Date: 2025-12-11
 * @Version: V1.0
 */
@Data
@TableName("sys_option")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "系统配置")
public class SysOption implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * key
     */
    @Excel(name = "key", width = 15)
    @ApiModelProperty(value = "key")
    @TableId(value = "key_", type = IdType.INPUT)
    private String key;

    /**
     * value
     */
    @Excel(name = "value", width = 15)
    @ApiModelProperty(value = "value")
    private String value;

    /**
     * 说明
     */
    @Excel(name = "description", width = 15)
    @ApiModelProperty(value = "description")
    private String description;

    /**
     * 序号
     */
    @Excel(name = "sortNo", width = 15)
    @ApiModelProperty(value = "sortNo")
    private Float sortNo;
}
