package com.cslg.disk.example.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class AddFriendsDto {
    private Integer selfId;

    private List<Integer> relateIds;
}
