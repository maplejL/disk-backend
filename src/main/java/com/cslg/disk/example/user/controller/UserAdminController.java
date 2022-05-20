package com.cslg.disk.example.user.controller;

import com.alibaba.fastjson.JSON;
import com.cslg.disk.common.ResponseMessage;
import com.cslg.disk.example.user.anno.UserLoginToken;
import com.cslg.disk.example.user.dto.RegisterDto;
import com.cslg.disk.example.user.entity.MyUser;
import com.cslg.disk.example.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/admin")
@Slf4j
public class UserAdminController {
    @Autowired
    private UserService userService;

    /**
     * 获取被锁定的ip和用户
     * @return
     */
    @GetMapping("/locked")
    @UserLoginToken(admin = true, required = false)
    @ResponseBody
    public ResponseMessage getLockedHost() {
        Map<String, Set<String>> map = new HashMap<>();
        //不存在一个ip锁多次的情况
        //模拟存在被锁ip
//        UserController.lockedHost.add("127.0.0.1");
        map.put("lockedHosts", UserController.lockedHost);
        return ResponseMessage.success(map);
    }

    /**
     * 解锁用户和ip
     * @param host ip
     * @return
     */
    @GetMapping("/unlock")
    @UserLoginToken(admin = true)
    @ResponseBody
    public Boolean unlockHost(@RequestParam(value = "host") String host) {
        if (host == null || host.equals("")) {
            return false;
        }
        UserController.lockedHost.remove(host);
        UserController.loginTimes.remove(host);
        return true;
    }

    @GetMapping("/onlineUsers")
    @UserLoginToken(admin = true)
    @ResponseBody
    public ResponseMessage onlineUsers() {
        return ResponseMessage.success(userService.getOnlineUsers());
    }

    @GetMapping("/users")
    @UserLoginToken(admin = true)
    @ResponseBody
    public ResponseMessage getAllUsers() {
        return ResponseMessage.success(userService.getAllUsers());
    }

    @PostMapping("/addUsers")
    @UserLoginToken(admin = true)
    @ResponseBody
    public ResponseMessage addUsers(@RequestBody RegisterDto registerDto) {
        return ResponseMessage.success(userService.addUsers(registerDto));
    }

    @GetMapping("/deleteUser")
    @UserLoginToken(admin = true)
    @ResponseBody
    public ResponseMessage deleteUser(@RequestParam("ids") String ids) {
        return ResponseMessage.success(userService.deleteUser((List<String>) JSON.parse(ids)));
    }

    @PostMapping("/updateUser")
    @UserLoginToken(admin = true)
    @ResponseBody
    public ResponseMessage updateUser(@RequestBody MyUser user) {
        return ResponseMessage.success(userService.refactor(user));
    }
}
