package io.finer.erp.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * @Description: bas_biz_options
 * @Author: jeecg-boot
 * @Date:   2022-12-18
 * @Version: V1.1
 */
@Data
@TableName("bas_biz_options")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="bas_biz_options对象", description="bas_biz_options")
public class BasBizOptions implements Serializable {
    private static final long serialVersionUID = 1L;

	/**ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "ID")
    private java.lang.String id;
	/**批次模式*/
	@Excel(name = "批次模式", width = 15, dictTable = "x_batch_mode", dicText = "", dicCode = "")
	@Dict(dictTable = "x_batch_mode", dicText = "", dicCode = "")
    @ApiModelProperty(value = "批次模式")
    private java.lang.String batchMode; //0 无批次、1 入库时手动输入批次号、2 入库时自动生成批次号、3 入库时自动生成批次号可修改

    //20251211 cfm del：移到SysOption中
    //private java.lang.String appDownloadUrl;
    //private java.lang.String apkDownloadUrl;
    //private java.lang.String apkVersion;

    //20250711 cfm add
    private java.lang.Integer isLockedWhenCheck;
}
