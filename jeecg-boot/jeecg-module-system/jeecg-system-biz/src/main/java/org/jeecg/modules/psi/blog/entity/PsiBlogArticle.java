package org.jeecg.modules.psi.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@TableName("psi_blog_article")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "PsiBlogArticle对象", description = "博客文章表")
public class PsiBlogArticle {

    @TableId(type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "文章ID")
    private String id;

    @ApiModelProperty(value = "文章标题")
    private String title;

    @ApiModelProperty(value = "文章摘要")
    private String summary;

    @ApiModelProperty(value = "文章内容")
    private String content;

    @ApiModelProperty(value = "封面图片")
    private String coverImage;

    @ApiModelProperty(value = "作者ID")
    private String authorId;

    @ApiModelProperty(value = "作者名称")
    private String authorName;

    @ApiModelProperty(value = "作者头像")
    private String authorAvatar;

    @ApiModelProperty(value = "分类ID")
    private String categoryId;

    @ApiModelProperty(value = "分类名称")
    private String categoryName;

    @ApiModelProperty(value = "标签")
    private String tags;

    @ApiModelProperty(value = "浏览次数")
    private Integer viewCount;

    @ApiModelProperty(value = "点赞次数")
    private Integer likeCount;

    @ApiModelProperty(value = "评论次数")
    private Integer commentCount;

    @ApiModelProperty(value = "收藏次数")
    private Integer collectCount;

    @ApiModelProperty(value = "状态（1发布 0草稿）")
    private String status;

    @ApiModelProperty(value = "是否置顶")
    private String isTop;

    @ApiModelProperty(value = "是否推荐")
    private String isRecommend;

    @ApiModelProperty(value = "删除标志")
    private String delFlag;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "发布时间")
    private Date publishTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否已点赞")
    private Boolean isLiked;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否已收藏")
    private Boolean isCollected;
}
