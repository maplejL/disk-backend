package com.cslg.disk.example.file.service;

import com.cslg.disk.example.file.dao.ThumbnailDao;
import com.cslg.disk.example.file.dto.SearchPageDto;
import com.cslg.disk.example.file.entity.MyFile;
import com.cslg.disk.example.file.entity.Thumbnail;
import com.cslg.disk.example.file.dao.FileDao;
import com.cslg.disk.example.file.util.FileUtil;
import com.cslg.disk.example.file.util.ImageUtil;
import com.cslg.disk.example.file.util.MutiThreadDownLoad;
import com.cslg.disk.utils.TencentCOSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@Service
public class FileServiceImpl implements FileService  {
    @Autowired
    private FileDao fileDao;

    @Autowired
    private ThumbnailDao thumbnailDao;

    @Autowired
    private FileUtil fileUtil;

    @Override
    public Map<String, Object> getFile(SearchPageDto searchPageDto) {
        int pageSize = searchPageDto.getPageSize();
        int typeCode = searchPageDto.getTypeCode();
        int pageNo = searchPageDto.getPageNo();

        int start = pageNo * pageSize;
        List<MyFile> myFileList = fileDao.findByPage(start, pageSize, typeCode);
        if (typeCode == 1) {
            myFileList.forEach(item -> {
                String thumbnailName = thumbnailDao.findByVideoUrl(item.getUrl());
                item.setThumbnailName("http://localhost:9999/" + thumbnailName + ".jpg");
            });
        }
        Map<String, Object> map = new HashMap<>();
        map.put("total", fileDao.findAll().stream().filter(e -> e.getTypeCode() == typeCode).count());
        map.put("files", myFileList);
        return map;
    }

    @Override
    public List<MyFile> getFile() {
        return fileDao.findAll();
    }

    public MyFile uploadFile(MultipartFile file, int typeCode, String targetFilePath) {

        if (file == null) {
            return null;
        }
        String uploadFilePath = TencentCOSUtil.uploadfile(file);
        long size = file.getSize();
        String fileSize = fileUtil.getSize(size);
        MyFile uploadMyFile = new MyFile();
        uploadMyFile.setFileName(file.getOriginalFilename());
        uploadMyFile.setSize(fileSize);
        uploadMyFile.setUrl(uploadFilePath);
        uploadMyFile.setTypeCode(typeCode);
        String contentType = file.getContentType();
        String[] split = contentType.split("/");
        uploadMyFile.setTypeName(split[1]);

        if (typeCode == 1) {
            ImageUtil imageUtil = new ImageUtil();
            try {
                String thumbnailName = UUID.randomUUID().toString();
                String path = imageUtil.randomGrabberFFmpegImage(uploadFilePath, targetFilePath, thumbnailName);
                Thumbnail thumbnail = new Thumbnail();
                thumbnail.setName(thumbnailName);
                thumbnail.setUrl(path);
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
        return fileDao.save(uploadMyFile);
    }

    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    @Override
    public Object downloadFile(String urlStr,String savePath) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        String filename = (new File(urlStr)).getName();
        savePath = savePath+"\\"+filename;
        Integer threadSize = 100;
        CountDownLatch latch = new CountDownLatch(threadSize);
        MutiThreadDownLoad mutiThreadDownLoad = new MutiThreadDownLoad(threadSize,urlStr,savePath,latch);
        long startTime = System.currentTimeMillis();
        try {
            mutiThreadDownLoad.executeDownLoad();
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("全部下载结束,共耗时" + (endTime - startTime) / 1000 + "s");
        Map<String, Object> map = new HashMap<>();
        map.put("time", (endTime - startTime) / 1000);
        map.put("path", savePath);
        return map;
    }
}
