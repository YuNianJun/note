package com.notebook.note_back.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class CorsConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;
//        @Override
//        public void addCorsMappings(CorsRegistry registry) {
//            // 定义允许的原始域
//            String allowedOrigin = "http://localhost:8080";
//
//            registry.addMapping("/**")
//                    .allowCredentials(true)
//                    .allowedOrigins(allowedOrigin)
//                    .allowedMethods("POST","GET")
//                    .allowedHeaders("*")
//                    .exposedHeaders("*");
//        }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //登录接口和注册接口不拦截
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns("/user/login", "/user/register");
    }

}

