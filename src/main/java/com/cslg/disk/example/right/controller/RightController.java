package com.cslg.disk.example.right.controller;

import com.cslg.disk.common.ResponseMessage;
import com.cslg.disk.example.right.service.RightService;
import com.cslg.disk.example.user.anno.UserLoginToken;
import com.cslg.disk.example.user.dto.RegisterDto;
import com.cslg.disk.example.user.entity.MyUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/right")
@Slf4j
public class RightController {

    @Autowired
    private RightService rightService;

    @GetMapping("/rights")
    @UserLoginToken(admin = true)
    @ResponseBody
    public ResponseMessage getAllUsers() {
        return ResponseMessage.success(rightService.getAllRoles());
    }

//    @PostMapping("/addRights")
//    @UserLoginToken(admin = true)
//    @ResponseBody
//    public ResponseMessage addUsers(@RequestBody RegisterDto registerDto) {
//        return ResponseMessage.success(rightService.addRights(registerDto));
//    }
//
//    @GetMapping("/deleteRight")
//    @UserLoginToken(admin = true)
//    @ResponseBody
//    public ResponseMessage deleteUser(@RequestParam(value = "ids") List<String> ids) {
//        return ResponseMessage.success(rightService.deleteUser(ids));
//    }
//
//    @PostMapping("/updateRight")
//    @UserLoginToken(admin = true)
//    @ResponseBody
//    public ResponseMessage updateUser(@RequestBody MyUser user) {
//        return ResponseMessage.success(rightService.refactor(user));
//    }
}
