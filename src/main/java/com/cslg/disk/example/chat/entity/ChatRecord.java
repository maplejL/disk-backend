package com.cslg.disk.example.chat.entity;

import com.cslg.disk.common.BaseEntity;
import com.cslg.disk.example.user.entity.UserAvater;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.Date;
import java.io.Serializable;

/**
 * (ChatRecord)实体类
 *
 * @author makejava
 * @since 2022-02-08 11:14:45
 */
@Data
@Entity
public class ChatRecord extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 205027065730733024L;

    private String content;

    private Integer conversationId;

    private Integer sendUser;

    private String sendUserName;

    @Transient
    private UserAvater avater;
}

