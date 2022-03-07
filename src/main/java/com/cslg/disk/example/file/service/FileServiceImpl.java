package com.cslg.disk.example.file.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cslg.disk.common.exception.BusinessException;
import com.cslg.disk.example.file.dao.ThumbnailDao;
import com.cslg.disk.example.file.dto.SearchPageDto;
import com.cslg.disk.example.file.entity.MyFile;
import com.cslg.disk.example.file.entity.Thumbnail;
import com.cslg.disk.example.file.dao.FileDao;
import com.cslg.disk.example.file.util.FileUtil;
import com.cslg.disk.example.file.util.ImageUtil;
import com.cslg.disk.example.file.util.MutiThreadDownLoad;
import com.cslg.disk.example.user.dao.UserAvaterDao;
import com.cslg.disk.example.user.entity.UserAvater;
import com.cslg.disk.example.user.service.UserService;
import com.cslg.disk.example.user.service.UserServiceImpl;
import com.cslg.disk.utils.TencentCOSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Service
@Transactional
public class FileServiceImpl implements FileService  {
    @Autowired
    private FileDao fileDao;

    @Autowired
    private UserAvaterDao userAvaterDao;

    @Autowired
    private UserService userService;

    @Autowired
    private ThumbnailDao thumbnailDao;

    @Autowired
    private FileUtil fileUtil;

    private static Map<Integer, List<String>> typeAndCode = new HashMap<>();

    private static Map<Integer, String> typeAndName = new HashMap<>();

    static {
        List<String> type1 = new ArrayList<>();
        List<String> type2 = new ArrayList<>();
        List<String> type3 = new ArrayList<>();
        List<String> type4 = new ArrayList<>();
        type1.add("mp4");
        type4.add("jpg");
        type4.add("png");
        type4.add("jpeg");
        type2.add("xlsx");
        type3.add("mp3");
        typeAndCode.put(1, type1);
        typeAndCode.put(2, type2);
        typeAndCode.put(3, type3);
        typeAndCode.put(4, type4);
        typeAndName.put(1, "视频");
        typeAndName.put(2, "文档");
        typeAndName.put(3, "音乐");
        typeAndName.put(4, "图片");
    }

    @Override
    public Map<String, Object> getFile(SearchPageDto searchPageDto, HttpServletRequest request) {
        int pageSize = searchPageDto.getPageSize();
        int typeCode = searchPageDto.getTypeCode();
        int pageNo = searchPageDto.getPageNo();
        String input = searchPageDto.getInput();
        int start = pageNo * pageSize;
        List<MyFile> myFileList = new ArrayList<>();
        Integer userId = UserServiceImpl.getUserId(request);
        if (input == null) {
            myFileList = fileDao.findByPage(start, pageSize, typeCode, userId);
        } else {
            myFileList = fileDao.findByPageWithInput(start, pageSize, typeCode, input, userId);
        }
        if (typeCode == 1) {
            myFileList.forEach(item -> {
                String thumbnailName = thumbnailDao.findByVideoUrl(item.getUrl());
                item.setThumbnailName("http://localhost:9999/" + thumbnailName + ".jpg");
            });
        }
        Map<String, Object> map = new HashMap<>();
        Integer total;
        if (input != null) {
            map.put("total", fileDao.findTotalWithInput(typeCode, input, userId));
        } else {
            map.put("total", fileDao.findTotal(typeCode, userId, 0));
        }
        map.put("files", myFileList);
        return map;
    }

    @Override
    public Map<String, Object> getSharedFile(SearchPageDto searchPageDto, HttpServletRequest request) {
        int pageSize = searchPageDto.getPageSize();
        int typeCode = searchPageDto.getTypeCode();
        int pageNo = searchPageDto.getPageNo();
        String input = searchPageDto.getInput();
        int start = pageNo * pageSize;
        List<MyFile> myFileList = new ArrayList<>();
        Integer userId = UserServiceImpl.getUserId(request);
        if (input == null) {
            myFileList = fileDao.findSharedFilesByPage(start, pageSize, typeCode, userId);
        } else {
            myFileList = fileDao.findSharedFilesByPageWithInput(start, pageSize, typeCode, input, userId);
        }
        if (typeCode == 1) {
            myFileList.forEach(item -> {
                String thumbnailName = thumbnailDao.findByVideoUrl(item.getUrl());
                item.setThumbnailName("http://localhost:9999/" + thumbnailName + ".jpg");
            });
        }
        Map<String, Object> map = new HashMap<>();
        Integer total;
        if (input != null) {
            map.put("total", fileDao.findShredTotalWithInput(typeCode, input, userId));
        } else {
            map.put("total", fileDao.findSharedTotal(typeCode, userId, 0));
        }
        map.put("sharedFiles", myFileList);
        return map;
    }

    @Override
    public List<MyFile> getFile() {
        return fileDao.findAll();
    }

