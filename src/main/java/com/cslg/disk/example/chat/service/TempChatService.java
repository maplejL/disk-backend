package com.cslg.disk.example.chat.service;

import java.util.List;

import com.cslg.disk.example.chat.entity.TempChat;
import org.springframework.data.domain.Page;

/**
 * (TempChat)表服务接口
 *
 * @author zry
 * @since 2022-02-08 15:54:58
 */
public interface TempChatService {
    com.cslg.disk.example.chat.entity.TempChat queryById(Integer id);

    Page<com.cslg.disk.example.chat.entity.TempChat> queryAllByLimit(int offset, int limit);

    com.cslg.disk.example.chat.entity.TempChat insert(com.cslg.disk.example.chat.entity.TempChat tempChat);

    com.cslg.disk.example.chat.entity.TempChat update(com.cslg.disk.example.chat.entity.TempChat tempChat);

    boolean deleteById(Integer id);

    List<com.cslg.disk.example.chat.entity.TempChat> getall();

    List<TempChat> getallById(Integer id);

}


