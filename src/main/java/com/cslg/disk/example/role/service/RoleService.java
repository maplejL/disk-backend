package com.cslg.disk.example.role.service;

import com.cslg.disk.example.role.dto.RoleDto;
import com.cslg.disk.example.role.entity.Role;

import java.util.List;

public interface RoleService {
    List<Role> getAllRoles();

    Object addRoles(RoleDto roleDto);

    Object deleteRole(List<String> ids);
}
