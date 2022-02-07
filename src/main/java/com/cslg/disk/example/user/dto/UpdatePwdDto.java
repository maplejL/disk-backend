package com.cslg.disk.example.user.dto;

import lombok.Data;

@Data
public class UpdatePwdDto {
    private String id;

    private String newPassword;
}
