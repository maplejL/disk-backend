package com.cslg.disk.example.chat.dto;

import lombok.Data;

import java.util.List;

@Data
public class ConversationDto {
    private List<Integer> userIds;

    private String conversationName;
}
