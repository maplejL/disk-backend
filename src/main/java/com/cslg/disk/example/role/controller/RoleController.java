package com.cslg.disk.example.role.controller;

import com.cslg.disk.common.ResponseMessage;
import com.cslg.disk.example.role.dto.RoleDto;
import com.cslg.disk.example.role.service.RoleService;
import com.cslg.disk.example.user.anno.UserLoginToken;
import com.cslg.disk.example.user.dto.RegisterDto;
import com.cslg.disk.example.user.entity.MyUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
@Slf4j
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/roles")
    @UserLoginToken(admin = true)
    @ResponseBody
    public ResponseMessage getAllUsers() {
        return ResponseMessage.success(roleService.getAllRoles());
    }

    @PostMapping("/addRoles")
    @UserLoginToken(admin = true)
    @ResponseBody
    public ResponseMessage addUsers(@RequestBody RoleDto roleDto) {
        return ResponseMessage.success(roleService.addRoles(roleDto));
    }

    @GetMapping("/deleteRole")
    @UserLoginToken(admin = true)
    @ResponseBody
    public ResponseMessage deleteUser(@RequestParam(value = "ids") List<String> ids) {
        return ResponseMessage.success(roleService.deleteRole(ids));
    }
}
