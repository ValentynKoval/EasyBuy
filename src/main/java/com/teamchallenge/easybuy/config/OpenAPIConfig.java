package com.teamchallenge.easybuy.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "EasyBuy Marketplace API",
                version = "v1",
                description = "Documentation of REST endpoints"
        )
)
public class OpenAPIConfig {
}