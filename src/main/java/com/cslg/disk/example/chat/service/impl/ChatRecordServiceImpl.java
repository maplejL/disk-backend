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
import com.cslg.disk.example.user.dao.UserAvaterDao;
import com.cslg.disk.example.user.entity.UserAvater;
import com.cslg.disk.example.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private UserAvaterDao userAvaterDao;

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private UserService userService;

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
    public ChatRecord sendMessage(ChatDto chatDto) {
        Integer conversationId = chatDto.getConversationId();
        Conversation conversation = conversationService.queryById(conversationId);
        List<String> ids = Arrays.stream(conversation.getUserIds().split(",")).filter(e -> !e.equals(chatDto.getUserId()))
                .collect(Collectors.toList());
        StringBuilder offLineUserIds = new StringBuilder();
        List<String> onLineUserIds = new ArrayList<>();
        for (String id : ids) {
            //判断是否在线，不在线保存到待推送消息
            Integer isOffLine = socket.isOnLine(id);
            if (isOffLine == 0) {
                offLineUserIds.append(id).append(",");
            } else {
                onLineUserIds.add(id);
            }
        }
        offLineUserIds.delete(offLineUserIds.length()-1, offLineUserIds.length());
        if (offLineUserIds.length() > 0) {
            TempChat tempChat = new TempChat();
            tempChat.setContent(chatDto.getContent());
            tempChat.setOffLineUserIds(offLineUserIds.toString());
            tempChat.setConversationId(conversationId);
            tempChat.setConversationName(conversation.getConversationName());
            tempChatDao.save(tempChat);
        }
        ChatRecord chatRecord = new ChatRecord();
        chatRecord.setSendUser(chatDto.getUserId());
        chatRecord.setContent(chatDto.getContent());
        chatRecord.setConversationId(conversationId);
        chatRecord.setSendUserName(userService.getUserById(chatDto.getUserId().toString()).getUsername());
        UserAvater avater = userAvaterDao.findByUserId(chatDto.getUserId());
        chatRecord.setAvater(avater);
        chatRecord = insert(chatRecord);
        Map<String, List<ChatRecord>> map = new HashMap<>();
        List<ChatRecord> list = new ArrayList<>();
        list.add(chatRecord);
        map.put("newChatRecord", list);
        for (String onLineUserId : onLineUserIds) {
            socket.sendOneObject(onLineUserId, map);
        }
        return chatRecord;
    }

    @Override
    public Object deleteTempChat(Integer id, Integer userId) {
        TempChat one = tempChatDao.getOne(id);
        String offLineUserIds = one.getOffLineUserIds();
        String[] split = offLineUserIds.split(",");
        StringBuilder builder = new StringBuilder();
        for (String s : split) {
            if (!s.equals(userId.toString())) {
                builder.append(s).append(",");
            }
        }
        builder.toString().replaceAll(" ", "");
        builder.replace(builder.length()-1, builder.length(), "");
        one.setOffLineUserIds(builder.toString());
        TempChat save = tempChatDao.save(one);

        return save;
    }

    @Override
    public List<ChatRecord> getChatRecordByConversationId(String id) {
        List<ChatRecord> byConversationId = chatRecordDao.findByConversationId(id);
        if (byConversationId == null || byConversationId.size() == 0) {
            return null;
        }
        List<Integer> userIds = byConversationId.stream().map(e -> e.getSendUser()).collect(Collectors.toList());
        List<UserAvater> avaters = userAvaterDao.findByUserIds(userIds);
        List<Integer> haveAvaterUserIds = avaters.stream().map(e -> e.getUserId()).collect(Collectors.toList());
        byConversationId.stream().forEach(e -> {
            UserAvater avater;
            if (!haveAvaterUserIds.contains(e.getSendUser())){
                avater = new UserAvater();
                avater.setUrl("static/image/img.png");
                e.setAvater(avater);
            } else {
                avater = avaters.get(haveAvaterUserIds.indexOf(e.getSendUser()));
            }
            e.setAvater(avater);
        });
        return byConversationId;
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


