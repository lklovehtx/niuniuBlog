package org.jeecg.modules.psi.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.psi.blog.entity.PsiBlogComment;

import java.util.List;

public interface IPsiBlogCommentService extends IService<PsiBlogComment> {

    IPage<PsiBlogComment> queryByArticle(Page<PsiBlogComment> page, String articleId);

    List<PsiBlogComment> queryReplies(List<String> parentIds);

    boolean saveComment(PsiBlogComment comment);

    boolean deleteComment(String id);

    boolean likeComment(String id);
}
