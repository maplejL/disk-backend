package com.cslg.disk.example.file.entity;

import com.cslg.disk.common.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class ShareRecord extends BaseEntity {
    private Integer fileId;

    private Integer userId;

    private String sharedIds;
}
