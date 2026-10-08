package com.jong.figurepreorderledgerbackend.config;

import com.jong.figurepreorderledgerbackend.config.properties.CorsProperties;
import com.jong.figurepreorderledgerbackend.config.properties.UploadProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        UploadProperties.class,
        CorsProperties.class
})
public class PropertiesConfig {
}
