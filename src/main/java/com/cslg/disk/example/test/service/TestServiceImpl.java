package com.cslg.disk.example.test.service;

import com.cslg.disk.example.test.dao.TestDao;
import com.cslg.disk.utils.TencentCOSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class TestServiceImpl implements TestService {
    @Autowired
    private TestDao testDao;

    @Override
    public Map<String, Date> get() {
        TencentCOSUtil tencentCOSUtil = new TencentCOSUtil();
        return tencentCOSUtil.listFolders(TencentCOSUtil.bucketName);
    }
}
