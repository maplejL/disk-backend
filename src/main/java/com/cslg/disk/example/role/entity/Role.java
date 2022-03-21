package com.cslg.disk.example.role.entity;

import com.cslg.disk.common.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class Role extends BaseEntity {
    private String roleName;

    private String description;

    private String rights;
}
