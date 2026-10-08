package com.jong.figurepreorderledgerbackend.config.properties;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Getter
@Validated
@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {

    @NotEmpty
    private final List<String> allowedOrigins;

    public CorsProperties(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }
}
