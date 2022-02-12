package com.cslg.disk.example.chat.service.impl;

import com.cslg.disk.example.chat.entity.TempChat;
import com.cslg.disk.example.chat.dao.TempChatDao;
import com.cslg.disk.example.chat.service.TempChatService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (TempChat)表服务实现类
 *
 * @author makejava
 * @since 2022-02-08 15:54:58
 */
@Service
@Transactional
public class TempChatServiceImpl implements TempChatService {
    @Autowired
    private TempChatDao tempChatDao;

    @Override
    public TempChat queryById(Integer id) {
        return tempChatDao.getOne(id);
    }

    @Override
    public List<TempChat> getall() {
        return tempChatDao.findAll();

    }

    @Override
    public List<TempChat> getallById(Integer id) {
        return tempChatDao.findTempChatsById(id);
    }

    @Override
    public Page<TempChat> queryAllByLimit(int offset, int limit) {
        return tempChatDao.findAll(PageRequest.of((offset - 1)
                * limit, limit));
    }

    @Override
    public TempChat insert(TempChat tempChat) {

        return tempChatDao.save(tempChat);
    }


    @Override
    public TempChat update(TempChat tempChat) {

        return tempChatDao.save(tempChat);
    }


    @Override
    public boolean deleteById(Integer id) {

        try {
            tempChatDao.deleteById(id);
        } catch (Exception ex) {
            return false;
        }
        return true;

    }
}


