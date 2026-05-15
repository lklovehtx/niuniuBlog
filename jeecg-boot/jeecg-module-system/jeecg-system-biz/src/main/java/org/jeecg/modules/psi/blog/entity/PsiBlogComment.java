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
@TableName("psi_blog_comment")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "PsiBlogComment对象", description = "博客评论表")
public class PsiBlogComment {

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "评论ID")
    private String id;

    @ApiModelProperty(value = "文章ID")
    private String articleId;

    @ApiModelProperty(value = "父评论ID")
    private String parentId;

    @ApiModelProperty(value = "评论内容")
    private String content;

    @ApiModelProperty(value = "评论者ID")
    private String commentatorId;

    @ApiModelProperty(value = "评论者名称")
    private String commentatorName;

    @ApiModelProperty(value = "评论者头像")
    private String commentatorAvatar;

    @ApiModelProperty(value = "回复目标用户ID")
    private String replyToId;

    @ApiModelProperty(value = "回复目标用户名")
    private String replyToName;

    @ApiModelProperty(value = "点赞次数")
    private Integer likeCount;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "删除标志")
    private String delFlag;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
