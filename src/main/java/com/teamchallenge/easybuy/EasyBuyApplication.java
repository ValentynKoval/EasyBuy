package com.teamchallenge.easybuy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableCaching
@EnableJpaRepositories(basePackages = "com.teamchallenge.easybuy")
@Configuration
@SpringBootApplication
public class EasyBuyApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyBuyApplication.class, args);
    }

}

