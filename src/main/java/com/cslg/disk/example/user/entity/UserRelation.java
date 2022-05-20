package com.cslg.disk.example.user.entity;

import com.cslg.disk.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.List;

@Data
@Entity
public class UserRelation extends BaseEntity {
    private Integer selfId;

    private Integer relateId;

    //1为通过好友申请，0待通过
    private Integer isBuild;

    @Transient
    private MyUser user;
}
