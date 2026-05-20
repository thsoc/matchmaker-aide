package com.aide.infrastructure.impl;

import com.aide.infrastructure.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class NioFileStorageServiceImpl implements FileStorageService {
    
    @Value("${file.upload.path:/uploads}")
    private String basePath;

    @Override
    public String saveFile(MultipartFile file, String directory) throws IOException {
        // 创建完整的目录路径
        String fullDirectory = basePath + "/" + directory;
        // 创建目录
        Path uploadDir = Paths.get(fullDirectory);
        
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String fileName = UUID.randomUUID().toString() + fileExtension;
        Path filePath = uploadDir.resolve(fileName);

        // 使用NIO保存文件
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile());
             FileChannel channel = fos.getChannel()) {
            
            byte[] bytes = file.getBytes();
            channel.write(java.nio.ByteBuffer.wrap(bytes));
            // 强制刷新文件
            channel.force(true);
        }

        log.info("文件保存成功: {}", filePath);
        return "/" + directory + "/" + fileName;
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(basePath + filePath);
            Files.deleteIfExists(path);
            log.info("文件删除成功: {}", filePath);
        } catch (IOException e) {
            log.error("文件删除失败: {}", filePath, e);
        }
    }
}
