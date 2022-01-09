package com.cslg.disk.example.file.service;

import com.cslg.disk.example.file.dto.SearchPageDto;
import com.cslg.disk.example.file.entity.File;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface FileService {
    public List<File> getFile();

    public Map<String, Object> getFile(SearchPageDto searchPageDto);

    public File uploadFile(MultipartFile file,int typeCode, String targetFilePath);
}
