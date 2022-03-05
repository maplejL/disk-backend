package com.cslg.disk.example.file.service;

import com.cslg.disk.example.file.dto.SearchPageDto;
import com.cslg.disk.example.file.entity.MyFile;
import com.cslg.disk.example.user.entity.UserAvater;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface FileService {
    public List<MyFile> getFile();

    public Map<String, Object> getFile(SearchPageDto searchPageDto);

    public MyFile uploadFile(MultipartFile file, int typeCode, String targetFilePath);

    Object downloadFile(String urlStr,String savePath, HttpServletResponse res) throws IOException;

    Object deleteFile(List<Integer> ids);

    Object refactorFile(MyFile file);

    Map<String, Object> getDeleteFiles(SearchPageDto searchPageDto);

    Map<String, Date> getFolders();

    Boolean recoverFiles(List<Integer> ids);

    Boolean completelyDelete(List<Integer> ids);

    void previewFile(String url);

    UserAvater uploadAvater(String userId, MultipartFile file, String targetFilePath);

    Map<String, List<MyFile>> getFileTree(Integer userId);
}
