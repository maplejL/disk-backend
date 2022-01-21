package com.cslg.disk.utils;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.*;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: zh
 * @Date: 2020/6/5 16:22
 */
@Data
public class TencentCOSUtil {
    // 存储桶名称
    public static final String bucketName = "disk-1305749742";
    //secretId 秘钥id
    private static final String SecretId = "AKIDtfEahaFfqnVzGI6g3C6mESSEm6D7oGUU";
    //SecretKey 秘钥
    private static final String SecretKey = "nMhngKjvsa5MPDoCMHyRfeQyHMDpDMHw";
    // 腾讯云 自定义文件夹名称
    private static final String prefix = "mall/";
    // 访问域名
    public static final String URL = "https://disk-1305749742.cos-website.ap-shanghai.myqcloud.com/";
    // 创建COS 凭证
    private static COSCredentials credentials = new BasicCOSCredentials(SecretId,SecretKey);
    // 配置 COS 区域 就购买时选择的区域 我这里是 广州（guangzhou）
    private static ClientConfig clientConfig = new ClientConfig(new Region("ap-shanghai"));

    public static COSClient cosClient;
    static {
        //创建cos客户端连接
        cosClient = new COSClient(credentials,clientConfig);
    }

    // Prints progress while waiting for the transfer to finish.
    private static void showTransferProgress(Transfer transfer) {
        System.out.println(transfer.getDescription());
        do {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return;
            }
            TransferProgress progress = transfer.getProgress();
            long so_far = progress.getBytesTransferred();
            long total = progress.getTotalBytesToTransfer();
            double pct = progress.getPercentTransferred();
            System.out.printf("[%d / %d]\n", so_far, total);
        } while (transfer.isDone() == false);
        System.out.println(transfer.getState());
    }

    private static File transferToFile(MultipartFile multipartFile) {
//        选择用缓冲区来实现这个转换即使用java 创建的临时文件 使用 MultipartFile.transferto()方法 。
        File file = null;
        try {
            String originalFilename = multipartFile.getOriginalFilename();
            String[] filename = originalFilename.split(".");
            file=File.createTempFile(filename[0], filename[1]);
            multipartFile.transferTo(file);
            file.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
    // 上传文件, 根据文件大小自动选择简单上传或者分块上传。
    public static String uploadFile(MultipartFile file) {
        ExecutorService threadPool = Executors.newFixedThreadPool(100);
        // 传入一个threadpool, 若不传入线程池, 默认TransferManager中会生成一个单线程的线程池。
        TransferManager transferManager = new TransferManager(cosClient, threadPool);
        String key = "test.mp4";
        File localFile = new File("C:\\Users\\user\\Videos\\Captures\\test.mp4");
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
        try {
            // 返回一个异步结果Upload, 可同步的调用waitForUploadResult等待upload结束, 成功返回UploadResult, 失败抛出异常.
            long startTime = System.currentTimeMillis();
            Upload upload = transferManager.upload(putObjectRequest);
            showTransferProgress(upload);
            UploadResult uploadResult = upload.waitForUploadResult();
            key = uploadResult.getKey();
            long endTime = System.currentTimeMillis();
            System.out.println("used time: " + (endTime - startTime) / 1000);
            System.out.println(uploadResult.getETag());
        } catch (CosServiceException e) {
            e.printStackTrace();
        } catch (CosClientException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        transferManager.shutdownNow();
        cosClient.shutdown();
        return key;
    }

    // 上传文件（上传过程中暂停, 并继续上传)
    public static void pauseUploadFileAndResume() {
        ExecutorService threadPool = Executors.newFixedThreadPool(4);
        // 传入一个threadpool, 若不传入线程池, 默认TransferManager中会生成一个单线程的线程池。
        TransferManager transferManager = new TransferManager(cosClient, threadPool);

        String key = "aaa/bbb.txt";
        File localFile = new File("src/test/resources/len30M.txt");
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
        try {
            // 返回一个异步结果Upload, 可同步的调用waitForUploadResult等待upload结束, 成功返回UploadResult, 失败抛出异常.
            Upload upload = transferManager.upload(putObjectRequest);
            Thread.sleep(10000);
            PersistableUpload persistableUpload = upload.pause();
            upload = transferManager.resumeUpload(persistableUpload);
            showTransferProgress(upload);
            UploadResult uploadResult = upload.waitForUploadResult();
            System.out.println(uploadResult.getETag());
        } catch (CosServiceException e) {
            e.printStackTrace();
        } catch (CosClientException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        transferManager.shutdownNow();
        cosClient.shutdown();
    }

    public static String uploadfile(MultipartFile file){

        String fileName = file.getOriginalFilename();
        try {
            String substring = fileName.substring(fileName.lastIndexOf("."));
            File localFile = File.createTempFile(String.valueOf(System.currentTimeMillis()),substring);
            file.transferTo(localFile);
            Random random = new Random();
            fileName =prefix+random.nextInt(10000)+System.currentTimeMillis()+substring;
            // 将 文件上传至 COS
            PutObjectRequest objectRequest = new PutObjectRequest(bucketName,fileName,localFile);
            cosClient.putObject(objectRequest);
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName,fileName);
            COSObject object = cosClient.getObject(getObjectRequest);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            cosClient.shutdown();
        }
        return URL+fileName;
    }

    /**
     * 查看桶文件
     * @param bucketName
     * @return
     * @throws CosClientException
     * @throws CosServiceException
     */
    public static ObjectListing listObjects(String bucketName,String folderName) throws CosClientException, CosServiceException {
        COSClient cosClient = new COSClient(credentials,clientConfig);

        // 获取 bucket 下成员（设置 delimiter）
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
        listObjectsRequest.setBucketName(bucketName);
        // 设置 list 的 prefix, 表示 list 出来的文件 key 都是以这个 prefix 开始
        listObjectsRequest.setPrefix("");
        // 设置 delimiter 为/, 即获取的是直接成员，不包含目录下的递归子成员
        listObjectsRequest.setDelimiter("");
        // 设置 marker, (marker 由上一次 list 获取到, 或者第一次 list marker 为空)
        listObjectsRequest.setMarker("");
        // 设置最多 list 100 个成员,（如果不设置, 默认为 1000 个，最大允许一次 list 1000 个 key）
//        listObjectsRequest.setMaxKeys(100);

        ObjectListing objectListing = cosClient.listObjects(listObjectsRequest);
        // 获取下次 list 的 marker
        String nextMarker = objectListing.getNextMarker();
        // 判断是否已经 list 完, 如果 list 结束, 则 isTruncated 为 false, 否则为 true
        boolean isTruncated = objectListing.isTruncated();
        List<COSObjectSummary> objectSummaries = objectListing.getObjectSummaries();
        for (COSObjectSummary cosObjectSummary : objectSummaries) {
            // get file path
            String key = cosObjectSummary.getKey();
            // get file length
            long fileSize = cosObjectSummary.getSize();
            // get file etag
            String eTag = cosObjectSummary.getETag();
            // get last modify time
            Date lastModified = cosObjectSummary.getLastModified();
            // get file save type
            String StorageClassStr = cosObjectSummary.getStorageClass();
        }
        return objectListing;
    }

    /**
     * 查看全部的文件夹
     * @param bucketName
     * @return
     * @throws CosClientException
     * @throws CosServiceException
     */
    public static Map<String, Date> listFolders(String bucketName) throws CosClientException, CosServiceException {
        COSClient cosClient = new COSClient(credentials,clientConfig);

        // 获取 bucket 下成员（设置 delimiter）
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
        listObjectsRequest.setBucketName(bucketName);
        // 设置 list 的 prefix, 表示 list 出来的文件 key 都是以这个 prefix 开始
        listObjectsRequest.setPrefix("");
        // 设置 delimiter 为/, 即获取的是直接成员，不包含目录下的递归子成员
        listObjectsRequest.setDelimiter("");
        // 设置 marker, (marker 由上一次 list 获取到, 或者第一次 list marker 为空)
        listObjectsRequest.setMarker("");
        // 设置最多 list 100 个成员,（如果不设置, 默认为 1000 个，最大允许一次 list 1000 个 key）
        listObjectsRequest.setMaxKeys(100000);

        ObjectListing objectListing = cosClient.listObjects(listObjectsRequest);
        Map<String, Date> folders = new HashMap<>();
        List<COSObjectSummary> objectSummaries = objectListing.getObjectSummaries();
        for (COSObjectSummary objectSummary : objectSummaries) {
            Date newDate = objectSummary.getLastModified();
            String[] split = objectSummary.getKey().split("/");
            if (!folders.containsKey(split[0])) {
                folders.put(split[0], newDate);
            } else {
                if (newDate.after(folders.get(split[0]))) {
                    folders.put(split[0], newDate);
                }
            }
        }
        folders.remove("cos-access-log");
        return folders;
    }

}
