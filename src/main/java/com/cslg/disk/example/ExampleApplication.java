package com.cslg.disk.example;

import com.cslg.disk.config.SwaggerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableJpaAuditing
@Import(SwaggerConfig.class)
public class ExampleApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }

}
