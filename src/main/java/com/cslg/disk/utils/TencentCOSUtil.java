package com.cslg.disk.utils;

import ch.qos.logback.core.util.FileUtil;
import com.mysql.cj.util.StringUtils;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.*;
import com.qcloud.cos.utils.IOUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Data
@Slf4j
public class TencentCOSUtil {
    // 存储桶名称
    public static final String bucketName = "disk-1305749742";
    //secretId 秘钥id
    private static final String SecretId = "AKIDtfEahaFfqnVzGI6g3C6mESSEm6D7oGUU";
    //SecretKey 秘钥
    private static final String SecretKey = "nMhngKjvsa5MPDoCMHyRfeQyHMDpDMHw";

    // 腾讯云 自定义文件夹名称
    private static String prefix = "mall/";
    // 访问域名
    public static final String URL = "https://disk-1305749742.cos-website.ap-shanghai.myqcloud.com/";
    // 创建COS 凭证
    private static COSCredentials credentials = new BasicCOSCredentials(SecretId, SecretKey);
    // 配置 COS 区域 就购买时选择的区域 我这里是 广州（guangzhou）
    private static ClientConfig clientConfig = new ClientConfig(new Region("ap-shanghai"));

    public static COSClient cosClient;

    static {
        //创建cos客户端连接
        cosClient = new COSClient(credentials, clientConfig);
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

    //存储桶中彻底删除文件
    public static void delete(String key) {
        new Thread(new Runnable() {
            public void run() {
                // 指定要删除的 bucket 和路径
                try {
                    cosClient.deleteObject(bucketName, key);
                    System.out.println("删除成功");
                } catch (Throwable tb) {
                    System.out.println("删除文件失败");
                    tb.printStackTrace();
                }
            }
        }).start();
    }

    public static String uploadfile(MultipartFile file) {

        String fileName = file.getOriginalFilename();
        try {
            long startTime=System.currentTimeMillis();
            String substring = fileName.substring(fileName.lastIndexOf("."));
            File localFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), substring);
            file.transferTo(localFile);
            Random random = new Random();
            fileName = prefix + random.nextInt(10000) + System.currentTimeMillis() + substring;
            // 将 文件上传至 COS
            PutObjectRequest objectRequest = new PutObjectRequest(bucketName, fileName, localFile);
            cosClient.putObject(objectRequest);
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, fileName);
            COSObject object = cosClient.getObject(getObjectRequest);
            long endTime=System.currentTimeMillis();
            System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cosClient.shutdown();
        }
        return URL + fileName;
    }

    public static String uploadfile(File file) {
        String fileName = file.getName();
        try {
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
            // 将 文件上传至 COS
            PutObjectRequest objectRequest = new PutObjectRequest(bucketName, fileName, file);
            cosClient.putObject(objectRequest);
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, fileName);
            COSObject object = cosClient.getObject(getObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cosClient.shutdown();
        }
        return URL + fileName;
    }

    /**
     * 查看桶文件
     *
     * @param bucketName
     * @return
     * @throws CosClientException
     * @throws CosServiceException
     */
    public static ObjectListing listObjects(String bucketName, String folderName) throws CosClientException, CosServiceException {
        COSClient cosClient = new COSClient(credentials, clientConfig);

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
     *
     * @param bucketName
     * @return
     * @throws CosClientException
     * @throws CosServiceException
     */
    public static Map<String, Date> listFolders(String bucketName) throws CosClientException, CosServiceException {
        COSClient cosClient = new COSClient(credentials, clientConfig);

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

    public Object addFolder(String folderName) {
        File file = new File("src\\main\\resources\\static\\default.txt");
        String temp = prefix;
        prefix = folderName + "/";
        uploadfile(file);
        prefix = temp;
        return folderName;
    }

}
