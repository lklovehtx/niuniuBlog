package org.jeecg.modules.psi.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@TableName("psi_blog_collect")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "PsiBlogCollect对象", description = "文章收藏表")
public class PsiBlogCollect {

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "收藏ID")
    private String id;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "文章ID")
    private String articleId;

    @ApiModelProperty(value = "收藏时间")
    private Date createTime;
}
