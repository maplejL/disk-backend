package com.cslg.disk.example.role.service;

import com.cslg.disk.example.right.entity.Right;
import com.cslg.disk.example.role.dao.RoleDao;
import com.cslg.disk.example.role.dto.RoleDto;
import com.cslg.disk.example.role.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleDao roleDao;


    @Override
    public List<Role> getAllRoles() {
        List<Role> all = roleDao.findAll();
        return all;
    }

    @Override
    public Object addRoles(RoleDto roleDto) {
        if (roleDto == null) {
            return null;
        }
        Role role = new Role();
        role.setRoleName(roleDto.getRoleName());
        List<String> list = roleDto.getRightIds();
        String rights = "("+list.stream().collect(Collectors.joining(","))+")";
        role.setRights(rights);
        Role save = roleDao.save(role);
        return save;
    }

    @Override
    public Object deleteRole(List<String> ids) {
        return roleDao.deleteByIds(ids) > 0;
    }
}
