package com.aide.infrastructure;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    
    /**
     * 保存文件并返回访问路径
     * @param file 上传的文件
     * @param directory 目标目录
     * @return 文件访问URL
     */
    String saveFile(MultipartFile file, String directory) throws IOException;
    
    /**
     * 删除文件
     * @param filePath 文件路径
     */
    void deleteFile(String filePath);
}
