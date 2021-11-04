package com.cslg.disk.utils;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Random;

/**
 * @Author: zh
 * @Date: 2020/6/5 16:22
 */

public class TencentCOSUtil {
    // 存储桶名称
    private static final String bucketName = "disk-1305749742";
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

    public static String uploadfile(MultipartFile file){
        // 创建 COS 客户端连接
        COSClient cosClient = new COSClient(credentials,clientConfig);
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
}
