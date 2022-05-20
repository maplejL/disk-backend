package com.cslg.disk.example.file.entity;

import com.cslg.disk.common.BaseEntity;
import com.cslg.disk.example.user.entity.MyUser;
import com.cslg.disk.example.user.entity.UserAvater;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Transient;

@Data
@Entity
public class ShareRecord extends BaseEntity {
    private Integer fileId;

    private Integer userId;

    private String sharedIds;

    private String extractionCode;

    //30,7,1,0(永久)
    private Integer validPeriod;

    @Transient
    private MyFile file;

    @Transient
    private MyUser user;

    @Transient
    private Integer remainTime;

    @Transient
    private UserAvater avater;

    @Transient
    private String linkContent;
}
