package com.jong.mikumikureservationbackend.vo.thumbnail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/* 썸네일 업로드 결과: 이미지를 내려주는 주소 (/uploads/ + file_path) */
@Schema(description = "썸네일 업로드 결과")
@Getter
@RequiredArgsConstructor
public class ThumbnailUrlResponse {

    @Schema(description = "이미지를 내려주는 주소 (/uploads/ + file_path)", example = "/uploads/1/thumbnail-image/figure/2026/10/uuid.png")
    private final String thumbnailUrl;
}
