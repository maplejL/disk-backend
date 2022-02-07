package com.cslg.disk.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends Throwable {
    @ResponseBody
    @ExceptionHandler(BusinessException.class)
    public SystemMessage exceptionHandler(HttpServletRequest req, HttpServletResponse res, Exception e) {
        BusinessException exception = (BusinessException) e;
        if (exception.getCode() == null) {
            res.setStatus(500);
        }
        res.setStatus(exception.getCode());
        return new SystemMessage(res.getStatus(), exception.getMessage());
    }
}
