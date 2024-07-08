package com.ticketty.tickettyapp;

import com.ticketty.tickettyapp.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Value("${cors.allowedOrigins}")
    private String allowedOrigins;

    @Value("${excludePathPatterns}")
    private String excludePathPatterns;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins.split(",")) // 허용할 출처
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 허용할 HTTP method
                .allowedHeaders("Authorization", "Content-Type", "Refresh")
                .allowCredentials(true) // 쿠키 인증 요청 허용
                .maxAge(3000); // 원하는 시간만큼 pre-flight 리퀘스트를 캐싱
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(excludePathPatterns.split(","));
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}