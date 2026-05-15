package org.jeecg.modules.psi.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.psi.blog.entity.PsiBlogComment;

import java.util.List;

public interface PsiBlogCommentMapper extends BaseMapper<PsiBlogComment> {

    IPage<PsiBlogComment> queryByArticle(Page<PsiBlogComment> page, @Param("articleId") String articleId);

    List<PsiBlogComment> queryByParentId(@Param("parentId") String parentId);

    List<PsiBlogComment> queryRepliesByParentIds(@Param("parentIds") List<String> parentIds);

    void deleteByArticleId(@Param("articleId") String articleId);
}
