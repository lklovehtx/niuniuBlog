package org.jeecg.modules.psi.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.psi.blog.entity.PsiBlogArticle;
import org.jeecg.modules.psi.blog.service.IAvatarService;
import org.jeecg.modules.psi.blog.service.IPsiBlogArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/psi/blog/article")
@Api(tags = "博客文章接口")
public class PsiBlogArticleController {

    @Autowired
    private IPsiBlogArticleService articleService;

    @Autowired
    private IAvatarService avatarService;

    @GetMapping("/list")
    @ApiOperation(value = "获取文章列表", notes = "获取文章列表")
    public Result<IPage<PsiBlogArticle>> list(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String authorId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String theme,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            HttpServletRequest request) {
        try {
            Page<PsiBlogArticle> page = new Page<>(pageNo, pageSize);
            PsiBlogArticle article = new PsiBlogArticle();
            article.setTitle(title);
            article.setCategoryId(categoryId);
            article.setAuthorId(authorId);
            article.setStatus(status);
            
            // 设置日期筛选（滚轮日期格式为 M/D）
            if (date != null && !date.isEmpty()) {
                article.setPublishTime(parseDate(date));
            }
            
            // 设置题材和主题筛选
            if (topic != null && !topic.isEmpty()) {
                article.setCategoryName(topic);
            }
            if (theme != null && !theme.isEmpty()) {
                article.setTags(theme);
            }

            String userId = request.getHeader("X-User-Id");
            IPage<PsiBlogArticle> result = articleService.queryPageList(page, article);

            if (userId != null) {
                for (PsiBlogArticle item : result.getRecords()) {
                    item.setIsLiked(articleService.isLiked(item.getId(), userId));
                    item.setIsCollected(articleService.isCollected(item.getId(), userId));
                    // 将作者头像相对路径转换为可访问的URL
                    if (item.getAuthorAvatar() != null && !item.getAuthorAvatar().isEmpty()) {
                        item.setAuthorAvatar(avatarService.getAvatarUrl(item.getAuthorAvatar()));
                    }
                }
            } else {
                // 未登录用户也需要处理头像URL
                for (PsiBlogArticle item : result.getRecords()) {
                    if (item.getAuthorAvatar() != null && !item.getAuthorAvatar().isEmpty()) {
                        item.setAuthorAvatar(avatarService.getAvatarUrl(item.getAuthorAvatar()));
                    }
                }
            }

            return Result.ok(result);
        } catch (Exception e) {
            log.error("获取文章列表异常", e);
            return Result.error("获取文章列表异常: " + e.getMessage());
        }
    }
    
