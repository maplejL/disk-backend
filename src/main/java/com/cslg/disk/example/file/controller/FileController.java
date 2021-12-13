package com.cslg.disk.example.file.controller;

import com.cslg.disk.common.ResponseMessage;
import com.cslg.disk.example.file.dto.SearchPageDto;
import com.cslg.disk.example.file.entity.File;
import com.cslg.disk.example.file.entity.Picture;
import com.cslg.disk.example.file.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    private FileService fileService;

    //分页获取文件
    @PostMapping("/getPage")
    public Iterable<File> getFile(@RequestBody SearchPageDto searchPageDto) {
        return fileService.getFile(searchPageDto);
    }

    //获取全部文件
    @GetMapping("/getAll")
    public Iterable<File> getFile() {
        return fileService.getFile();
    }

    @PostMapping("/upload")
    public ResponseMessage uploadPicture(@RequestParam(value = "file") MultipartFile file,
                                         @RequestParam(value = "typeCode") int typeCode,
                                         @RequestParam(value = "targetFilePath") String targetFilePath) {
        return ResponseMessage.success(fileService.uploadFile(file, typeCode, targetFilePath));
    }
}
