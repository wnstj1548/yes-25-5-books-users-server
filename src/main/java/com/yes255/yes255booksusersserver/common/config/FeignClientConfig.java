package com.yes255.yes255booksusersserver.common.config;

import com.yes255.yes255booksusersserver.common.interceptor.JwtAuthorizationRequestInterceptor;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

    @Bean
    public JwtAuthorizationRequestInterceptor jwtAuthorizationRequestInterceptor() {
        return new JwtAuthorizationRequestInterceptor();
    }

    @Bean
    public RequestInterceptor requestInterceptor(
        JwtAuthorizationRequestInterceptor interceptor) {
        return interceptor;
    }
}
