package com.cslg.disk.example.test.service;

import com.cslg.disk.example.test.dao.TestDao;
import com.cslg.disk.utils.TencentCOSUtil;
import com.qcloud.cos.model.ObjectListing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {
    @Autowired
    private TestDao testDao;

    @Override
    public ObjectListing get() {
        TencentCOSUtil tencentCOSUtil = new TencentCOSUtil();
        return tencentCOSUtil.listObjects("disk-1305749742");
    }
}
