package com.cslg.disk.example.user.controller;

import com.cslg.disk.common.exception.GlobalExceptionHandler;
import com.cslg.disk.common.ResponseMessage;
import com.cslg.disk.example.user.anno.UserLoginToken;
import com.cslg.disk.example.user.dto.LoginDto;
import com.cslg.disk.example.user.dto.RegisterDto;
import com.cslg.disk.example.user.dto.UpdatePwdDto;
import com.cslg.disk.example.user.entity.MyUser;
import com.cslg.disk.example.user.service.UserService;
import com.cslg.disk.example.user.util.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController extends GlobalExceptionHandler {

    protected static String publicKey;

    protected static String privateKey;

    static {
        Map<String, String> keys = RSAUtils.createKeys(512);
        publicKey = keys.get("publicKey");
        privateKey = keys.get("privateKey");
    }
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @ResponseBody
    public ResponseMessage login(@RequestBody LoginDto loginDto) {
        Map<String, Object> login = userService.login(loginDto);
        return ResponseMessage.isNul(login);
    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseMessage register(@RequestBody RegisterDto registerDto) {
        MyUser login = userService.register(registerDto);
        return ResponseMessage.isNul(login);
    }


    @GetMapping("/getPublicKey")
    @ResponseBody
    public ResponseMessage getPublicKey() {
//        Map<String, String> keys= RSAUtils.createKeys(512);
        Map<String, String> keys = new HashMap<>();
        keys.put("publicKey", publicKey);
        return ResponseMessage.isNul(keys);
    }

    @PutMapping("/update")
    @ResponseBody
    public ResponseMessage update(@RequestBody UpdatePwdDto updatePwdDto) {
        return ResponseMessage.isNul(userService.updatePwd(updatePwdDto));
    }

    @GetMapping("/getFriends")
    @UserLoginToken
    public ResponseMessage getFriends(@RequestParam(value = "id")Integer id) {
        return ResponseMessage.success(userService.getFriends(id));
    }
}
