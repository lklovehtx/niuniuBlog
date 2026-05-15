package org.jeecg.modules.psi.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.psi.blog.entity.PsiBlogComment;
import org.jeecg.modules.psi.blog.entity.SysBlogUser;
import org.jeecg.modules.psi.blog.mapper.PsiBlogCommentMapper;
import org.jeecg.modules.psi.blog.service.IAvatarService;
import org.jeecg.modules.psi.blog.service.IPsiBlogCommentService;
import org.jeecg.modules.psi.blog.service.ISysBlogUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service("psiBlogCommentService")
@Slf4j
public class PsiBlogCommentServiceImpl extends ServiceImpl<PsiBlogCommentMapper, PsiBlogComment> implements IPsiBlogCommentService {

    @Autowired
    private PsiBlogCommentMapper psiBlogCommentMapper;

    @Autowired
    private ISysBlogUserService userService;

    @Autowired
    private IAvatarService avatarService;

    @Override
    public IPage<PsiBlogComment> queryByArticle(Page<PsiBlogComment> page, String articleId) {
        IPage<PsiBlogComment> result = psiBlogCommentMapper.queryByArticle(page, articleId);
        for (PsiBlogComment comment : result.getRecords()) {
            fillCommentatorAvatar(comment);
        }
        return result;
    }

    private void fillCommentatorAvatar(PsiBlogComment comment) {
        if (comment.getCommentatorId() != null && !comment.getCommentatorId().isEmpty()) {
            SysBlogUser user = userService.selectById(comment.getCommentatorId());
            if (user != null && user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                comment.setCommentatorAvatar(avatarService.getAvatarUrl(user.getAvatar()));
            }
        }
    }

    @Override
    public List<PsiBlogComment> queryReplies(List<String> parentIds) {
        if (parentIds == null || parentIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<PsiBlogComment> replies = psiBlogCommentMapper.queryRepliesByParentIds(parentIds);
        for (PsiBlogComment reply : replies) {
            fillCommentatorAvatar(reply);
        }
        return replies;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveComment(PsiBlogComment comment) {
        comment.setDelFlag("0");
        comment.setStatus("1");
        comment.setLikeCount(0);

        if (comment.getParentId() == null || comment.getParentId().isEmpty()) {
            comment.setParentId(null);
        }

        return this.save(comment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComment(String id) {
        PsiBlogComment comment = this.getById(id);
        if (comment != null) {
            comment.setDelFlag("1");
            return this.updateById(comment);
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean likeComment(String id) {
        PsiBlogComment comment = this.getById(id);
        if (comment != null) {
            comment.setLikeCount(comment.getLikeCount() + 1);
            return this.updateById(comment);
        }
        return false;
    }
}
