package org.jeecg.modules.psi.blog.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IAvatarService {

    String uploadAvatar(MultipartFile file) throws IOException;

    void deleteAvatar(String avatarPath) throws IOException;

    String getAvatarUrl(String avatarPath);
}