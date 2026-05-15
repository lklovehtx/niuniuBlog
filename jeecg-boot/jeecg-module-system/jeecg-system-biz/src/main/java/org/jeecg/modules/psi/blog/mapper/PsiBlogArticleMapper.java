package org.jeecg.modules.psi.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.psi.blog.entity.PsiBlogArticle;

public interface PsiBlogArticleMapper extends BaseMapper<PsiBlogArticle> {

    IPage<PsiBlogArticle> queryPageList(Page<PsiBlogArticle> page, @Param("query") PsiBlogArticle article);

    IPage<PsiBlogArticle> queryByAuthor(Page<PsiBlogArticle> page, @Param("authorId") String authorId);

    PsiBlogArticle selectById(@Param("id") String id);

    void increaseViewCount(@Param("id") String id);

    void increaseLikeCount(@Param("id") String id);

    void decreaseLikeCount(@Param("id") String id);

    void increaseCommentCount(@Param("id") String id);

    void increaseCollectCount(@Param("id") String id);

    void decreaseCollectCount(@Param("id") String id);
}
