package com.jong.figurepreorderledgerbackend.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    @NotBlank
    @Size(min = 32)
    private final String secret;

    @Min(1)
    private final long expirationHours;

    public JwtProperties(String secret, long expirationHours) {
        this.secret = secret;
        this.expirationHours = expirationHours;
    }
}
