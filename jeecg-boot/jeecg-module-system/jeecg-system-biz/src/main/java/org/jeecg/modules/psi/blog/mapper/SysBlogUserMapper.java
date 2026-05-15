package org.jeecg.modules.psi.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.psi.blog.entity.SysBlogUser;

public interface SysBlogUserMapper extends BaseMapper<SysBlogUser> {

    SysBlogUser selectByUsername(@Param("username") String username);

    SysBlogUser selectById(@Param("id") String id);

    Integer countArticlesByUserId(@Param("userId") String userId);

    Integer sumLikesByUserId(@Param("userId") String userId);
}
