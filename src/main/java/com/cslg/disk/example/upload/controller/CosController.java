//package com.cslg.disk.example.upload.controller;
//
//
//import com.cslg.disk.example.file.util.FileUtil;
//import com.cslg.disk.example.picture.dto.UploadPictureDto;
//import com.cslg.disk.example.picture.entity.Picture;
//import com.cslg.disk.example.picture.service.PictureService;
//import com.cslg.disk.utils.TencentCOSUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.multipart.support.MultipartFilter;
//
//import java.text.DecimalFormat;
//
///**
// * @Author: zh
// * @Date: 2020/6/5 16:48
// */
//@RestController
//@RequestMapping("/cos")
//public class CosController {
//    @Autowired
//    private PictureService pictureService;
//
//    FileUtil fileUtil = new FileUtil();
//    @PostMapping(value = "/upload")
//    public Object upload(@RequestParam(value = "file") MultipartFile file){
//        if (file == null){
//            return "上传文件为空";
//        }
//        String uploadfile = TencentCOSUtil.uploadfile(file);
//        long size = file.getSize();
//        String fileSize = fileUtil.getSize(size);
//        UploadPictureDto uploadPictureDto = new UploadPictureDto();
//        uploadPictureDto.setImgUrl(uploadfile);
//        uploadPictureDto.setSize(fileSize);
//        pictureService.uploadPicture(uploadPictureDto);
//        return uploadfile;
//    }
//
//
//}
//
