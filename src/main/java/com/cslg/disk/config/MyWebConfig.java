package com.cslg.disk.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyWebConfig extends WebMvcConfigurationSupport {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        System.out.println("1");
        registry.addResourceHandler("/image/**").addResourceLocations("file:///C:/Users/user/Videos/Captures/");
        super.addResourceHandlers(registry);
    }
}