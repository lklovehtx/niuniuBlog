package org.jeecg.modules.psi.blog.service;

import java.util.Map;

public interface ISysMailService {

    boolean sendEmail(String to, String subject, String content);

    boolean sendVerificationEmail(String to, String verificationCode);

    boolean verifyCode(String to, String code);

    boolean checkVerified(String to);

    Map<String, Object> generateVerificationCode(String to);
}