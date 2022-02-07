package com.cslg.disk.example.folders.service;

import com.cslg.disk.example.file.service.FileService;
import com.cslg.disk.example.folders.dao.FolderDao;
import com.cslg.disk.example.folders.entity.Folder;
import com.cslg.disk.utils.TencentCOSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class FolderServiceImpl implements FolderService {
    @Autowired
    private FolderDao folderDao;


    @Override
    public void initFolders() {
        if (folderDao.findAll().size() == 0) {
            TencentCOSUtil tencentCOSUtil = new TencentCOSUtil();
            Map<String, Date> folders = tencentCOSUtil.listFolders(TencentCOSUtil.bucketName);
            folders.keySet().forEach(e -> {
                Folder folder = new Folder();
                folder.setFolderName(e);
                folderDao.save(folder);
            });
        }
    }

    @Override
    public List<Folder> getFolders() {
        initFolders();
        List<Folder> all = folderDao.findAll();
        return all;
    }

    @Override
    public Object addFolder(String name) {
        TencentCOSUtil tencentCOSUtil = new TencentCOSUtil();
        Object o = tencentCOSUtil.addFolder(name);
        return o;
    }

    @Override
    public Object batchDelete(List<String> ids) {
        if (ids.size()==0 || ids==null) {
            return null;
        }
        Integer integer = folderDao.batchDelete(ids);
        return integer>0;
    }
}
