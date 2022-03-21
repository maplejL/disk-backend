package com.cslg.disk.example.user.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cslg.disk.example.user.dto.LoginDto;
import com.cslg.disk.example.user.dto.RegisterDto;
import com.cslg.disk.example.user.dto.UpdatePwdDto;
import com.cslg.disk.example.user.entity.MyUser;
import com.cslg.disk.example.user.util.RSAUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface UserService {
    Map<String, Object> login(LoginDto loginDto, HttpServletRequest request);

    RSAUtils getKeys();

    MyUser register(RegisterDto registerDto);

    Object updatePwd(UpdatePwdDto updatePwdDto);

    List<MyUser> getFriends(Integer id);

    String testLogin(LoginDto loginDto);

    Object logout(String id);

    MyUser getUserById(String id);

    MyUser refactor(MyUser user);

    List<MyUser> getAllUsers();

    MyUser addUsers(RegisterDto registerDto);

    Object deleteUser(List<String> ids);
}
