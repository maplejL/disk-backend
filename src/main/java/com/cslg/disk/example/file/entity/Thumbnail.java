package com.cslg.disk.example.file.entity;

import com.cslg.disk.common.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;

/***
 * 缩略图
 */
@Data
@Entity
public class Thumbnail extends BaseEntity {
    private String url;

    private String videoUrl;

    private String name;
}
