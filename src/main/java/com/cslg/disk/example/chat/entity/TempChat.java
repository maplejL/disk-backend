package com.cslg.disk.example.chat.entity;

import com.cslg.disk.common.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class TempChat extends BaseEntity {
    private String content;

    private String offLineUserIds;

    private Integer conversationId;
}
