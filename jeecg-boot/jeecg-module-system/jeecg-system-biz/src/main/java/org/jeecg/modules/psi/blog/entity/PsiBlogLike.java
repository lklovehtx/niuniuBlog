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
@TableName("psi_blog_like")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "PsiBlogLike对象", description = "文章点赞表")
public class PsiBlogLike {

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "点赞ID")
    private String id;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "文章ID")
    private String articleId;

    @ApiModelProperty(value = "点赞时间")
    private Date createTime;
}
