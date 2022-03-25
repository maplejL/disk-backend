package com.cslg.disk.example.right.service;

import com.cslg.disk.example.right.dao.RightDao;
import com.cslg.disk.example.right.entity.MyRight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RightServiceImpl implements RightService {
    @Autowired
    private RightDao rightDao;


    @Override
    public List<MyRight> getAllRoles() {
        List<MyRight> all = rightDao.findAll();
        return all;
    }
}
