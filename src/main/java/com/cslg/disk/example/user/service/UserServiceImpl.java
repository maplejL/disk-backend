package com.cslg.disk.example.user.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cslg.disk.common.exception.BusinessException;
import com.cslg.disk.example.chat.dao.TempChatDao;
import com.cslg.disk.example.chat.entity.TempChat;
import com.cslg.disk.example.chat.service.TempChatService;
import com.cslg.disk.example.redis.RedisService;
import com.cslg.disk.example.socket.WebSocket;
import com.cslg.disk.example.user.dao.UserDao;
import com.cslg.disk.example.user.dao.UserFriendDao;
import com.cslg.disk.example.user.dto.LoginDto;
import com.cslg.disk.example.user.dto.RegisterDto;
import com.cslg.disk.example.user.dto.UpdatePwdDto;
import com.cslg.disk.example.user.entity.MyUser;
import com.cslg.disk.example.user.util.RSAUtils;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.hibernate.id.UUIDGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.UUIDEditor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.Session;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserFriendDao userFriendDao;

    @Autowired
    private TempChatDao tempChatDao;

    @Autowired
    private WebSocket webSocket;

    @Override
    public Map<String, Object> login(LoginDto loginDto, HttpServletRequest request) {
        if ("".equals(loginDto.getUsername()) || "".equals(loginDto.getPassword())) {
            throw new BusinessException("用户名或密码为空");
        }
        MyUser userInfo = userDao.findByUserName(loginDto.getUsername());
        String password = Optional.ofNullable(userInfo).map(e -> e.getPassword()).orElse("");
        if (loginDto.getStillLogin()) {
            redisService.deleteValue("user:"+userInfo.getId());
        }
        if (password.length() == 0) {
            throw new BusinessException("此用户不存在");
        } else {
            String md5Password = getMd5Password(loginDto.getPassword());
            if (md5Password.equals(password)) {
                String token = getToken(userInfo);
                if (redisService.getValue("user:"+userInfo.getId()) != null) {
                    //前台拦截后判断是否继续登录，若继续登录，先清除redis记录，再调用login
                    Map map = new HashMap();
                    map.put("message", "您已在其他设备登录");
                    map.put("username", userInfo.getUsername());
                    map.put("password", loginDto.getPassword());
                    throw new BusinessException(444, JSON.toJSONString(map));
                }
                Map<String, Object> map = new HashMap<>();
                map.put("token",(Object) token);
                userInfo.setPassword("空");
                map.put("userInfo", userInfo);
                redisService.setToken(userInfo, token);
                redisService.setValue(userInfo.getId().toString(), request.getRemoteAddr());
                List<TempChat> tempChats = tempChatDao.findTempChatsById(userInfo.getId());
                if (tempChats.size()>0) {
                    webSocket.sendOneMessage(userInfo.getId().toString(), "您有未读聊天!");
                    Map<String, List<TempChat>> tempChatMap = new HashMap<>();
                    tempChatMap.put("tempChat", tempChats);
                    webSocket.sendOneObject(userInfo.getId().toString(), tempChatMap);
                }
                return map;
            }
            throw new BusinessException("用户密码不正确");
        }
    }

    @Override
    public RSAUtils getKeys() {

        return null;
    }

    private String getMd5Password(String password) {
        String realPass = RSAUtils.privateDecrypt(Base64.decodeBase64(password), RSAUtils.privateKeya);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(realPass.getBytes());
        return new BigInteger(1, md.digest()).toString(16);
    }

    @Override
    public MyUser register(RegisterDto registerDto) {
        if (registerDto == null) {
            throw new BusinessException("无注册信息");
        }
        MyUser byUserName = userDao.findByUserName(registerDto.getUsername());
        if (byUserName != null) {
            throw new BusinessException("该用户名已存在");
        }
        MyUser myUser = new MyUser();
        registerDto.setPassword(getMd5Password(registerDto.getPassword()));
        BeanUtils.copyProperties(registerDto, myUser);
        MyUser save = userDao.save(myUser);
        return save;
    }

    @Override
    public Object updatePwd(UpdatePwdDto updatePwdDto) {
        String newPassword = getMd5Password(updatePwdDto.getNewPassword());
        MyUser user = userDao.findById(updatePwdDto.getId());
        user.setPassword(newPassword);
        MyUser save = userDao.save(user);
        return save;
    }

    @Override
    public List<MyUser> getFriends(Integer id) {
        if (null == id) {
            return null;
        }
        List<Integer> friendIds = userFriendDao.getFriendIds(id);
        if (friendIds.size() == 0) {
            return null;
        }
        List<MyUser> users = userDao.findByIds(friendIds);
        return users;
    }

    @Override
    public String testLogin(LoginDto loginDto) {
        MyUser byUserName = userDao.findByUserName(loginDto.getUsername());
        String token = getToken(byUserName);
        redisService.setToken(byUserName, token);
        return token;
    }

    @Override
    public Object logout(String id) {
        redisService.deleteValue("user:"+id);
        log.info("用户"+id+"退出登录成功");
        return true;
    }

    @Override
    public MyUser getUserById(String id) {
        return userDao.findById(id);
    }

    public String getToken(MyUser user) {
        String token="";
        token= JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withIssuedAt(new Date())
                .withAudience(String.valueOf(user.getId()))
                .sign(Algorithm.HMAC256(user.getPassword()));
        return token;
    }
}