    public MyFile uploadFile(MultipartFile file, int typeCode, String targetFilePath, HttpServletRequest request) {

        if (file == null) {
            return null;
        }
        Integer userId = UserServiceImpl.getUserId(request);
        String[] split1 = file.getOriginalFilename().split("\\.");
        //取最后.后的类型
        String type = split1[split1.length-1];
        List<String> typeNames = typeAndCode.get(typeCode);
        if (!typeNames.contains(type)) {
            throw new BusinessException("上传的文件类型错误!");
        }
        String uploadFilePath = TencentCOSUtil.uploadfile(file);
        long size = file.getSize();
        String fileSize = fileUtil.getSize(size);
        MyFile uploadMyFile = new MyFile();
        uploadMyFile.setFileName(file.getOriginalFilename());
        uploadMyFile.setSize(fileSize);
        uploadMyFile.setUrl(uploadFilePath);
        uploadMyFile.setTypeCode(typeCode);
        uploadMyFile.setTypeName(type);
        uploadMyFile.setUserId(userId);
        String contentType = file.getContentType();
        String[] split = contentType.split("/");
//        uploadMyFile.setTypeName(split[1]);

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

    @Override
    public UserAvater uploadAvater(MultipartFile file, String targetFilePath, HttpServletRequest request) {
        if (file == null) {
            return null;
        }
        Integer userId = UserServiceImpl.getUserId(request);
        String type = (file.getOriginalFilename().split("\\."))[1];
        String uploadFilePath = TencentCOSUtil.uploadfile(file);
        long size = file.getSize();
        String fileSize = fileUtil.getSize(size);

        //已存在则覆盖
        UserAvater avater = userAvaterDao.findByUserId(userId);
        if (avater == null) {
            avater = new UserAvater();
        }

        avater.setFileName(file.getOriginalFilename());
        avater.setUrl(uploadFilePath);
        avater.setTypeName(type);
        avater.setUserId(userId);

        return userAvaterDao.save(avater);
    }

    @Override
    public Map<String, List<MyFile>> getFileTree(Integer userId) {
        List<MyFile> allFiles = fileDao.findByUserId(userId);
        Map<Integer, List<MyFile>> collect = allFiles.stream().collect(Collectors.groupingBy(MyFile::getTypeCode));
        Set<Integer> keys = collect.keySet();
        Map<String, List<MyFile>> result = new HashMap<>();
        for (Integer key : keys) {
            List<MyFile> myFiles = collect.get(key);
            result.put(typeAndName.get(key), myFiles);
        }
        return result;
    }

    //与好友共享文件
    @Override
    public Object shareFile(Integer fileId, List<Integer> userIds, HttpServletRequest request) {
        Integer currentUserId = UserServiceImpl.getUserId(request);
        MyFile one = fileDao.getOne(fileId);
        StringBuilder users = new StringBuilder();
        if (one == null) {
            return false;
        }
        if (one.getShareWithUser() == null) {
            for (Integer userId : userIds) {
                users.append(userId.toString()).append(",");
            }
        } else {
            users.append(one.getShareWithUser()).append(",");
            String[] split = one.getShareWithUser().split(",");
            for (int i = 0; i < split.length; i++) {
                users.append(split[i]).append(",");
            }
        }
        users.replace(users.length()-1, users.length(), "");
        one.setShareWithUser(users.toString());
        return fileDao.save(one);
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
    public Object downloadFile(String id,String savePath, HttpServletResponse res) throws IOException {
        MyFile one = fileDao.getOne(Integer.valueOf(id));
        String urlStr = one.getUrl();
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        String filename = one.getFileName();
        savePath = savePath+"\\"+filename;
        Integer threadSize = 100;
        CountDownLatch latch = new CountDownLatch(threadSize);
        MutiThreadDownLoad mutiThreadDownLoad = new MutiThreadDownLoad(threadSize,urlStr,savePath,latch);
        long startTime = System.currentTimeMillis();
        try {
            res = mutiThreadDownLoad.executeDownLoad(res);
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

    @Override
    public Object deleteFile(List<Integer> ids) {
        fileDao.batchDelete(ids);
        return null;
    }

    @Override
    public Object refactorFile(MyFile file) {
        MyFile save = fileDao.save(file);
        return save;
    }

    public Map<String, Object> getDeleteFiles(SearchPageDto searchPageDto, HttpServletRequest request) {
        int pageSize = searchPageDto.getPageSize();
        int typeCode = searchPageDto.getTypeCode();
        int pageNo = searchPageDto.getPageNo();

        int start = pageNo * pageSize;
        Integer userId = UserServiceImpl.getUserId(request);
        List<MyFile> myFileList = fileDao.findDeleteByPage(start, pageSize, userId);

        myFileList.forEach(item -> {
            if (item.getTypeCode() == 1) {
                String thumbnailName = thumbnailDao.findByVideoUrl(item.getUrl());
                item.setThumbnailName("http://localhost:9999/" + thumbnailName + ".jpg");
            }
        });
        Map<String, Object> map = new HashMap<>();
        map.put("total", fileDao.findTotal(userId, 1));
        map.put("files", myFileList);
        return map;
    }

    @Override
    public Map<String, Date> getFolders() {
        TencentCOSUtil tencentCOSUtil = new TencentCOSUtil();
        Map<String, Date> map = tencentCOSUtil.listFolders(TencentCOSUtil.bucketName);
        return map;
    }

    @Override
    public Boolean recoverFiles(List<Integer> ids) {
        return fileDao.recoverFiles(ids) > 0;
    }

    @Override
    public Boolean completelyDelete(List<Integer> ids) {
        return fileDao.completelyDelete(ids) > 0;
    }

    @Override
    public void previewFile(String url) {
        try {
            fileUtil.browse(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
