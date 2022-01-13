package com.cslg.disk.example.file.dto;

import lombok.Data;

import java.util.List;

@Data
public class DeleteFileDto {
    private List<Integer> ids;
}
