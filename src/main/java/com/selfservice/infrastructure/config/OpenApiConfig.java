package com.selfservice.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Self Service API")
                        .version("1.0")
                        .description("API para sistema de Self Service")
                        .contact(new Contact()
                                .name("Desenvolvedor")
                                .email("dev@selfservice.com")));
    }
} 