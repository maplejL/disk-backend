package com.cslg.disk.example.test.entity;


import com.cslg.disk.common.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Test extends BaseEntity {

    private String name;
}
