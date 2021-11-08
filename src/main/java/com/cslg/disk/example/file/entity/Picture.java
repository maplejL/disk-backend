package com.cslg.disk.example.file.entity;

import com.cslg.disk.common.BaseEntity;
import lombok.Data;


import javax.persistence.Entity;

@Data
@Entity
public class Picture extends BaseEntity {

    private String url;

    private String size;

    private String fileName;
}
