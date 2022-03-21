package com.cslg.disk.example.right.entity;

import com.cslg.disk.common.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class Right extends BaseEntity {
    private String rightName;

    private String description;
}
