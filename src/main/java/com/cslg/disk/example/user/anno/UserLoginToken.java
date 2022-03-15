package com.cslg.disk.example.user.anno;

import com.cslg.disk.example.user.entity.MyUser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserLoginToken {
    boolean required() default true;
    int id() default 0;
    boolean admin() default false;
}
