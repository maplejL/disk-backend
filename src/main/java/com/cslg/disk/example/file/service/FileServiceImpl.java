package com.cslg.disk.example.file.service;

import com.cslg.disk.common.exception.BusinessException;
import com.cslg.disk.example.file.dao.SharedFileDao;
import com.cslg.disk.example.file.dao.ThumbnailDao;
import com.cslg.disk.example.file.dto.SearchPageDto;
import com.cslg.disk.example.file.entity.MyFile;
import com.cslg.disk.example.file.entity.ShareRecord;
import com.cslg.disk.example.file.entity.Thumbnail;
import com.cslg.disk.example.file.dao.FileDao;
import com.cslg.disk.example.file.util.FileUtil;
import com.cslg.disk.example.file.util.ImageUtil;
import com.cslg.disk.example.file.util.MutiThreadDownLoad;
import com.cslg.disk.example.socket.WebSocket;
import com.cslg.disk.example.user.dao.UserAvaterDao;
import com.cslg.disk.example.user.entity.MyUser;
import com.cslg.disk.example.user.entity.UserAvater;
import com.cslg.disk.example.user.service.UserService;
import com.cslg.disk.example.user.service.UserServiceImpl;
import com.cslg.disk.utils.TencentCOSUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private WebSocket socket;

    @Autowired
    private SharedFileDao sharedFileDao;

    @Autowired
    private UserService userService;

    @Autowired
    private ThumbnailDao thumbnailDao;

    @Autowired
    private FileUtil fileUtil;

    private static Map<Integer, List<String>> typeAndCode = new HashMap<>();

    private static Map<Integer, String> typeAndName = new HashMap<>();

    private static List<String> previewTypes = new ArrayList<>();

    static {
        List<String> type1 = new ArrayList<>();
        List<String> type2 = new ArrayList<>();
        List<String> type3 = new ArrayList<>();
        List<String> type4 = new ArrayList<>();
        previewTypes.add("xlsx");
        previewTypes.add("xls");
        previewTypes.add("doc");
        previewTypes.add("docx");
        type1.add("mp4");
        type4.add("jpg");
        type4.add("png");
        type4.add("jpeg");
        type2.add("xlsx");
        type2.add("xls");
        type2.add("doc");
        type2.add("txt");
        type2.add("docx");
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
        Integer currentUserId = UserServiceImpl.getUserId(request);
        int start = pageNo * pageSize;
        List<MyFile> myFileList = new ArrayList<>();
        Integer userId = UserServiceImpl.getUserId(request);
        //获取所有的文件信息
        if (input == null) {
            myFileList = fileDao.findSharedFilesByPage(start, pageSize, typeCode, userId);
        } else {
            myFileList = fileDao.findSharedFilesByPageWithInput(start, pageSize, typeCode, input, userId);
        }
        //获取分享记录
        for (MyFile myFile : myFileList) {
            Integer id = myFile.getId();
            List<ShareRecord> list = sharedFileDao.findByUserIdAndFileId(userId, id);
            List<MyUser> users = new ArrayList<>();
            //为该文件设置分享者（文件拥有者）,被分享者
            for (ShareRecord shareRecord : list) {
                if (shareRecord.getUserId() != currentUserId) {
                    //当前用户为被分享者
                    users.add(userService.getUserById(String.valueOf(myFile.getUserId())));
                } else {
                    //当前用户为分享者
                    String[] split = shareRecord.getSharedIds().split(",");
                    for (String s : split) {
                        users.add(userService.getUserById(s));
                    }
                }
            }
            myFile.setSharedUsers(users);
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
            map.put("total", fileDao.findSharedTotalWithInput(typeCode, input, userId));
        } else {
            map.put("total", fileDao.findSharedTotal(typeCode, userId, 0));
        }
        map.put("sharedFiles", myFileList);
        return map;
    }

    @Override
    public List<MyFile> getFile(HttpServletRequest request) {
        return fileDao.findAll().stream()
                .filter(e -> e.getUserId() == UserServiceImpl.getUserId(request) && e.getIsDelete() == 0)
                .collect(Collectors.toList());
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
        ShareRecord record = sharedFileDao.findByFileId(one.getId());
//        if (record != null && record.getUserId() == currentUserId) {
//            //当前文件已被分享过
//            String[] split = record.getSharedIds().split(",");
//            List<MyUser> list = new ArrayList<>();
//            for (String s : split) {
//                if (userIds.contains(Integer.valueOf(s))) {
//                    //该文件已被分享给该用户
//                    list.add(userService.getUserById(s));
//                }
//            }
//            if (list.size() > 0) {
//                StringBuilder builder = new StringBuilder();
//                for (MyUser myUser : list) {
//                    builder.append(myUser.getUsername()).append(" ");
//                }
//                throw new BusinessException("以下用户已被共享此文件: ["+builder+"]");
//            }
//            users.append(record.getSharedIds());
//        }
        if (record != null && record.getUserId() == currentUserId) {
            String sharedIds = record.getSharedIds();
            String[] split = sharedIds.split(",");
            for (int i = 0; i < split.length; i++) {
                if (!userIds.contains(split[i])) {
                    userIds.add(Integer.valueOf(split[i]));
                }
            }
            //当前文件已被分享过,删除原记录重新生成
            sharedFileDao.deleteById(record.getId());
        }
        for (Integer userId : userIds) {
            users.append(userId.toString()).append(",");
        }
        users.replace(users.length()-1, users.length(), "");
        ShareRecord shareRecord = new ShareRecord();
        shareRecord.setFileId(fileId);
        shareRecord.setUserId(currentUserId);
        shareRecord.setSharedIds(users.toString());
        ShareRecord save = sharedFileDao.save(shareRecord);
        if (save != null) {
            for (Integer userId : userIds) {
                socket.sendOneMessage(userId.toString(), "您有新的分享文件");
            }
        }
        return true;
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
    public Object downloadFile(String id,String savePath, HttpServletResponse response, HttpServletRequest request) throws IOException {
        MyFile one = fileDao.getOne(Integer.valueOf(id));
        String urlStr = one.getUrl();
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        String filename = one.getFileName();
        savePath = savePath+"\\"+filename;
        Double fileSize = Double.valueOf(one.getSize().substring(0, one.getSize().length() - 2));
        Double threadSize = fileSize/5;
        CountDownLatch latch = new CountDownLatch(threadSize.intValue());
        MutiThreadDownLoad mutiThreadDownLoad = new MutiThreadDownLoad(threadSize.intValue(),urlStr,savePath,latch);
        long startTime = System.currentTimeMillis();
        try {
            response = mutiThreadDownLoad.executeDownLoad(response, request);
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
        for (Integer id : ids) {
            ShareRecord share = sharedFileDao.findByFileId(id);
            if (share != null) {
                //存在共享记录
                throw new BusinessException("该文件已被共享,删除需要先取消共享!");
            }
        }
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
        List<MyFile> files = fileDao.findByIds(ids);
        List<String> urls = files.stream().map(MyFile::getUrl).collect(Collectors.toList());
        for (String url : urls) {
            String key = url.substring(url.indexOf("mall/"), url.length());
            TencentCOSUtil.delete(key);
        }
        return fileDao.completelyDelete(ids) > 0;
    }

    @Override
    public void previewFile(String url) {
        int i = url.lastIndexOf(".");
        String prefix = url.substring(i+1, url.length());
        if (!previewTypes.contains(prefix)) {
            throw new BusinessException("该文件类型不支持预览!");
        }
        try {
            fileUtil.browse(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getExtractionCode() {
        String[] beforeShuffle = new String[] { "2", "3", "4", "5", "6", "7",
                "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
                "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z" };
        List list = Arrays.asList(beforeShuffle);
        Collections.shuffle(list);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
        }
        String afterShuffle = sb.toString();
        String result = afterShuffle.substring(5, 9);
        return result;
    }

    /**
     *创建文件、文件夹{调用makeDir（） 递归方法}
     *file.exists() 返回  true  文件、文件夹存在
     *file.exists() 返回 false 文件、文件夹不存在
     *@ throws IOException
     */
    public static boolean createFile(File file) throws IOException {
        if(!file.exists()){
            makeDir(file.getParentFile());
        }
        return file.createNewFile();
    }
    /**
     * 递归方法
     * makeDir（） 采用递归方法对文件、文件夹进行遍历创建新文件、新文件夹
     * @param dir
     */
    public static void makeDir(File dir) {
        if(!dir.getParentFile().exists()) {
            makeDir(dir.getParentFile());
        }
        dir.mkdir();
    }

    /**
     * 生成不带白边的二维码
     *
     * @throws Exception 异常
     */
    @Override
    public Object generatorQrCode(String fileId,Integer validPeriod, HttpServletRequest request) {
        MyFile file = fileDao.getOne(Integer.valueOf(fileId));
        int typeCode = file.getTypeCode();
        String QRCodePath = "D:\\disk\\qrcode.png";
        File file1 = new File(QRCodePath);
        try {
            //若文件夹不存在则创建文件夹
            createFile(file1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //定义二维码的内容参数
        Map<EncodeHintType,Object> hints=new HashMap<EncodeHintType, Object>();
        //设置边框距
        hints.put(EncodeHintType.MARGIN,2);
        //设置字符编码格式
        hints.put(EncodeHintType.CHARACTER_SET,"UTF-8");
        //设置容错等级 等级越高存入内容越少
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M); //以上就是设置内容参数 如果不用中文可以不用设置
        ShareRecord shareRecord = new ShareRecord();
        shareRecord.setFileId(Integer.valueOf(fileId));
        shareRecord.setUserId(UserServiceImpl.getUserId(request));
        shareRecord.setValidPeriod(validPeriod);
        shareRecord.setExtractionCode(getExtractionCode());
        ShareRecord save = sharedFileDao.save(shareRecord);
        try {
            //设置二维码的内容
//            String contents = file.getUrl();
            String contents = "http://localhost:8080/#/share?typeCode="+typeCode+"&id="+save.getId();
            //第一个参数为二维码内容，第二个是二维码格式，第三四是宽高，第四是前面写的内容参数 如果无写null
            BitMatrix bm = new MultiFormatWriter().encode(contents, BarcodeFormat.QR_CODE, 200, 200, hints);
            //第一个参数是BitMatrix，第二个是生成图片的格式，第三个是生成文件的地址
            MatrixToImageWriter.writeToStream(bm, "png", new FileOutputStream(QRCodePath));
            InputStream in = null;
            byte[] data = null;
            try {
                in = new FileInputStream(QRCodePath);
                data = new byte[in.available()];
                in.read(data);
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //进行Base64编码
            BASE64Encoder encoder = new BASE64Encoder();
            String encode = encoder.encode(data);
            Map<String, String> map = new HashMap<>();
            map.put("encode", encode);
            map.put("url", contents);
            map.put("extractionCode", save.getExtractionCode());
            map.put("path", QRCodePath);
            return map;
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public ShareRecord showSharedFile(Integer id, String extractionCode) {
        if (id == null) {
            return null;
        }
        ShareRecord record;
        try{
            record = sharedFileDao.findByIdAndExtractionCode(id, extractionCode);
        }catch (Exception e){
            throw new BusinessException("当前文件已超过有效期, 即将跳转至首页");
        }
        Integer validPeriod = record.getValidPeriod();
        if (validPeriod != 0) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            Date currentTime = null;//现在系统当前时间
            try {
                currentTime = dateFormat.parse(dateFormat.format(new Date()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long diff = System.currentTimeMillis() - record.getCreatedDate().getTime();
            double days = diff * 1.0 / (1000 * 60 * 60 * 24) ;
            if (days > validPeriod) {
                //已超过有效期
                sharedFileDao.mydeleteById(id);
                throw new BusinessException("当前文件已超过有效期, 即将跳转至首页");
            }
            //获取剩余时间
            record.setRemainTime(validPeriod-(int) days);
        } else {
            record.setRemainTime(999999);
        }
        MyFile file = fileDao.findById(record.getFileId()).get();
        MyUser user = userService.getUserById(record.getUserId().toString());
        UserAvater avater = userAvaterDao.findByUserId(user.getId());
        if (avater == null) {
            if (user.getSex() == null) {
                //未选择性别，默认男头像
                avater = userAvaterDao.findByUserId(0);
            }else {
                avater = userAvaterDao.findByUserId(user.getSex() == 0 ? 0 : -1);
            }
        }
        record.setAvater(avater);
        record.setFile(file);
        record.setUser(user);
        return record;

    }

    @Override
    public Object cancelShare(Integer id, HttpServletRequest request) {
        ShareRecord shareRecord = sharedFileDao.findByFileId(id);
        if (shareRecord == null) {
            return false;
        }
        return sharedFileDao.mydeleteById(shareRecord.getId()) > 0;
    }

    @Override
    public ShareRecord getByFileId(Integer fileId) {
        ShareRecord byFileId = sharedFileDao.findByFileId(fileId);
        return byFileId;
    }

    @Override
    public Map<String, Object> getLinkRecord(Integer id, Integer pageNo, Integer pageSize) {
        pageNo = 0;
        pageSize = 10000;
        int start = pageNo * pageSize;
//        limit :start,:size
        //通过用户id获取链接记录
        List<ShareRecord> records = sharedFileDao.findByUserId(id, start, 10000);
        List<Integer> fileIds = records.stream().map(e -> e.getFileId()).collect(Collectors.toList());
        List<MyFile> files = fileDao.findByIds(fileIds);
        for (ShareRecord record : records) {
            Map<Integer, MyFile> collect = files.stream().collect(Collectors.toMap(MyFile::getId, MyFile -> MyFile));
            String content = "http://localhost:8080/#/share?typeCode="+collect.get(record.getFileId()).getTypeCode()+"&id="+record.getId();
            record.setLinkContent(content);
            record.setFile(collect.get(record.getFileId()));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("records", records);
        map.put("total", sharedFileDao.findAll().stream().filter(e -> e.getIsDelete() == 0 && e.getUserId() == id).count());
        return map;
    }

    @Override
    public Object deleteRecord(Integer id, HttpServletRequest request) {
        sharedFileDao.deleteById(id);
        return true;
    }
}
