package com.cslg.disk.example.file.entity;

import com.cslg.disk.common.BaseEntity;
import com.cslg.disk.example.user.entity.MyUser;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

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

    @Transient
    private List<MyUser> sharedUsers;
}
