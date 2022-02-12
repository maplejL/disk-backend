package com.cslg.disk.example.chat.service;

import com.cslg.disk.example.chat.dto.ConversationDto;
import com.cslg.disk.example.chat.entity.Conversation;

import java.util.List;

import org.springframework.data.domain.Page;

/**
 * (Conversation)表服务接口
 *
 * @author zry
 * @since 2022-02-08 10:46:09
 */
public interface ConversationService {
    Conversation queryById(Integer id);

    Page<Conversation> queryAllByLimit(int offset, int limit);

    Conversation insert(ConversationDto conversationDto);

    Conversation update(Conversation conversation);

    boolean deleteById(Integer id);

    List<Conversation> getall();

    //获取用户所在的所有会话
    List<Conversation> getallById(Integer id);
}


