package org.jeecg.modules.psi.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.psi.blog.entity.PsiBlogComment;
import org.jeecg.modules.psi.blog.service.IPsiBlogCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/psi/blog/comment")
@Api(tags = "博客评论接口")
public class PsiBlogCommentController {

    @Autowired
    private IPsiBlogCommentService commentService;

    @GetMapping("/list/{articleId}")
    @ApiOperation(value = "获取评论列表", notes = "获取评论列表")
    public Result<List<PsiBlogComment>> list(@PathVariable String articleId,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        try {
            Page<PsiBlogComment> page = new Page<>(pageNo, pageSize);
            IPage<PsiBlogComment> result = commentService.queryByArticle(page, articleId);
            return Result.ok(result.getRecords());
        } catch (Exception e) {
            log.error("获取评论列表异常", e);
            return Result.error("获取评论列表异常: " + e.getMessage());
        }
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加评论", notes = "添加评论")
    public Result<PsiBlogComment> add(@RequestBody PsiBlogComment comment, HttpServletRequest request) {
        try {
            String userId = request.getHeader("X-User-Id");
            String username = request.getHeader("X-Username");

            if (userId == null) {
                return Result.error("请先登录");
            }

            comment.setCommentatorId(userId);
            comment.setCommentatorName(username);
            comment.setCreateBy(username);

            boolean success = commentService.saveComment(comment);
            if (success) {
                return Result.ok(comment);
            }
            return Result.error("评论失败");
        } catch (Exception e) {
            log.error("添加评论异常", e);
            return Result.error("添加评论异常: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除评论", notes = "删除评论")
    public Result<Boolean> delete(@PathVariable String id, HttpServletRequest request) {
        try {
            String userId = request.getHeader("X-User-Id");
            if (userId == null) {
                return Result.error("请先登录");
            }

            boolean success = commentService.deleteComment(id);
            if (success) {
                return Result.ok(true);
            }
            return Result.error("删除失败");
        } catch (Exception e) {
            log.error("删除评论异常", e);
            return Result.error("删除评论异常: " + e.getMessage());
        }
    }

    @PostMapping("/like/{id}")
    @ApiOperation(value = "点赞评论", notes = "点赞评论")
    public Result<Boolean> like(@PathVariable String id) {
        try {
            boolean success = commentService.likeComment(id);
            if (success) {
                return Result.ok(true);
            }
            return Result.error("点赞失败");
        } catch (Exception e) {
            log.error("点赞评论异常", e);
            return Result.error("点赞评论异常: " + e.getMessage());
        }
    }
}
