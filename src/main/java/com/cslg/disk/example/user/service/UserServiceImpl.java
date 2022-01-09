package com.cslg.disk.example.user.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cslg.disk.common.exception.BusinessException;
import com.cslg.disk.example.user.dao.UserDao;
import com.cslg.disk.example.user.dto.LoginDto;
import com.cslg.disk.example.user.dto.RegisterDto;
import com.cslg.disk.example.user.entity.MyUser;
import com.cslg.disk.example.user.util.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public Map<String, Object> login(LoginDto loginDto) {

        MyUser userInfo = userDao.findByUserName(loginDto.getUsername());
        String password = Optional.ofNullable(userInfo).map(e -> e.getPassword()).orElse("");
        if (password.length() == 0) {
            throw new BusinessException("此用户不存在");
        } else {
            String md5Password = getMd5Password(loginDto.getPassword());
            if (md5Password.equals(password)) {
                String token = getToken(userInfo);
                Map<String, Object> map = new HashMap<>();
                map.put("token",(Object) token);
                userInfo.setPassword("空");
                map.put("userInfo", userInfo);
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
        MyUser myUser = new MyUser();
        registerDto.setPassword(getMd5Password(registerDto.getPassword()));
        BeanUtils.copyProperties(registerDto, myUser);
        MyUser save = userDao.save(myUser);
        return save;
    }

    public String getToken(MyUser user) {
        String token="";
        token= JWT.create().withAudience(String.valueOf(user.getId()))
                .sign(Algorithm.HMAC256(user.getPassword()));
        return token;
    }
}
