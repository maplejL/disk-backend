package com.cslg.disk.example.chat.service;

import com.cslg.disk.example.chat.entity.TempChat;

import java.util.List;

import org.springframework.data.domain.Page;

/**
 * (TempChat)表服务接口
 *
 * @author zry
 * @since 2022-02-08 15:54:58
 */
public interface TempChatService {
    TempChat queryById(Integer id);

    Page<TempChat> queryAllByLimit(int offset, int limit);

    TempChat insert(TempChat tempChat);

    TempChat update(TempChat tempChat);

    boolean deleteById(Integer id);

    List<TempChat> getall();

    List<TempChat> getallById(Integer id);

}


