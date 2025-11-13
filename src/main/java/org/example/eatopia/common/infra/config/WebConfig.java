package org.example.eatopia.common.infra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")       // 모든 경로에 대해
                .allowedOrigins("*")       // 모든 도메인(Origin)에서의 요청을 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // 허용할 HTTP 메소드
                .allowedHeaders("*")       // 모든 헤더 허용
                .maxAge(3600);             // Pre-flight 요청 캐시 시간 (초)
    }
}