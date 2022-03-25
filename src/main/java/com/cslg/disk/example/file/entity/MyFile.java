package com.cslg.disk.example.file.entity;

import com.cslg.disk.common.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.Date;

@Data
@Entity
public class MyFile extends BaseEntity {
    private String url;

    private String size;

    private String fileName;

    private int typeCode;

    private String thumbnailName;

    private String typeName;

    private static Boolean isHover = false;

    private Integer userId;

    @Transient
    private Date shareDate;
}