    /**
     * 解析滚轮日期格式 M/D 为 Date 对象
     */
    private Date parseDate(String dateStr) {
        try {
            String[] parts = dateStr.split("/");
            if (parts.length == 2) {
                int month = Integer.parseInt(parts[0]);
                int day = Integer.parseInt(parts[1]);
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.MONTH, month - 1);
                cal.set(Calendar.DAY_OF_MONTH, day);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                return cal.getTime();
            }
        } catch (Exception e) {
            log.warn("日期解析失败: " + dateStr, e);
        }
        return null;
    }

    @GetMapping("/detail/{id}")
    @ApiOperation(value = "获取文章详情", notes = "获取文章详情")
    public Result<PsiBlogArticle> getArticle(@PathVariable String id, HttpServletRequest request) {
        try {
            PsiBlogArticle article = articleService.queryById(id);
            if (article != null) {
                articleService.increaseViewCount(id);

                String userId = request.getHeader("X-User-Id");
                if (userId != null) {
                    article.setIsLiked(articleService.isLiked(id, userId));
                    article.setIsCollected(articleService.isCollected(id, userId));
                }

                return Result.ok(article);
            }
            return Result.error("文章不存在");
        } catch (Exception e) {
            log.error("获取文章详情异常", e);
            return Result.error("获取文章详情异常: " + e.getMessage());
        }
    }

    @PostMapping("/add")
    @ApiOperation(value = "创建文章", notes = "创建文章")
    public Result<PsiBlogArticle> add(@RequestBody PsiBlogArticle article, HttpServletRequest request) {
        try {
            String userId = request.getHeader("X-User-Id");
            String username = request.getHeader("X-Username");

            if (userId == null) {
                return Result.error("请先登录");
            }

            article.setAuthorId(userId);
            article.setAuthorName(username);
            article.setCreateBy(username);

            if ("1".equals(article.getStatus())) {
                article.setPublishTime(new Date());
            }

            boolean success = articleService.saveArticle(article);
            if (success) {
                return Result.ok(article);
            }
            return Result.error("创建失败");
        } catch (Exception e) {
            log.error("创建文章异常", e);
            return Result.error("创建文章异常: " + e.getMessage());
        }
    }

    @PutMapping("/edit")
    @ApiOperation(value = "更新文章", notes = "更新文章")
    public Result<Boolean> edit(@RequestBody PsiBlogArticle article, HttpServletRequest request) {
        try {
            String username = request.getHeader("X-Username");
            article.setUpdateBy(username);

            if ("1".equals(article.getStatus()) && article.getPublishTime() == null) {
                article.setPublishTime(new Date());
            }

            boolean success = articleService.updateArticle(article);
            if (success) {
                return Result.ok(true);
            }
            return Result.error("更新失败");
        } catch (Exception e) {
            log.error("更新文章异常", e);
            return Result.error("更新文章异常: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除文章", notes = "删除文章")
    public Result<Boolean> delete(@PathVariable String id) {
        try {
            boolean success = articleService.deleteArticle(id);
            if (success) {
                return Result.ok(true);
            }
            return Result.error("删除失败");
        } catch (Exception e) {
            log.error("删除文章异常", e);
            return Result.error("删除文章异常: " + e.getMessage());
        }
    }

    @PostMapping("/like/{id}")
    @ApiOperation(value = "点赞文章", notes = "点赞文章")
    public Result<Boolean> like(@PathVariable String id, HttpServletRequest request) {
        try {
            String userId = request.getHeader("X-User-Id");
            if (userId == null) {
                return Result.error("请先登录");
            }

            boolean success = articleService.likeArticle(id, userId);
            if (success) {
                return Result.ok(true);
            }
            return Result.error("您已经点赞过了");
        } catch (Exception e) {
            log.error("点赞文章异常", e);
            return Result.error("点赞文章异常: " + e.getMessage());
        }
    }

    @DeleteMapping("/like/{id}")
    @ApiOperation(value = "取消点赞", notes = "取消点赞")
    public Result<Boolean> unlike(@PathVariable String id, HttpServletRequest request) {
        try {
            String userId = request.getHeader("X-User-Id");
            if (userId == null) {
                return Result.error("请先登录");
            }

            boolean success = articleService.unlikeArticle(id, userId);
            if (success) {
                return Result.ok(true);
            }
            return Result.error("取消点赞失败");
        } catch (Exception e) {
            log.error("取消点赞异常", e);
            return Result.error("取消点赞异常: " + e.getMessage());
        }
    }

    @PostMapping("/collect/{id}")
    @ApiOperation(value = "收藏文章", notes = "收藏文章")
    public Result<Boolean> collect(@PathVariable String id, HttpServletRequest request) {
        try {
            String userId = request.getHeader("X-User-Id");
            if (userId == null) {
                return Result.error("请先登录");
            }

            boolean success = articleService.collectArticle(id, userId);
            if (success) {
                return Result.ok(true);
            }
            return Result.error("您已经收藏过了");
        } catch (Exception e) {
            log.error("收藏文章异常", e);
            return Result.error("收藏文章异常: " + e.getMessage());
        }
    }

    @DeleteMapping("/collect/{id}")
    @ApiOperation(value = "取消收藏", notes = "取消收藏")
    public Result<Boolean> uncollect(@PathVariable String id, HttpServletRequest request) {
        try {
            String userId = request.getHeader("X-User-Id");
            if (userId == null) {
                return Result.error("请先登录");
            }

            boolean success = articleService.uncollectArticle(id, userId);
            if (success) {
                return Result.ok(true);
            }
            return Result.error("取消收藏失败");
        } catch (Exception e) {
            log.error("取消收藏异常", e);
            return Result.error("取消收藏异常: " + e.getMessage());
        }
    }

    @PostMapping("/batch-publish")
    @ApiOperation(value = "批量发布文章", notes = "批量发布文章")
    public Result<Integer> batchPublish(@RequestBody List<PsiBlogArticle> articles, HttpServletRequest request) {
        try {
            String userId = request.getHeader("X-User-Id");
            String username = request.getHeader("X-Username");

            if (userId == null) {
                return Result.error("请先登录");
            }

            int successCount = 0;
            for (PsiBlogArticle article : articles) {
                article.setAuthorId(userId);
                article.setAuthorName(username);
                article.setCreateBy(username);
                article.setStatus("1");
                article.setPublishTime(new Date());
                article.setViewCount(0);
                article.setLikeCount(0);

                boolean success = articleService.saveArticle(article);
                if (success) {
                    successCount++;
                }
            }

            return Result.ok(successCount);
        } catch (Exception e) {
            log.error("批量发布文章异常", e);
            return Result.error("批量发布文章异常: " + e.getMessage());
        }
    }
}
