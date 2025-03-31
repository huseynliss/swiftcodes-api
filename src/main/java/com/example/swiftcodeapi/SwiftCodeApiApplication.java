package com.example.swiftcodeapi;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition
public class SwiftCodeApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwiftCodeApiApplication.class, args);
    }



}