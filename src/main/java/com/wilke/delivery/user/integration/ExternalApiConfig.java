package com.wilke.delivery.user.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExternalApiConfig {

    @Bean
    ExternalApiClient externalApiClient(@Value("${external-api-service.baseUrl}") String serviceUrl) {
        return new ExternalApiClient(serviceUrl);
    }

}
