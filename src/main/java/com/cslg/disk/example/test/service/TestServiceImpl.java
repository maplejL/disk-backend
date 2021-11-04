package com.cslg.disk.example.test.service;

import com.cslg.disk.example.test.dao.TestDao;
import com.cslg.disk.example.test.entity.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestServiceImpl implements TestService {
    @Autowired
    private TestDao testDao;

    @Override
    public List<Test> get() {
        return testDao.findAll();
    }
}
