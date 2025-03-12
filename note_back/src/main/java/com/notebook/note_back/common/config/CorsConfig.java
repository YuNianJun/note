package com.notebook.note_back.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 定义允许的原始域
        String allowedOrigin = "http://localhost:8080";

        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedOrigins(allowedOrigin)
                .allowedMethods("POST","GET")  // 直接传递可变参数，简化代码
                .allowedHeaders("*")
                .exposedHeaders("*");
    }
}

