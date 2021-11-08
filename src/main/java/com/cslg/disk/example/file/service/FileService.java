package com.cslg.disk.example.file.service;

import com.cslg.disk.example.file.entity.File;
import com.cslg.disk.example.file.entity.Picture;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    public Iterable<File> getFile();

    public Iterable<File> getFile(int pageSize,int typeCode);

    public File uploadFile(MultipartFile file,int typeCode, String targetFilePath);
}
