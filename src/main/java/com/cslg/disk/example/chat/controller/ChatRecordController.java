package com.cslg.disk.example.chat.controller;

import com.cslg.disk.example.chat.dto.ChatDto;
import com.cslg.disk.example.chat.entity.ChatRecord;
import com.cslg.disk.example.chat.service.ChatRecordService;
import com.cslg.disk.example.user.anno.UserLoginToken;
import org.springframework.web.bind.annotation.*;
import com.cslg.disk.common.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;

/**
 * (ChatRecord)表控制层
 *
 * @author makejava
 * @since 2022-02-08 11:14:44
 */
@RestController
@RequestMapping("chatRecord")
public class ChatRecordController {
    /**
     * 服务对象
     */
    @Autowired
    private ChatRecordService chatRecordService;

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("selectOne")
    public ResponseMessage selectOne(Integer id) {
        return ResponseMessage.success(chatRecordService.queryById(id));
    }

    @PostMapping("/send")
    @UserLoginToken
    public ResponseMessage sendMessage(@RequestBody ChatDto chatDto) {
        return ResponseMessage.success(chatRecordService.sendMessage(chatDto));
    }

    @GetMapping("/deleteTempChat")
    @UserLoginToken
    public ResponseMessage deleteTempChat(@RequestParam("ids")List<Integer> ids) {
        return ResponseMessage.success(chatRecordService.deleteTempChat(ids));
    }

}


