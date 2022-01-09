package com.cslg.disk.example.aspect;

import com.cslg.disk.example.log.LogService;
import com.cslg.disk.example.log.SysLog;
import com.cslg.disk.example.log.SysLogAnno;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Aspect
@Slf4j
@Component
public class SysLogAspect {

    @Autowired
    private LogService logService;


    @Pointcut("@annotation(com.cslg.disk.example.log.SysLogAnno)")
    public void logPointCut() {}

    @After("logPointCut()")
    public Object after(JoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        Object result = point;

        long time = System.currentTimeMillis() - beginTime;
        try {
            saveLog(point, time);
        } catch (Exception e) {
        }
        return result;
    }


    private void saveLog(JoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SysLog sysLog = new SysLog();
        sysLog.setExeuTime(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        sysLog.setCreateDate(dateFormat.format(new Date()));
        SysLogAnno sysLogAnno = method.getAnnotation(SysLogAnno.class);
        if(sysLog != null){
            //注解上的描述
            sysLog.setRemark(sysLogAnno.value());
        }
        //请求的 类名、方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        sysLog.setClassName(className);
        sysLog.setMethodName(methodName);
        //请求的参数
        Object[] args = joinPoint.getArgs();
        try{
            List<String> list = new ArrayList<String>();
            for (Object o : args) {
                list.add(new Gson().toJson(o));
            }
            sysLog.setParams(list.toString());
        }catch (Exception e){ }
        logService.save(sysLog);
    }

}
