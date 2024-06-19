package com.yes255.yes255booksusersserver.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("book-user 서버 API")
                        .description("도서-회원 서버 API 명세서 입니다.")
                        .version("v0.0.1"));
    }
}
