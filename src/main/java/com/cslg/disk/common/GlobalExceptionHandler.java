package com.cslg.disk.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends Throwable {
    @ResponseBody
    @ExceptionHandler
    public Map<String, Object> exceptionHandler(HttpServletRequest req, Exception e) {
        log.info("url : {} , exception : {}", req.getRequestURI(), e.getMessage());
        Map<String, Object> ret = new HashMap<>();
        ret.put("url", req.getRequestURI());
        ret.put("exception", e.getMessage());
        return ret;
    }

}
