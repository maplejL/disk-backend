package com.cslg.disk.example.user.controller;

import com.cslg.disk.common.exception.BusinessException;
import com.cslg.disk.common.exception.GlobalExceptionHandler;
import com.cslg.disk.common.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import com.cslg.disk.example.user.anno.UserLoginToken;
import com.cslg.disk.example.user.dto.LoginDto;
import com.cslg.disk.example.user.dto.RegisterDto;
import com.cslg.disk.example.user.dto.UpdatePwdDto;
import com.cslg.disk.example.user.entity.MyUser;
import com.cslg.disk.example.user.service.UserService;
import com.cslg.disk.example.user.util.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.Session;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController extends GlobalExceptionHandler {

    protected static String publicKey;

    protected static String privateKey;

    protected Map<String, Integer> loginTimes = new ConcurrentHashMap<>();

    protected Set<String> lockedHost = new ConcurrentSkipListSet<>();

    //默认登录失败次数为3
    protected Integer limitLoginTime = 5;

    static {
        Map<String, String> keys = RSAUtils.createKeys(512);
        publicKey = keys.get("publicKey");
        privateKey = keys.get("privateKey");
    }
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @ResponseBody
    public ResponseMessage login(@RequestBody LoginDto loginDto, HttpServletRequest request) {
        String host = request.getRemoteHost();
        if (lockedHost.contains(host)) {
            throw new BusinessException("当前ip已被锁定,请联系管理员进行解锁");
        }
        if (limitLoginTime > 0) {
            Integer times = loginTimes.getOrDefault(host, 0);
            times++;
            if (times > limitLoginTime) {
                lockedHost.add(host);
                throw new BusinessException("当前ip已被锁定,请联系管理员进行解锁");
            }
            loginTimes.put(host, times);
        }
        Map<String, Object> login = userService.login(loginDto, request);
        if (limitLoginTime > 0) {
            loginTimes.remove(host);
        }
        return ResponseMessage.isNul(login);
    }

    @GetMapping("/logout")
    @ResponseBody
    public ResponseMessage logout(@RequestParam("id")String id) {
        return ResponseMessage.success(userService.logout(id));
    }

    @PostMapping("/testLogin")
    public String testLogin(@RequestBody LoginDto loginDto) {
        return userService.testLogin(loginDto);
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

    @PostMapping("/refactor")
    @UserLoginToken
    public ResponseMessage refactor(@RequestBody MyUser user) {
        return ResponseMessage.success(userService.refactor(user));
    }

    /**
     * 获取被锁定的ip和用户
     * @return
     */
    @GetMapping("/locked")
    @UserLoginToken(admin = true, required = false)
    @ResponseBody
    public ResponseMessage getLockedHost() {
        Map<String, Set<String>> map = new HashMap<>();
        map.put("lockedHosts", lockedHost);
        return ResponseMessage.success(map);
    }

    /**
     * 解锁用户和ip
     * @param hosts ip列表
     * @return
     */
    @GetMapping("/unlock")
    @UserLoginToken(admin = true)
    @ResponseBody
    public Boolean unlockHost(@RequestParam(value = "hosts") List<String> hosts) {
        if (hosts == null) {
            return false;
        }
        for (String host : hosts) {
            lockedHost.remove(host);
            loginTimes.remove(host);
        }
        return true;
    }

    @GetMapping("/users")
    @UserLoginToken(admin = true)
    @ResponseBody
    public ResponseMessage getAllUsers() {
        return ResponseMessage.success(userService.getAllUsers());
    }
}
