package com.youthcase.orderflow.global.config.iamport;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "iamport.api")
public class IamportProperties {
    @NotBlank private String key;
    @NotBlank private String secret;
}
