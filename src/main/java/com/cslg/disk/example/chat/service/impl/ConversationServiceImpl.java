package com.cslg.disk.example.chat.service.impl;

import com.cslg.disk.example.chat.dto.ConversationDto;
import com.cslg.disk.example.chat.entity.Conversation;
import com.cslg.disk.example.chat.dao.ConversationDao;
import com.cslg.disk.example.chat.service.ConversationService;
import com.cslg.disk.example.user.dao.UserAvaterDao;
import com.cslg.disk.example.user.dao.UserDao;
import com.cslg.disk.example.user.entity.MyUser;
import com.cslg.disk.example.user.entity.UserAvater;
import com.cslg.disk.example.user.service.UserServiceImpl;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * (Conversation)表服务实现类
 *
 * @author makejava
 * @since 2022-02-08 10:46:10
 */
@Service("conversationService")
@Transactional
public class ConversationServiceImpl implements ConversationService {
    @Resource
    private ConversationDao conversationDao;

    @Autowired
    private UserAvaterDao userAvaterDao;

    @Autowired
    private UserDao userDao;

    @Override
    public Conversation queryById(Integer id) {
        return conversationDao.getOne(id);
    }

    @Override
    public List<Conversation> getall() {
        return conversationDao.findAll();
    }

    @Override
    public List<Conversation> getallById(Integer id) {
        List<Conversation> conversations = conversationDao.getallById(id);
        for (Conversation conversation : conversations) {
            String[] userIds = conversation.getUserIds().split(",");
            List<UserAvater> avaters = new ArrayList<>();
            List<Integer> ids = new ArrayList<>();
            for (int i = 0; i < userIds.length; i++) {
                ids.add(Integer.valueOf(userIds[i]));
            }
            List<UserAvater> avaterList = userAvaterDao.findByUserIds(ids);
            for (UserAvater avater : avaterList) {
                if (avater == null) {
                    MyUser user = userDao.getOne(avater.getUserId());
                    if (user.getSex() == null) {
                        //未选择性别，默认男头像
                        avater = userAvaterDao.findByUserId(0);
                    } else {
                        avater = userAvaterDao.findByUserId(user.getSex() == 0 ? 0 : -1);
                    }
                }
            }
            conversation.setUserAvaters(avaterList);
        }
        return conversations;
    }

    @Override
    public Page<Conversation> queryAllByLimit(int offset, int limit) {
        return conversationDao.findAll(PageRequest.of((offset - 1) * limit, limit));
    }

    @Override
    public Conversation insert(ConversationDto conversationDto, HttpServletRequest request) {
        List<Integer> userIds = conversationDto.getUserIds();
        userIds.add(UserServiceImpl.getUserId(request));
        String conversationName = conversationDto.getConversationName();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < userIds.size(); i++) {
            if (i == userIds.size() - 1) {
                builder.append(userIds.get(i));
            } else {
                builder.append(userIds.get(i)).append(",");
            }
        }
        Conversation conversation = new Conversation();
        if (conversationName == null) {
            List<MyUser> users = userDao.findByIds(userIds);
            StringBuilder name = new StringBuilder();
            for (int i = 0; i < 3 && i != users.size(); i++) {
                name.append(users.get(i).getUsername()).append(",");
            }
            name.replace(name.length()-1, name.length(), "");
            conversationName = name.toString();
        }
        conversation.setConversationName(conversationName);
        conversation.setUserIds(builder.toString());
        return conversationDao.save(conversation);
    }


    @Override
    public Conversation update(Conversation conversation) {

        return conversationDao.save(conversation);
    }


    @Override
    public boolean deleteById(Integer id) {

        try {
            conversationDao.deleteById(id);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }
}


