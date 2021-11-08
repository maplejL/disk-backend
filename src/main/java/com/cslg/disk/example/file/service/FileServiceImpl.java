package com.cslg.disk.example.file.service;

import com.cslg.disk.example.file.dao.ThumbnailDao;
import com.cslg.disk.example.file.entity.File;
import com.cslg.disk.example.file.entity.Thumbnail;
import com.cslg.disk.example.file.util.FileUtil;
import com.cslg.disk.example.file.dao.FileDao;
import com.cslg.disk.example.file.entity.Picture;
import com.cslg.disk.example.file.util.ImageUtil;
import com.cslg.disk.utils.TencentCOSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class FileServiceImpl implements FileService {
    @Autowired
    private FileDao fileDao;

    @Autowired
    private ThumbnailDao thumbnailDao;

    FileUtil fileUtil = new FileUtil();

    @Override
    public Iterable<File> getFile(int pageSize, int typeCode) {

        Iterable<File> fileList = fileDao.findByPage(pageSize, typeCode);
        if (typeCode == 1) {
            fileList.forEach(item -> {
                String thumbnailUrl = thumbnailDao.findByVideoUrl(item.getUrl());
                item.setThumbnailUrl(thumbnailUrl);
            });
        }
        return fileList;
    }

    @Override
    public Iterable<File> getFile() {
        return fileDao.findAll();
    }

    public File uploadFile(MultipartFile file, int typeCode, String targetFilePath) {

        if (file == null) {
            return null;
        }
        String uploadFilePath = TencentCOSUtil.uploadfile(file);
        long size = file.getSize();
        String fileSize = fileUtil.getSize(size);
        File uploadFile = new File();
        uploadFile.setFileName(file.getOriginalFilename());
        uploadFile.setSize(fileSize);
        uploadFile.setUrl(uploadFilePath);
        uploadFile.setTypeCode(typeCode);

        if (typeCode == 1) {
            ImageUtil imageUtil = new ImageUtil();
            try {
                imageUtil.randomGrabberFFmpegImage(uploadFilePath, targetFilePath, file.getOriginalFilename());
                Thumbnail thumbnail = new Thumbnail();
                thumbnail.setName(file.getOriginalFilename());
                thumbnail.setUrl(targetFilePath);
                thumbnail.setVideoUrl(uploadFilePath);
                thumbnailDao.save(thumbnail);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        Picture uploadPicture = new Picture();
//        uploadPicture.setUrl(uploadFilePath);
//        uploadPicture.setSize(fileSize);
//        uploadPicture.setFileName(file.getOriginalFilename());
        return fileDao.save(uploadFile);
    }
}
