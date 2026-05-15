package org.jeecg.modules.psi.blog.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.PasswordUtil;
import org.jeecg.modules.psi.blog.entity.SysBlogUser;
import org.jeecg.modules.psi.blog.service.IAvatarService;
import org.jeecg.modules.psi.blog.service.ISysBlogUserService;
import org.jeecg.modules.psi.blog.service.ISysMailService;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/psi/blog/user")
@Api(tags = "博客用户接口")
public class SysBlogUserController {

    private static final Logger log = LoggerFactory.getLogger(SysBlogUserController.class);

    @Autowired
    private ISysBlogUserService userService;

    @Autowired
    private ISysMailService mailService;

    @Autowired
    private IAvatarService avatarService;

    @PostMapping("/register")
    @ApiOperation(value = "用户注册", notes = "用户注册")
    public Result<Map<String, Object>> register(@RequestBody Map<String, String> requestData) {
        try {
            String username = requestData.get("username");
            String password = requestData.get("password");
            String nickname = requestData.get("nickname");
            String email = requestData.get("email");
            String verificationCode = requestData.get("verificationCode");

            if (username == null || username.trim().isEmpty()) {
                return Result.error("用户名不能为空");
            }
            if (password == null || password.trim().isEmpty()) {
                return Result.error("密码不能为空");
            }
            if (email == null || email.trim().isEmpty()) {
                return Result.error("邮箱不能为空");
            }
            if (verificationCode == null || verificationCode.trim().isEmpty()) {
                return Result.error("验证码不能为空");
            }

            // 验证邮箱是否已验证
            boolean verified = mailService.checkVerified(email);
            if (!verified) {
                return Result.error("邮箱未验证或验证已过期");
            }

            // 检查用户名是否已存在
            SysBlogUser existUser = userService.selectByUsername(username);
            if (existUser != null) {
                return Result.error("用户名已存在");
            }

            // 创建新用户
            SysBlogUser newUser = new SysBlogUser();
            newUser.setUsername(username);
            newUser.setPassword(password); // 实际生产环境应该加密
            newUser.setNickname(nickname != null && !nickname.isEmpty() ? nickname : username);
            newUser.setEmail(email);
            newUser.setEmailVerified(true);
            newUser.setCreateTime(new Date());

            SysBlogUser registeredUser = userService.register(newUser);
            if (registeredUser != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("id", registeredUser.getId());
                result.put("username", registeredUser.getUsername());
                result.put("nickname", registeredUser.getNickname());
                result.put("email", registeredUser.getEmail());
                Result<Map<String, Object>> r = Result.ok(result);
                r.setMessage("注册成功");
                return r;
            }
            return Result.error("注册失败");
        } catch (Exception e) {
            log.error("注册异常", e);
            return Result.error("注册异常: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    @ApiOperation(value = "用户登录", notes = "用户登录")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginData, HttpServletRequest request) {
        try {
            String username = loginData.get("username");
            String password = loginData.get("password");

            if (username == null || password == null) {
                return Result.error("用户名和密码不能为空");
            }

            SysBlogUser user = userService.login(username, password);
            if (user != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("id", user.getId());
                result.put("username", user.getUsername());
                result.put("nickname", user.getNickname());
                // 将avatar相对路径转换为可访问的URL
                String avatarUrl = user.getAvatar() != null && !user.getAvatar().isEmpty() 
                    ? avatarService.getAvatarUrl(user.getAvatar()) 
                    : null;
                result.put("avatar", avatarUrl);
                result.put("email", user.getEmail());
                result.put("emailVerified", user.getEmailVerified());
                Result<Map<String, Object>> r = Result.ok(result);
                r.setMessage("登录成功");
                return r;
            }
            return Result.error("用户名或密码错误");
        } catch (Exception e) {
            log.error("登录异常", e);
            return Result.error("登录异常: " + e.getMessage());
        }
    }

    @GetMapping("/info/{id}")
    @ApiOperation(value = "获取用户信息", notes = "根据用户ID获取用户信息")
    public Result<SysBlogUser> getUserInfo(@PathVariable("id") String id) {
        try {
            SysBlogUser user = userService.getById(id);
            if (user != null) {
                // 密码字段置空，不返回给前端
                user.setPassword(null);
                // 将avatar相对路径转换为可访问的URL
                if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                    user.setAvatar(avatarService.getAvatarUrl(user.getAvatar()));
                }
                return Result.ok(user);
            }
            return Result.error("用户不存在");
        } catch (Exception e) {
            log.error("获取用户信息异常", e);
            return Result.error("获取用户信息异常: " + e.getMessage());
        }
    }

    @GetMapping("/stats/{id}")
    @ApiOperation(value = "获取用户统计数据", notes = "获取用户的文章数、关注数、获赞数")
    public Result<Map<String, Object>> getUserStats(@PathVariable("id") String id) {
        try {
            Map<String, Object> stats = userService.getUserStats(id);
            return Result.ok(stats);
        } catch (Exception e) {
            log.error("获取用户统计数据异常", e);
            return Result.error("获取用户统计数据异常: " + e.getMessage());
        }
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新用户信息", notes = "更新用户信息")
    public Result<Boolean> updateUserInfo(@RequestBody SysBlogUser user, HttpServletRequest request) {
        try {
            // 设置更新人
            user.setUpdateBy(request.getParameter("username"));
            user.setUpdateTime(new Date());

            boolean result = userService.updateUserInfo(user);
            if (result) {
                return Result.ok(true);
            }
            return Result.error("更新失败");
        } catch (Exception e) {
            log.error("更新用户信息异常", e);
            return Result.error("更新用户信息异常: " + e.getMessage());
        }
    }

    @PostMapping("/avatar/upload")
    @ApiOperation(value = "上传头像", notes = "上传头像图片")
    public Result<Map<String, Object>> uploadAvatar(@RequestParam("file") MultipartFile file,
                                                    @RequestParam("userId") String userId) {
        try {
            if (file == null || file.isEmpty()) {
                return Result.error("请选择要上传的头像文件");
            }
            if (userId == null || userId.trim().isEmpty()) {
                return Result.error("用户ID不能为空");
            }

            String avatarPath = avatarService.uploadAvatar(file);
            String avatarUrl = avatarService.getAvatarUrl(avatarPath);

            SysBlogUser user = userService.getById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            user.setAvatar(avatarPath);
            user.setUpdateTime(new Date());
            userService.updateById(user);

            Map<String, Object> result = new HashMap<>();
            result.put("path", avatarPath);
            result.put("url", avatarUrl);

            Result<Map<String, Object>> r = Result.ok(result);
            r.setMessage("上传成功");
            return r;
        } catch (Exception e) {
            log.error("上传头像异常", e);
            return Result.error("上传头像异常: " + e.getMessage());
        }
    }
}