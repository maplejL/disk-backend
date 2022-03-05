package com.cslg.disk.example.chat.entity;

import com.cslg.disk.common.BaseEntity;
import com.cslg.disk.example.user.entity.UserAvater;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.Date;
import java.io.Serializable;
import java.util.List;

/**
 * (Conversation)实体类
 *
 * @author makejava
 * @since 2022-02-08 10:46:09
 */
@Data
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class Conversation extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 953340369077241987L;

    private String userIds;

    private String conversationName;

    @Transient
    private List<UserAvater> userAvaters;
}

