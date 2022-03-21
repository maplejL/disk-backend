package com.cslg.disk.example.role.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleDto {
    private String roleName;

    private List<String> rightIds;
}
