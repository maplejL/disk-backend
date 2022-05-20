package com.cslg.disk.example.user.service;

import com.cslg.disk.example.user.dto.LoginDto;
import com.cslg.disk.example.user.dto.RegisterDto;
import com.cslg.disk.example.user.dto.UpdatePwdDto;
import com.cslg.disk.example.user.entity.MyUser;
import com.cslg.disk.example.user.entity.UserRelation;
import com.cslg.disk.example.user.util.RSAUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface UserService {
    Map<String, Object> login(LoginDto loginDto, HttpServletRequest request);

    RSAUtils getKeys();

    MyUser register(RegisterDto registerDto);

    Object updatePwd(UpdatePwdDto updatePwdDto);

    Map<String, List<MyUser>> getFriends(Integer id, Integer fileId);

    String testLogin(LoginDto loginDto);

    Object logout(String id);

    MyUser getUserById(String id);

    MyUser refactor(MyUser user);

    List<MyUser> getAllUsers();

    MyUser addUsers(RegisterDto registerDto);

    Object deleteUser(List<String> ids);

    Object addFriend(String username, HttpServletRequest request);

    List<UserRelation> getFriendApply(String id);

    boolean doFriendApply(String id,boolean isBuild);

    MyUser getUserByName(String name, HttpServletRequest request);

    boolean deleteFriend(String name, HttpServletRequest request);

    Map<String, Object> getOnlineUsers();
}
