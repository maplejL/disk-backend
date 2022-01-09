package com.cslg.disk.example.log;

import lombok.Data;

@Data
public class SysLog {

    private String className;

    private String methodName;

    private String params;

    private Long exeuTime;

    private String remark;

    private String createDate;
}
