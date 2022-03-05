package com.cslg.disk.example.user.entity;

import com.cslg.disk.common.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;

@Entity
@Data
public class UserAvater extends BaseEntity {
    private String url;

    private String fileName;

    private String typeName;

    private Integer userId;
}
