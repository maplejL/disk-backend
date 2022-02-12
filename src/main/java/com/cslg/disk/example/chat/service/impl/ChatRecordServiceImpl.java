package com.cslg.disk.example.chat.service.impl;

import com.cslg.disk.example.chat.dao.TempChatDao;
import com.cslg.disk.example.chat.dto.ChatDto;
import com.cslg.disk.example.chat.entity.ChatRecord;
import com.cslg.disk.example.chat.dao.ChatRecordDao;
import com.cslg.disk.example.chat.entity.Conversation;
import com.cslg.disk.example.chat.entity.TempChat;
import com.cslg.disk.example.chat.service.ChatRecordService;
import com.cslg.disk.example.chat.service.ConversationService;
import com.cslg.disk.example.socket.WebSocket;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;

/**
 * (ChatRecord)表服务实现类
 *
 * @author makejava
 * @since 2022-02-08 11:14:45
 */

/**
 * {
 *     "userId": 2,
 *     "conversationId": 2,
 *     "content": "testttt"
 * }
 */
@Service("chatRecordService")
@Transactional
public class ChatRecordServiceImpl implements ChatRecordService {
    @Autowired
    private ChatRecordDao chatRecordDao;

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private WebSocket socket;

    @Autowired
    private TempChatDao tempChatDao;

    @Override
    public ChatRecord queryById(Integer id) {
        return chatRecordDao.getOne(id);
    }

    @Override
    public List<ChatRecord> getall() {
        return chatRecordDao.findAll();

    }

    @Override
    public Object sendMessage(ChatDto chatDto) {
        Integer conversationId = chatDto.getConversationId();
        Conversation conversation = conversationService.queryById(conversationId);
        List<String> ids = Arrays.stream(conversation.getUserIds().split(",")).filter(e -> !e.equals(chatDto.getUserId()))
                .collect(Collectors.toList());
        StringBuilder offLineUserIds = new StringBuilder();
        for (String id : ids) {
            //判断是否在线，不在线保存到待推送消息
            Integer isOffLine = socket.sendOneMessage(id, chatDto.getContent());
            if (isOffLine == 0) {
                offLineUserIds.append(id).append(",");
            }
        }
        offLineUserIds.delete(offLineUserIds.length()-1, offLineUserIds.length());
        if (offLineUserIds.length() > 0) {
            TempChat tempChat = new TempChat();
            tempChat.setContent(chatDto.getContent());
            tempChat.setOffLineUserIds(offLineUserIds.toString());
            tempChat.setConversationId(conversationId);
            tempChatDao.save(tempChat);
        }
        ChatRecord chatRecord = new ChatRecord();
        chatRecord.setSendUser(chatDto.getUserId());
        chatRecord.setContent(chatDto.getContent());
        chatRecord.setConversationId(conversationId);
        insert(chatRecord);
        return true;
    }

    @Override
    public Object deleteTempChat(List<Integer> ids) {
        return tempChatDao.delete(ids)>0;
    }

    @Override
    public Page<ChatRecord> queryAllByLimit(int offset, int limit) {
        return chatRecordDao.findAll(PageRequest.of((offset - 1)
                * limit, limit));
    }

    @Override
    public ChatRecord insert(ChatRecord chatRecord) {

        return chatRecordDao.save(chatRecord);
    }


    @Override
    public ChatRecord update(ChatRecord chatRecord) {

        return chatRecordDao.save(chatRecord);
    }


    @Override
    public boolean deleteById(Integer id) {

        try {
            chatRecordDao.deleteById(id);
        } catch (Exception ex) {
            return false;
        }
        return true;

    }
}


