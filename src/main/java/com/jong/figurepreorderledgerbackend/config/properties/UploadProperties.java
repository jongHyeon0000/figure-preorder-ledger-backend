package com.jong.figurepreorderledgerbackend.config.properties;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.annotation.Validated;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Getter
@Validated
@ConfigurationProperties(prefix = "app.upload")
public class UploadProperties {

    @NotNull
    private final Path dir;

    @NotNull
    private final DataSize maxSize;

    @NotEmpty
    private final List<String> allowedExtensions;

    public UploadProperties(Path dir, DataSize maxSize, List<String> allowedExtensions) {
        this.dir = dir;
        this.maxSize = maxSize;
        this.allowedExtensions = allowedExtensions == null ? null : normalizeAll(allowedExtensions);
    }

    public boolean isAllowedExtension(String extension) {
        return extension != null && allowedExtensions.contains(normalize(extension));
    }

    private static List<String> normalizeAll(List<String> extensions) {
        return Collections.unmodifiableList(
                extensions.stream()
                        .map(UploadProperties::normalize)
                        .collect(Collectors.toList())
        );
    }

    private static String normalize(String extension) {
        return extension.trim().toLowerCase(Locale.ROOT).replaceFirst("^\\.", "");
    }
}
