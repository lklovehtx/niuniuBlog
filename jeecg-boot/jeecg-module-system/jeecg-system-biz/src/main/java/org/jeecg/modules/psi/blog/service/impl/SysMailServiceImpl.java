package org.jeecg.modules.psi.blog.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.psi.blog.service.ISysMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SysMailServiceImpl implements ISysMailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Value("${psi.blog.mail.mock:false}")
    private boolean mockMail;

    private static final Map<String, VerificationCode> verificationCodes = new ConcurrentHashMap<>();

    private static class VerificationCode {
        String code;
        long createTime;
        int verifyCount;
        boolean verified;

        VerificationCode(String code) {
            this.code = code;
            this.createTime = System.currentTimeMillis();
            this.verifyCount = 0;
            this.verified = false;
        }
    }

    @Override
    public boolean sendEmail(String to, String subject, String content) {
        if (mockMail || mailSender == null || fromEmail == null || fromEmail.isEmpty()) {
            log.info("【模拟发送】邮件: to={}, subject={}, content={}", to, subject, content);
            return true;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            log.info("邮件发送成功: to={}, subject={}", to, subject);
            return true;
        } catch (Exception e) {
            log.error("邮件发送失败: to={}, error={}", to, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean sendVerificationEmail(String to, String verificationCode) {
        String subject = "博客系统邮箱验证";
        String content = buildVerificationEmailContent(verificationCode);
        return sendEmail(to, subject, content);
    }

    @Override
    public boolean verifyCode(String to, String code) {
        VerificationCode verification = verificationCodes.get(to);
        if (verification == null) {
            log.warn("验证码不存在: to={}", to);
            return false;
        }

        long currentTime = System.currentTimeMillis();
        long expireTime = 15 * 60 * 1000;
        if (currentTime - verification.createTime > expireTime) {
            verificationCodes.remove(to);
            log.warn("验证码已过期: to={}", to);
            return false;
        }

        if (verification.code.equals(code)) {
            verification.verified = true;
            log.info("验证码验证成功: to={}", to);
            return true;
        }

        verification.verifyCount++;
        log.warn("验证码错误: to={}, verifyCount={}", to, verification.verifyCount);
        return false;
    }

    @Override
    public boolean checkVerified(String to) {
        VerificationCode verification = verificationCodes.get(to);
        if (verification == null) {
            log.warn("验证码不存在: to={}", to);
            return false;
        }

        long currentTime = System.currentTimeMillis();
        long expireTime = 15 * 60 * 1000;
        if (currentTime - verification.createTime > expireTime) {
            verificationCodes.remove(to);
            log.warn("验证码已过期: to={}", to);
            return false;
        }

        if (verification.verified) {
            verificationCodes.remove(to);
            log.info("注册验证通过: to={}", to);
            return true;
        }

        log.warn("邮箱未验证: to={}", to);
        return false;
    }

    @Override
    public Map<String, Object> generateVerificationCode(String to) {
        Random random = new Random();
        String code = String.format("%06d", random.nextInt(1000000));

        VerificationCode verification = new VerificationCode(code);
        verificationCodes.put(to, verification);

        boolean sent = sendVerificationEmail(to, code);

        Map<String, Object> result = new ConcurrentHashMap<>();
        result.put("success", sent);
        result.put("email", to);
        result.put("message", sent ? "验证码已发送" : "验证码发送失败");
        
        if (mockMail) {
            result.put("code", code);
        }

        return result;
    }

    private String buildVerificationEmailContent(String verificationCode) {
        return String.format("【博客系统】您的邮箱验证码是：%s\n\n验证码有效期为15分钟，请及时使用。", verificationCode);
    }
}