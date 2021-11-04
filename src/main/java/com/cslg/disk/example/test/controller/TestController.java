package com.cslg.disk.example.test.controller;


import com.cslg.disk.example.test.service.TestService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Api(value = "Test", tags = "测试相关接口")
public class TestController {
    @Autowired
    private TestService testService;

    @GetMapping("/get")
    public void doGet() {
        testService.get();
        return;
    }
}
