package com.cslg.disk.example.file.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadFileDto {
    private String imgUrl;

    private String size;

    private MultipartFile file;
}
