package org.jeecg.modules.psi.blog.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.psi.blog.service.ISysMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/psi/blog/mail")
@Api(tags = "邮件服务接口")
public class SysMailController {

    @Autowired
    private ISysMailService mailService;

    @PostMapping("/sendVerificationCode")
    @ApiOperation(value = "发送验证码", notes = "发送邮箱验证码")
    public Result<Map<String, Object>> sendVerificationCode(@RequestBody Map<String, String> requestData) {
        try {
            String email = requestData.get("email");
            if (email == null || email.trim().isEmpty()) {
                return Result.error("邮箱地址不能为空");
            }

            if (!isValidEmail(email)) {
                return Result.error("邮箱格式不正确");
            }

            log.info("发送验证码到邮箱: {}", email);
            Map<String, Object> result = mailService.generateVerificationCode(email);

            if ((boolean) result.get("success")) {
                Result<Map<String, Object>> r = Result.ok(result);
                r.setMessage("验证码已发送");
                return r;
            } else {
                return Result.error("验证码发送失败，请稍后重试");
            }
        } catch (Exception e) {
            log.error("发送验证码异常: {}", e.getMessage());
            return Result.error("发送验证码异常: " + e.getMessage());
        }
    }

    @PostMapping("/verifyCode")
    @ApiOperation(value = "验证验证码", notes = "验证邮箱验证码")
    public Result<Object> verifyCode(@RequestBody Map<String, String> requestData) {
        try {
            String email = requestData.get("email");
            String code = requestData.get("code");

            if (email == null || email.trim().isEmpty()) {
                return Result.error("邮箱地址不能为空");
            }

            if (code == null || code.trim().isEmpty()) {
                return Result.error("验证码不能为空");
            }

            log.info("验证验证码: email={}, code={}", email, code);
            boolean success = mailService.verifyCode(email, code);

            if (success) {
                return Result.ok("验证成功");
            } else {
                return Result.error("验证码错误或已过期");
            }
        } catch (Exception e) {
            log.error("验证验证码异常: {}", e.getMessage());
            return Result.error("验证验证码异常: " + e.getMessage());
        }
    }

    @PostMapping("/sendEmail")
    @ApiOperation(value = "发送邮件", notes = "发送普通邮件")
    public Result<Object> sendEmail(@RequestBody Map<String, String> requestData) {
        try {
            String to = requestData.get("to");
            String subject = requestData.get("subject");
            String content = requestData.get("content");

            if (to == null || to.trim().isEmpty()) {
                return Result.error("收件人邮箱不能为空");
            }

            if (subject == null || subject.trim().isEmpty()) {
                return Result.error("邮件主题不能为空");
            }

            if (content == null || content.trim().isEmpty()) {
                return Result.error("邮件内容不能为空");
            }

            log.info("发送邮件: to={}, subject={}", to, subject);
            boolean success = mailService.sendEmail(to, subject, content);

            if (success) {
                return Result.ok("邮件发送成功");
            } else {
                return Result.error("邮件发送失败");
            }
        } catch (Exception e) {
            log.error("发送邮件异常: {}", e.getMessage());
            return Result.error("发送邮件异常: " + e.getMessage());
        }
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String regex = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        return email.matches(regex);
    }
}