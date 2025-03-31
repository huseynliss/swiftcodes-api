package com.example.swiftcodeapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI swiftCodesOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SWIFT Codes API")
                        .description("API for managing SWIFT codes for banks worldwide")
                        .version("1.0"));
    }
}
