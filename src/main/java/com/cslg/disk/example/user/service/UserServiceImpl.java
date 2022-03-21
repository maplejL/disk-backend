package com.cslg.disk.example.user.service;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cslg.disk.common.exception.BusinessException;
import com.cslg.disk.example.chat.dao.TempChatDao;
import com.cslg.disk.example.chat.entity.TempChat;
import com.cslg.disk.example.file.dao.FileDao;
import com.cslg.disk.example.file.entity.MyFile;
import com.cslg.disk.example.file.service.FileService;
import com.cslg.disk.example.redis.RedisService;
import com.cslg.disk.example.socket.WebSocket;
import com.cslg.disk.example.user.dao.UserAvaterDao;
import com.cslg.disk.example.user.dao.UserDao;
import com.cslg.disk.example.user.dao.UserFriendDao;
import com.cslg.disk.example.user.dto.LoginDto;
import com.cslg.disk.example.user.dto.RegisterDto;
import com.cslg.disk.example.user.dto.UpdatePwdDto;
import com.cslg.disk.example.user.entity.MyUser;
import com.cslg.disk.example.user.entity.UserAvater;
import com.cslg.disk.example.user.util.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisService redisService;

    @Autowired
    private FileDao fileDao;

    @Autowired
    private UserAvaterDao userAvaterDao;

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
            String md5Password = getMd5Password(loginDto.getPassword(), false);
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
                UserAvater avater = userAvaterDao.findByUserId(userInfo.getId());
                if (avater == null) {
                    if (userInfo.getSex() == null) {
                        //未选择性别，默认男头像
                        avater = userAvaterDao.findByUserId(0);
                    }else {
                        avater = userAvaterDao.findByUserId(userInfo.getSex() == 0 ? 0 : -1);
                    }
                }
                map.put("avater", avater);
                redisService.setToken(userInfo, token);
                redisService.setValue(userInfo.getId().toString(), request.getRemoteAddr());
                List<TempChat> tempChats = tempChatDao.findTempChatsById(userInfo.getId());
                if (tempChats.size()>0) {
                    webSocket.sendOneObject(userInfo.getId().toString(), "您有未读聊天!");
                    Map<String, List<TempChat>> tempChatMap = new HashMap<>();
                    tempChatMap.put("tempChat", tempChats);
                    webSocket.sendOneObject(userInfo.getId().toString(), tempChatMap);
                }
                //用于开启敏感词汇监测的定时任务
//                ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
//                Runnable runnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        List<MyFile> byUserId = fileDao.findByUserId(userInfo.getId());
//                        System.out.println("111");
//                    }
//                };
//                scheduledExecutorService.scheduleAtFixedRate(runnable, 0,  1, TimeUnit.HOURS);
                return map;
            }
            throw new BusinessException("用户密码不正确");
        }
    }

    @Override
    public RSAUtils getKeys() {

        return null;
    }

    public String getMd5Password(String password, boolean isRegister) {
        String realPass = RSAUtils.privateDecrypt(Base64.decodeBase64(password), RSAUtils.privateKeya);
        if (isRegister == true) {
            //必须包含大小写字母和数字的组合，不能使用特殊字符，长度在 8-10 之间
            String patternUserName = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[a-zA-Z0-9]{8,10}$";
            Pattern r = Pattern.compile(patternUserName);
            Matcher m = r.matcher(realPass);
            if (m.matches() == false) {
                throw new BusinessException("密码必须包含大小写字母和数字的组合，不能使用特殊字符，长度在 8-10 之间");
            }
        }

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
        if (registerDto.getEmail() != null && !("".equals(registerDto.getEmail()))) {
            String patternEmail = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
            Pattern r = Pattern.compile(patternEmail);
            Matcher m = r.matcher(registerDto.getEmail());
            System.out.println(m.matches());
            if (m.matches() == false) {
                throw new BusinessException("邮箱地址不合法, 如(xxxxxxxxxxx@qq.com)");
            }
        }
        if (registerDto.getUsername() != null || !("".equals(registerDto.getUsername()))) {
            //字母开头，允许5-16字节，允许字母数字下划线
            String patternUserName = "^[a-zA-Z][a-zA-Z0-9_]{4,15}$";
            Pattern r = Pattern.compile(patternUserName);
            Matcher m = r.matcher(registerDto.getUsername());
            if (m.matches() == false) {
                throw new BusinessException("用户名应以字母开头,允许5-16字节,允许字母数字下划线");
            }
        }
        MyUser byUserName = userDao.findByUserName(registerDto.getUsername());
        if (byUserName != null) {
            throw new BusinessException("该用户名已存在");
        }
        MyUser myUser = new MyUser();
        if (registerDto.getPassword() != null) {
            registerDto.setPassword(getMd5Password(registerDto.getPassword(), true));
        }
        BeanUtils.copyProperties(registerDto, myUser);
        MyUser save = userDao.save(myUser);
        return save;
    }

    @Override
    public Object updatePwd(UpdatePwdDto updatePwdDto) {
        String newPassword = getMd5Password(updatePwdDto.getNewPassword(), true);
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
        webSocket.logout(id);
        log.info("用户"+id+"退出登录成功");
        return true;
    }

    @Override
    public MyUser getUserById(String id) {
        return userDao.findById(id);
    }

    @Override
    public MyUser refactor(MyUser user) {
        if (user.getUsername().length() > 8) {
            throw new BusinessException("用户名长度区间为8位以内");
        }
        MyUser byId = userDao.findById(user.getId().toString());
        user.setPassword(byId.getPassword());
        if (byId != null) {
            BeanUtils.copyProperties(user, byId);
        }
        MyUser save = userDao.save(byId);
        UserAvater avater = userAvaterDao.findByUserId(save.getId());
        save.setAvaterId(avater.getId());
        save.setAvaterType(avater.getTypeName());
        save.setAvaterUrl(avater.getUrl());
        return save;

    }

    @Override
    public List<MyUser> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public MyUser addUsers(RegisterDto registerDto) {
        if (registerDto == null) {
            return null;
        }
        MyUser register = register(registerDto);
        return register;
    }

    @Override
    public Object deleteUser(List<String> ids) {
        if (ids.size() == 0 || ids == null) {
            return null;
        }
        Integer integer = userDao.deleteByIds(ids);
        return integer;
    }

    public String getToken(MyUser user) {
        String token="";
        token= JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withIssuedAt(new Date())
                .withAudience(String.valueOf(user.getId()), user.getIsAdmin().toString())
                .sign(Algorithm.HMAC256(user.getPassword()));
        return token;
    }

    public static Integer getUserId(HttpServletRequest request) {
        String token = request.getHeader("token");
        DecodedJWT decode = JWT.decode(token);
        String userId = decode.getAudience().get(0);
        return Integer.valueOf(userId);
    }
}
