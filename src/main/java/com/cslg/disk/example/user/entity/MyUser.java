package com.cslg.disk.example.user.entity;

import com.cslg.disk.common.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;

@Entity
@Data
public class MyUser extends BaseEntity{
    private String username;

    private String password;

    private String email;

    private String city;

    //1为男，2为女
    private Integer sex;

}
