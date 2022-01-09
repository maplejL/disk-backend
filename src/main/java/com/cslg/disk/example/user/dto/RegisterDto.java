package com.cslg.disk.example.user.dto;

import lombok.Data;

@Data
public class RegisterDto {
    private String username;

    private String password;

    private String email;

    private String city;

    //1为男，2为女
    private Integer sex;
}
