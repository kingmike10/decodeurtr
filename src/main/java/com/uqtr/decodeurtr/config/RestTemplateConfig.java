package com.uqtr.decodeurtr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        // Retourne un RestTemplate simple. La gestion des content-types particuliers sera faite
        return new RestTemplate();
    }
}