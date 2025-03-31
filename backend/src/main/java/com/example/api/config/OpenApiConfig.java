package com.example.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        log.info("Initializing OpenAPI configuration");
        return new OpenAPI()
                .info(new Info()
                        .title("SPEG Chat API")
                        .description("API pour l'application SPEG Chat")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("SPEG Team")
                                .email("contact@example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}