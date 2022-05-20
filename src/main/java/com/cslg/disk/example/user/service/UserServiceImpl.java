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
import com.cslg.disk.example.file.entity.ShareRecord;
import com.cslg.disk.example.file.service.FileService;
import com.cslg.disk.example.redis.RedisService;
import com.cslg.disk.example.socket.WebSocket;
import com.cslg.disk.example.user.controller.UserController;
import com.cslg.disk.example.user.dao.UserAvaterDao;
import com.cslg.disk.example.user.dao.UserDao;
import com.cslg.disk.example.user.dao.UserFriendDao;
import com.cslg.disk.example.user.dto.LoginDto;
import com.cslg.disk.example.user.dto.RegisterDto;
import com.cslg.disk.example.user.dto.UpdatePwdDto;
import com.cslg.disk.example.user.entity.MyUser;
import com.cslg.disk.example.user.entity.UserAvater;
import com.cslg.disk.example.user.entity.UserRelation;
import com.cslg.disk.example.user.util.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisService redisService;

    @Autowired
    private FileDao fileDao;

    @Autowired
    private FileService fileService;

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
            redisService.deleteValue("user:" + userInfo.getId());
        }
        if (password.length() == 0) {
            throw new BusinessException("此用户不存在");
        } else {
            String md5Password = getMd5Password(loginDto.getPassword(), false);
            if (md5Password.equals(password)) {
                String token = getToken(userInfo);
                if (redisService.getValue("user:" + userInfo.getId()) != null) {
                    //前台拦截后判断是否继续登录，若继续登录，先清除redis记录，再调用login
                    Map map = new HashMap();
                    map.put("message", "您已在其他设备登录");
                    map.put("username", userInfo.getUsername());
                    map.put("password", loginDto.getPassword());
                    throw new BusinessException(444, JSON.toJSONString(map));
                }
                MyUser userDto = new MyUser();
                BeanUtils.copyProperties(userInfo, userDto);
                Map<String, Object> map = new HashMap<>();
                map.put("token", (Object) token);
                userDto.setPassword("空");
                map.put("userInfo", userDto);
                UserAvater avater = userAvaterDao.findByUserId(userDto.getId());
                if (avater == null) {
                    if (userDto.getSex() == null) {
                        //未选择性别，默认男头像
                        avater = userAvaterDao.findByUserId(0);
                    } else {
                        avater = userAvaterDao.findByUserId(userDto.getSex() == 0 ? 0 : -1);
                    }
                }
                map.put("avater", avater);
                redisService.setToken(userDto, token);
                redisService.setValue(userDto.getId().toString(), request.getRemoteAddr());
                List<TempChat> tempChats = tempChatDao.findTempChatsById(userDto.getId());
                if (tempChats.size() > 0) {
                    webSocket.sendOneObject(userDto.getId().toString(), "您有未读聊天!");
                    Map<String, List<TempChat>> tempChatMap = new HashMap<>();
                    tempChatMap.put("tempChat", tempChats);
                    webSocket.sendOneObject(userDto.getId().toString(), tempChatMap);
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
                map.put("admin", false);
                if (userDto.getIsAdmin() == 1) {
                    map.put("admin", true);
                }
                return map;
            } else {
                throw new BusinessException("账号或密码不正确,当前还有" +
                        (UserController.limitLoginTime - UserController.loginTimes.get(request.getRemoteHost()))
                        + "次尝试次数,超过将锁定ip");
            }
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

    private void validateUser(String email, String name, String phone) {
        if (email != null && !("".equals(email))) {
            String patternEmail = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
            Pattern r = Pattern.compile(patternEmail);
            Matcher m = r.matcher(email);
            System.out.println(m.matches());
            if (m.matches() == false) {
                throw new BusinessException("邮箱地址不合法, 如(xxxxxxxxxxx@qq.com)");
            }
        }
        if (name != null && !("".equals(name))) {
            //字母开头，允许5-16字节，允许字母数字下划线
            String patternUserName = "^[a-zA-Z][a-zA-Z0-9_]{4,15}$";
            Pattern r = Pattern.compile(patternUserName);
            Matcher m = r.matcher(name);
            if (m.matches() == false) {
                throw new BusinessException("用户名应以字母开头,允许5-16字节,允许字母数字下划线");
            }
        }
        if (phone != null && !("".equals(phone))) {
//            String pattern = "^(13[0-9]|14[5|7]|15[0|1|2|3|4|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$";
            String patternPhone = "^(13[0-9]|14[5|7]|15[0|1|2|3|4|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$";
            Pattern r = Pattern.compile(patternPhone);
            Matcher m = r.matcher(phone);
            if (m.matches() == false) {
                throw new BusinessException("手机号不正确");
            }
        }
    }

    @Override
    public MyUser register(RegisterDto registerDto) {
        if (registerDto == null) {
            throw new BusinessException("无注册信息");
        }
        validateUser(registerDto.getEmail(), registerDto.getUsername(), registerDto.getPhone());
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
    public Map<String, List<MyUser>> getFriends(Integer id, Integer fileId) {
        if (null == id) {
            return null;
        }
        List<UserRelation> relations = userFriendDao.getFriendIds(id);
        List<Integer> friendIds = new ArrayList<>();
        relations.stream().forEach(relation -> {
            if (relation.getSelfId() == id) {
                friendIds.add(relation.getRelateId());
            } else {
                friendIds.add(relation.getSelfId());
            }
        });
        if (friendIds.size() == 0) {
            return null;
        }
        Map<String, List<MyUser>> map = new HashMap<>();
        if (fileId == 0) {
            List<MyUser> users = userDao.findByIds(friendIds);
            List<UserAvater> avaterList = userAvaterDao.findByUserIds(friendIds);
            Map<Integer, UserAvater> collect = avaterList.stream().collect(Collectors.toMap(UserAvater::getUserId, UserAvater -> UserAvater));
            for (MyUser user : users) {
                UserAvater avater = collect.get(user.getId());
                if (avater == null) {
                    if (user.getSex() == null) {
                        //未选择性别，默认男头像
                        avater = userAvaterDao.findByUserId(0);
                    } else {
                        avater = userAvaterDao.findByUserId(user.getSex() == 0 ? 0 : -1);
                    }
                }
                user.setAvaterUrl(avater.getUrl());
                user.setAvaterId(avater.getId());
                user.setAvaterType(avater.getTypeName());
            }
            map.put("allUsers", users);
            return map;
        } else {
            ShareRecord shareRecord = fileService.getByFileId(fileId);
            List<MyUser> sharedWith = new ArrayList<>();
            List<Integer> sharedWithIds = new ArrayList<>();
            if (shareRecord != null) {
                //该文件被分享
                String[] split = shareRecord.getSharedIds().split(",");
                for (String s : split) {
                    if (friendIds.contains(Integer.valueOf(s))) {
                        sharedWithIds.add(Integer.valueOf(s));
                    }
                }
                if (sharedWithIds.size() > 0) {
                    sharedWith = userDao.findByIds(sharedWithIds);
                }
            }
            if (sharedWith == null) {
                List<MyUser> users = userDao.findByIds(friendIds);
                map.put("allUsers", users);
                return map;
            } else {
                map.put("sharedWith", sharedWith);
//                friendIds.removeAll(sharedWithIds);
//                if (friendIds.size() > 0) {
//                    map.put("notSharedWith", userDao.findByIds(friendIds));
//                } else {
//                    map.put("notSharedWith", null);
//                }
                map.put("notSharedWith", userDao.findByIds(friendIds));
                return map;
            }
        }
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
        redisService.deleteValue("user:" + id);
        webSocket.logout(id);
        log.info("用户" + id + "退出登录成功");
        return true;
    }

    @Override
    public MyUser getUserById(String id) {
        return userDao.findById(id);
    }

    @Override
    public MyUser refactor(MyUser user) {
        validateUser(user.getEmail(), user.getUsername(), user.getPhone());
        MyUser byId = userDao.findById(user.getId().toString());
        user.setPassword(byId.getPassword());
        if (byId != null) {
            BeanUtils.copyProperties(user, byId);
        }
        MyUser save = userDao.save(byId);
        UserAvater avater = userAvaterDao.findByUserId(save.getId());
        if (avater != null) {
            save.setAvaterId(avater.getId());
            save.setAvaterType(avater.getTypeName());
            save.setAvaterUrl(avater.getUrl());
        }
        return save;

    }

    @Override
    public List<MyUser> getAllUsers() {
        return userDao.findAllNotDelete();
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

    //通过用户名查找用户
    @Override
    public Object addFriend(String username, HttpServletRequest request) {
        MyUser user = userDao.findByUserName(username);
        if (user == null) {
            //无此用户
            throw new BusinessException("该用户不存在!");
        }
        UserRelation relation;
        relation = userFriendDao.findIsExist(UserServiceImpl.getUserId(request), user.getId());
        if (relation != null) {
            if (relation.getIsBuild() == 1) {
                throw new BusinessException("您与此用户已成为好友!");
            } else {
                throw new BusinessException("您与此用户的好友申请已发送,请勿重新发送");
            }
        }
        relation = new UserRelation();
        relation.setSelfId(UserServiceImpl.getUserId(request));
        relation.setRelateId(user.getId());
        relation.setIsBuild(0);
        UserRelation save = userFriendDao.save(relation);
        return save;
    }

    @Override
    public List<UserRelation> getFriendApply(String id) {
        List<UserRelation> friendApply = userFriendDao.getFriendApply(id);
        for (UserRelation relation : friendApply) {
            //获取发送人
            MyUser myUser = userDao.findById(String.valueOf(relation.getSelfId()));
            relation.setUser(myUser);
        }
        return friendApply;
    }

    //处理好友申请
    @Override
    public boolean doFriendApply(String id, boolean isBuild) {
        UserRelation relation = userFriendDao.findById(Integer.valueOf(id)).get();
        if (isBuild) {
            relation.setIsBuild(1);
            userFriendDao.save(relation);
        } else {
            //不同意好友申请
            userFriendDao.deleteById(Integer.valueOf(id));
        }
        return true;
    }

    @Override
    public MyUser getUserByName(String name, HttpServletRequest request) {
        if (name.equals("")) {
            return null;
        }
        MyUser user = userDao.findByUserName(name);
        if (user == null) {
            return null;
        }
        UserAvater avater = userAvaterDao.findByUserId(user.getId());
        if (avater == null) {
            if (user.getSex() == null) {
                //未选择性别，默认男头像
                avater = userAvaterDao.findByUserId(0);
            } else {
                avater = userAvaterDao.findByUserId(user.getSex() == 0 ? 0 : -1);
            }
        }
        user.setAvaterUrl(avater.getUrl());
        return user;
    }

    @Override
    public boolean deleteFriend(String name, HttpServletRequest request) {
        MyUser user = userDao.findByUserName(name);
        if (user == null) {
            //无此用户
            throw new BusinessException("该用户不存在!");
        }
        Integer res = userFriendDao.deleteByUserId(UserServiceImpl.getUserId(request), user.getId());
        return res > 0;
    }

    @Override
    public Map<String, Object> getOnlineUsers() {
        List<String> onlineUsers = redisService.getOnlineUsers();
        List<MyUser> users = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        for (String onlineUser : onlineUsers) {
            //遍历每个用户的token
            DecodedJWT decode = JWT.decode(onlineUser);
            String userId = decode.getAudience().get(0);
            MyUser one = userDao.findById(userId);
            //获取该用户登录的ip
            Object ip = redisService.getValue(userId);
            users.add(one);
            map.put(userId, ip);
        }
        map.put("users", users);
        return map;
    }

    public String getToken(MyUser user) {
        String token = "";
        token = JWT.create()
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
