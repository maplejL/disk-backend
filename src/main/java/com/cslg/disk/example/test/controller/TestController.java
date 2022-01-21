package com.cslg.disk.example.test.controller;


import com.cslg.disk.example.log.SysLogAnno;
import com.cslg.disk.example.redis.RedisService;
import com.cslg.disk.example.socket.WebSocket;
import com.cslg.disk.example.test.service.TestService;
import com.qcloud.cos.model.ObjectListing;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
@Api(value = "Test", tags = "测试相关接口")
@Slf4j
public class TestController {
    @Autowired
    private TestService testService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private WebSocket webSocket;

    @SysLogAnno("测试")
    @GetMapping("/get")
    public Map<String, Date> doGet() {
        Map<String, Date> objectListing = testService.get();
        return objectListing;
    }

    @GetMapping("/testRedis")
    public void testRedis() {
//        redisService.setValue("test", "测试");
        Object test = redisService.getValue("test");
        log.info(test.toString());
    }


    @RequestMapping("/sendAllWebSocket")
    public String test() {
        webSocket.sendAllMessage("清晨起来打开窗，心情美美哒~");
        return null;
    }

    @RequestMapping("/sendOneWebSocket")
    public String sendOneWebSocket() {
        webSocket.sendOneMessage("DPS007", "只要你乖给你买条gai！");
        return "websocket单人发送";
    }


}
