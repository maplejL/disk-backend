package com.cslg.disk.example.user.entity;

import com.cslg.disk.common.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;
import java.util.List;

@Data
@Entity
public class UserRelation extends BaseEntity {
    private Integer selfId;

    private Integer relateId;
}
