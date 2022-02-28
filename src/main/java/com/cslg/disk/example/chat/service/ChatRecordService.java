package com.cslg.disk.example.chat.service;

import com.cslg.disk.example.chat.dto.ChatDto;
import com.cslg.disk.example.chat.entity.ChatRecord;

import java.util.List;

import org.springframework.data.domain.Page;

/**
 * (ChatRecord)表服务接口
 *
 * @author zry
 * @since 2022-02-08 11:14:45
 */
public interface ChatRecordService {
    ChatRecord queryById(Integer id);

    Page<ChatRecord> queryAllByLimit(int offset, int limit);

    ChatRecord insert(ChatRecord chatRecord);

    ChatRecord update(ChatRecord chatRecord);

    boolean deleteById(Integer id);

    List<ChatRecord> getall();

    ChatRecord sendMessage(ChatDto chatDto);

    Object deleteTempChat(List<Integer> ids);

    List<ChatRecord> getChatRecordByConversationId(String id);
}


