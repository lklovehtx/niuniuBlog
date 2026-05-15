package org.jeecg.modules.psi.blog.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.psi.blog.service.IAvatarService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
public class AvatarServiceImpl implements IAvatarService {

    private static final String AVATAR_ROOT = "avatar";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Override
    public String uploadAvatar(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String newFilename = UUID.randomUUID().toString() + extension;
        String datePath = LocalDate.now().format(DATE_FORMATTER);

        Path storagePath = Paths.get(AVATAR_ROOT, datePath);
        if (!Files.exists(storagePath)) {
            Files.createDirectories(storagePath);
        }

        Path filePath = storagePath.resolve(newFilename);
        Files.write(filePath, file.getBytes());

        return datePath + "/" + newFilename;
    }

    @Override
    public void deleteAvatar(String avatarPath) throws IOException {
        if (avatarPath == null || avatarPath.isEmpty()) {
            return;
        }
        Path filePath = Paths.get(AVATAR_ROOT, avatarPath);
        Files.deleteIfExists(filePath);
    }

    @Override
    public String getAvatarUrl(String avatarPath) {
        if (avatarPath == null || avatarPath.isEmpty()) {
            return null;
        }
        return "/jeecg-boot/avatar/" + avatarPath;
    }
}