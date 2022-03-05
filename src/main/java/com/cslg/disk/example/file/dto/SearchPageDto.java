package com.cslg.disk.example.file.dto;

import lombok.Data;

@Data
public class SearchPageDto {
    private int pageSize;

    private int pageNo;

    private int typeCode;

    private String input;

    private Integer userId;
}
