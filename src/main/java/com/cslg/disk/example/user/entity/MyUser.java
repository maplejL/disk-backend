package com.cslg.disk.example.user.entity;

import com.cslg.disk.common.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@Data
public class MyUser extends BaseEntity{
    private String username;

    private String password;

    private String email;

    private String city;

    //1为男，2为女
    private Integer sex;

    private String phone;

    private String address;

    @Transient
    private Integer avaterId;

    @Transient
    private String avaterType;

    @Transient
    private String avaterUrl;

}
