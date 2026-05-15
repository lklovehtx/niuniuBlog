package org.jeecg.modules.psi.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.psi.blog.entity.SysBlogUser;
import org.jeecg.modules.psi.blog.mapper.SysBlogUserMapper;
import org.jeecg.modules.psi.blog.service.ISysBlogUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.hutool.crypto.digest.BCrypt;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service("sysBlogUserService")
@Slf4j
public class SysBlogUserServiceImpl extends ServiceImpl<SysBlogUserMapper, SysBlogUser> implements ISysBlogUserService {

    @Autowired
    private SysBlogUserMapper sysBlogUserMapper;

    @Override
    public SysBlogUser selectByUsername(String username) {
        return sysBlogUserMapper.selectByUsername(username);
    }

    @Override
    public SysBlogUser selectById(String id) {
        return sysBlogUserMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysBlogUser register(SysBlogUser user) {
        user.setDelFlag("0");
        user.setStatus("1");
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        this.save(user);
        return user;
    }

    @Override
    public SysBlogUser login(String username, String password) {
        SysBlogUser user = sysBlogUserMapper.selectByUsername(username);
        if (user == null) {
            return null;
        }

        if (!"1".equals(user.getStatus())) {
            return null;
        }

        if (!"0".equals(user.getDelFlag())) {
            return null;
        }

        if (BCrypt.checkpw(password, user.getPassword())) {
            return user;
        }

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserInfo(SysBlogUser user) {
        if (user.getId() == null) {
            return false;
        }
        SysBlogUser existUser = this.getById(user.getId());
        if (existUser == null) {
            return false;
        }

        existUser.setNickname(user.getNickname());
        existUser.setEmail(user.getEmail());
        existUser.setPhone(user.getPhone());
        existUser.setBio(user.getBio());
        existUser.setUpdateBy(user.getUpdateBy());
        existUser.setUpdateTime(new Date());

        return this.updateById(existUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        SysBlogUser user = this.getById(userId);
        if (user == null) {
            return false;
        }

        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            return false;
        }

        user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        user.setUpdateTime(new Date());
        return this.updateById(user);
    }

    @Override
    public Map<String, Object> getUserStats(String userId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 文章数
        Integer articleCount = sysBlogUserMapper.countArticlesByUserId(userId);
        stats.put("articleCount", articleCount != null ? articleCount : 0);
        
        // 获赞数（所有文章的点赞数总和）
        Integer likeCount = sysBlogUserMapper.sumLikesByUserId(userId);
        stats.put("likeCount", likeCount != null ? likeCount : 0);
        
        // 关注数（当前默认返回0，如需实现关注功能需要创建关注表）
        stats.put("followerCount", 0);
        
        return stats;
    }
}
