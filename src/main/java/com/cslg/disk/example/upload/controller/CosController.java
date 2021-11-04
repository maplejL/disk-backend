package com.cslg.disk.example.upload.controller;


import com.cslg.disk.utils.TencentCOSUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: zh
 * @Date: 2020/6/5 16:48
 */
@RestController
@RequestMapping("/cos")
public class CosController {

    @PostMapping(value = "/upload")
    public Object upload(@RequestParam(value = "file") MultipartFile file){
        if (file == null){
            return "上传文件为空";
        }
        String uploadfile = TencentCOSUtil.uploadfile(file);
        return uploadfile;
    }
}

