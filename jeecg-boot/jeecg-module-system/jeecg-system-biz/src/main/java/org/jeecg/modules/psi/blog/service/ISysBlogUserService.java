package org.jeecg.modules.psi.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.psi.blog.entity.SysBlogUser;

import java.util.Map;

public interface ISysBlogUserService extends IService<SysBlogUser> {

    SysBlogUser selectByUsername(String username);

    SysBlogUser selectById(String id);

    SysBlogUser register(SysBlogUser user);

    SysBlogUser login(String username, String password);

    boolean updateUserInfo(SysBlogUser user);

    boolean changePassword(String userId, String oldPassword, String newPassword);

    Map<String, Object> getUserStats(String userId);
}
