package com.youthcase.orderflow.pr.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "po")
public record PoProperties(String baseUrl, String importPathItems, String importPathCheckout) {}
