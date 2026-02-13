package com.jalveo.weatherAPI.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    @Value("${weather.api.connect-timeout}")
    private int connectTimeout;

    @Value("${weather.api.read-timeout}")
    private int readTimeout;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
