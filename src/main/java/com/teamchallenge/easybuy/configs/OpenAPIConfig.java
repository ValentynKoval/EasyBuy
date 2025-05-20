package com.teamchallenge.easybuy.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "EasyBuy Marketplace API",
                version = "v1",
                description = "Документация REST-эндпойнтов"
        )
)
public class OpenAPIConfig { }