package com.cslg.disk.example.user.service;

import com.cslg.disk.example.user.dto.LoginDto;
import com.cslg.disk.example.user.dto.RegisterDto;
import com.cslg.disk.example.user.dto.UpdatePwdDto;
import com.cslg.disk.example.user.entity.MyUser;
import com.cslg.disk.example.user.util.RSAUtils;

import java.util.List;
import java.util.Map;

public interface UserService {
    Map<String, Object> login(LoginDto loginDto);

    RSAUtils getKeys();

    MyUser register(RegisterDto registerDto);

    Object updatePwd(UpdatePwdDto updatePwdDto);

    List<MyUser> getFriends(Integer id);

    String testLogin(LoginDto loginDto);
}
