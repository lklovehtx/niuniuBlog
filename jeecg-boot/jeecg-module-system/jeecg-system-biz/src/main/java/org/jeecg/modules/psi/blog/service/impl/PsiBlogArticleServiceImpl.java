package org.jeecg.modules.psi.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.psi.blog.entity.PsiBlogArticle;
import org.jeecg.modules.psi.blog.entity.PsiBlogCollect;
import org.jeecg.modules.psi.blog.entity.PsiBlogLike;
import org.jeecg.modules.psi.blog.mapper.PsiBlogArticleMapper;
import org.jeecg.modules.psi.blog.service.IPsiBlogArticleService;
import org.jeecg.modules.psi.blog.mapper.PsiBlogCollectMapper;
import org.jeecg.modules.psi.blog.mapper.PsiBlogLikeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service("psiBlogArticleService")
@Slf4j
public class PsiBlogArticleServiceImpl extends ServiceImpl<PsiBlogArticleMapper, PsiBlogArticle> implements IPsiBlogArticleService {

    @Autowired
    private PsiBlogArticleMapper psiBlogArticleMapper;

    @Autowired
    private PsiBlogLikeMapper psiBlogLikeMapper;

    @Autowired
    private PsiBlogCollectMapper psiBlogCollectMapper;

    @Override
    public IPage<PsiBlogArticle> queryPageList(Page<PsiBlogArticle> page, PsiBlogArticle article) {
        return psiBlogArticleMapper.queryPageList(page, article);
    }

    @Override
    public IPage<PsiBlogArticle> queryByAuthor(Page<PsiBlogArticle> page, String authorId) {
        return psiBlogArticleMapper.queryByAuthor(page, authorId);
    }

    @Override
    public PsiBlogArticle queryById(String id) {
        return psiBlogArticleMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveArticle(PsiBlogArticle article) {
        if (article.getId() == null || article.getId().isEmpty()) {
            article.setId(UUID.randomUUID().toString());
        }
        article.setDelFlag("0");
        article.setViewCount(0);
        article.setLikeCount(0);
        article.setCommentCount(0);
        article.setCollectCount(0);

        if (article.getStatus() == null) {
            article.setStatus("0");
        }
        if ("1".equals(article.getStatus()) && article.getPublishTime() == null) {
            article.setPublishTime(new Date());
        }

        return this.save(article);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateArticle(PsiBlogArticle article) {
        article.setUpdateTime(new Date());
        return this.updateById(article);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteArticle(String id) {
        PsiBlogArticle article = this.getById(id);
        if (article != null) {
            article.setDelFlag("1");
            return this.updateById(article);
        }
        return false;
    }

    @Override
    public void increaseViewCount(String id) {
        psiBlogArticleMapper.increaseViewCount(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean likeArticle(String id, String userId) {
        LambdaQueryWrapper<PsiBlogLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PsiBlogLike::getArticleId, id)
               .eq(PsiBlogLike::getUserId, userId);
        PsiBlogLike existingLike = psiBlogLikeMapper.selectOne(wrapper);

        if (existingLike != null) {
            return false;
        }

        PsiBlogLike like = new PsiBlogLike();
        like.setId(UUID.randomUUID().toString());
        like.setUserId(userId);
        like.setArticleId(id);
        like.setCreateTime(new Date());
        psiBlogLikeMapper.insert(like);

        psiBlogArticleMapper.increaseLikeCount(id);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unlikeArticle(String id, String userId) {
        LambdaQueryWrapper<PsiBlogLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PsiBlogLike::getArticleId, id)
               .eq(PsiBlogLike::getUserId, userId);
        int deleted = psiBlogLikeMapper.delete(wrapper);

        if (deleted > 0) {
            psiBlogArticleMapper.decreaseLikeCount(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean collectArticle(String id, String userId) {
        LambdaQueryWrapper<PsiBlogCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PsiBlogCollect::getArticleId, id)
               .eq(PsiBlogCollect::getUserId, userId);
        PsiBlogCollect existingCollect = psiBlogCollectMapper.selectOne(wrapper);

        if (existingCollect != null) {
            return false;
        }

        PsiBlogCollect collect = new PsiBlogCollect();
        collect.setId(UUID.randomUUID().toString());
        collect.setUserId(userId);
        collect.setArticleId(id);
        collect.setCreateTime(new Date());
        psiBlogCollectMapper.insert(collect);

        psiBlogArticleMapper.increaseCollectCount(id);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean uncollectArticle(String id, String userId) {
        LambdaQueryWrapper<PsiBlogCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PsiBlogCollect::getArticleId, id)
               .eq(PsiBlogCollect::getUserId, userId);
        int deleted = psiBlogCollectMapper.delete(wrapper);

        if (deleted > 0) {
            psiBlogArticleMapper.decreaseCollectCount(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean isLiked(String id, String userId) {
        LambdaQueryWrapper<PsiBlogLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PsiBlogLike::getArticleId, id)
               .eq(PsiBlogLike::getUserId, userId);
        return psiBlogLikeMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean isCollected(String id, String userId) {
        LambdaQueryWrapper<PsiBlogCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PsiBlogCollect::getArticleId, id)
               .eq(PsiBlogCollect::getUserId, userId);
        return psiBlogCollectMapper.selectCount(wrapper) > 0;
    }
}
