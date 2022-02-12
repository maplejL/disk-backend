package com.cslg.disk.example.user.dto;

import lombok.Data;

@Data
public class LoginDto {
    private String username;

    private String password;

    private Boolean stillLogin = false;
}
