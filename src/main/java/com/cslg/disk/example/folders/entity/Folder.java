package com.cslg.disk.example.folders.entity;

import com.cslg.disk.common.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class Folder extends BaseEntity {
    private String folderName;
}
