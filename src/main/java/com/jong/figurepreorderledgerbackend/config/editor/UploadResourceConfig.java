package com.jong.figurepreorderledgerbackend.config.editor;

import com.jong.figurepreorderledgerbackend.common.constant.UploadConstant;
import com.jong.figurepreorderledgerbackend.config.properties.UploadProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/* 업로드 폴더의 파일을 /uploads/** 주소로 내려준다 */
@Configuration
@RequiredArgsConstructor
public class UploadResourceConfig implements WebMvcConfigurer {

    private final UploadProperties uploadProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = uploadProperties.getDir().toAbsolutePath().normalize().toUri().toString();
        if (!location.endsWith("/")) {
            location = location + "/";
        }
        registry.addResourceHandler(UploadConstant.URL_PREFIX + "**").addResourceLocations(location);
    }
}
