package com.cslg.disk.example.chat.controller;

import com.cslg.disk.common.ResponseMessage;
import com.cslg.disk.example.chat.dto.ConversationDto;
import com.cslg.disk.example.chat.entity.Conversation;
import com.cslg.disk.example.chat.service.ConversationService;
import com.cslg.disk.example.user.anno.UserLoginToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * (Conversation)表控制层
 *
 * @author makejava
 * @since 2022-02-08 10:46:07
 */
@RestController
@RequestMapping("conversation")
public class ConversationController {
    /**
     * 服务对象
     */
    @Autowired
    private ConversationService conversationService;

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("selectOne")
    @UserLoginToken
    public ResponseMessage selectOne(Integer id) {
        return ResponseMessage.success(conversationService.queryById(id));
    }

    @PostMapping("/addConversation")
    @UserLoginToken
    public ResponseMessage addConversation(@RequestBody ConversationDto conversationDto) {
        return ResponseMessage.success(conversationService.insert(conversationDto));
    }

    @GetMapping("/conversations")
    @UserLoginToken
    public ResponseMessage getAllConversations(@RequestParam("id")Integer id) {
        return ResponseMessage.success(conversationService.getallById(id));
    }
}


