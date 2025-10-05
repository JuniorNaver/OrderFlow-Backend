package com.youthcase.orderflow.pr.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration @EnableConfigurationProperties(PoProperties.class)
public class PoClientConfig {
    @Bean WebClient poClient(PoProperties props) {
        return WebClient.builder().baseUrl(props.baseUrl()).build();
    }
}
