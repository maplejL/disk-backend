package com.cslg.disk.example.file.controller;

import com.cslg.disk.common.ResponseMessage;
import com.cslg.disk.example.file.dao.FileDao;
import com.cslg.disk.example.file.dto.SearchPageDto;
import com.cslg.disk.example.file.entity.MyFile;
import com.cslg.disk.example.file.service.FileService;
import com.cslg.disk.example.user.anno.UserLoginToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    private FileService fileService;

    @Autowired
    private FileDao fileDao;

    //分页获取文件
    @PostMapping("/getPage")
    @UserLoginToken
    public Map<String, Object> getFile(@RequestBody SearchPageDto searchPageDto) {
        return fileService.getFile(searchPageDto);
    }

    //获取全部文件
    @GetMapping("/getAll")
    @UserLoginToken
    public Iterable<MyFile> getFile() {
        return fileService.getFile();
    }

    @PostMapping("/upload")
    @UserLoginToken
    public ResponseMessage uploadFile(@RequestParam(value = "file") MultipartFile file,
                                      @RequestParam(value = "typeCode") int typeCode,
                                      @RequestParam(value = "targetFilePath") String targetFilePath) {
        return ResponseMessage.success(fileService.uploadFile(file, typeCode, targetFilePath));
    }

    @GetMapping("/download")
    @UserLoginToken
    public ResponseMessage downloadFile(@RequestParam(value = "url") String url,
                                        HttpServletResponse response) throws IOException {
        return ResponseMessage.success(fileService.downloadFile(url,"E:\\毕设\\disk-code-backend\\src\\main\\resources\\static"));
    }
}
