package com.jong.mikumikureservationbackend.config.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@ConfigurationProperties(prefix = "app.swagger")
public class SwaggerProperties {

    @NotBlank
    private final String username;

    @NotBlank
    private final String password;

    public SwaggerProperties(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
