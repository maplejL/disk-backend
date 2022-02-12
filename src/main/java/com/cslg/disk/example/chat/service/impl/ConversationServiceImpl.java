package com.cslg.disk.example.chat.service.impl;

import com.cslg.disk.example.chat.dto.ConversationDto;
import com.cslg.disk.example.chat.entity.Conversation;
import com.cslg.disk.example.chat.dao.ConversationDao;
import com.cslg.disk.example.chat.service.ConversationService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

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
        return conversations;
    }

    @Override
    public Page<Conversation> queryAllByLimit(int offset, int limit) {
        return conversationDao.findAll(PageRequest.of((offset - 1) * limit, limit));
    }

    @Override
    public Conversation insert(ConversationDto conversationDto) {
        List<Integer> userIds = conversationDto.getUserIds();
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


