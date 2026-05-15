package org.jeecg.modules.psi.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.psi.blog.entity.PsiBlogArticle;

public interface IPsiBlogArticleService extends IService<PsiBlogArticle> {

    IPage<PsiBlogArticle> queryPageList(Page<PsiBlogArticle> page, PsiBlogArticle article);

    IPage<PsiBlogArticle> queryByAuthor(Page<PsiBlogArticle> page, String authorId);

    PsiBlogArticle queryById(String id);

    boolean saveArticle(PsiBlogArticle article);

    boolean updateArticle(PsiBlogArticle article);

    boolean deleteArticle(String id);

    void increaseViewCount(String id);

    boolean likeArticle(String id, String userId);

    boolean unlikeArticle(String id, String userId);

    boolean collectArticle(String id, String userId);

    boolean uncollectArticle(String id, String userId);

    boolean isLiked(String id, String userId);

    boolean isCollected(String id, String userId);
}
