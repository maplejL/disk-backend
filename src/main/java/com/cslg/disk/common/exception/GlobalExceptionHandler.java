package com.cslg.disk.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends Throwable {
    @ResponseBody
    @ExceptionHandler
    public SystemMessage exceptionHandler(HttpServletRequest req, HttpServletResponse res, Exception e) {
        res.setStatus(500);
        return new SystemMessage(500, e.getMessage());
    }
    //注意这里  切记返回的是数据 加上 @ResponseBody  否则无法使用
//    @ResponseBody
//    @ExceptionHandler(value = Exception.class)
//    public SystemMessage exceptionHandler(Exception e) {
//        //注意 ResponData是我自己定义的返回类型，大家可以根据自己需要进行设计并返回
//        return new SystemMessage(500, e.getMessage());
//    }

}
