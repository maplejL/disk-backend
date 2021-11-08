package com.cslg.disk.example.support.entity;

import com.cslg.disk.common.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class SupportDict extends BaseEntity {
    private String typeName;

    private int typeCode;

    private String relationTable;
}
