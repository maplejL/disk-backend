package com.cslg.disk.example.chat.dto;

import lombok.Data;

@Data
public class ChatDto {
    private Integer userId;

    private Integer conversationId;

    private String content;
}
