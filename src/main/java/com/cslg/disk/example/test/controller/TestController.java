package com.cslg.disk.example.test.controller;


import com.cslg.disk.example.log.SysLogAnno;
import com.cslg.disk.example.redis.RedisService;
import com.cslg.disk.example.test.service.TestService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Api(value = "Test", tags = "测试相关接口")
@Slf4j
public class TestController {
    @Autowired
    private TestService testService;

    @Autowired
    private RedisService redisService;

    @SysLogAnno("测试")
    @GetMapping("/get")
    public void doGet() {
        testService.get();
        return;
    }

    @GetMapping("/testRedis")
    public void testRedis() {
//        redisService.setValue("test", "测试");
        Object test = redisService.getValue("test");
        log.info(test.toString());
    }


}
