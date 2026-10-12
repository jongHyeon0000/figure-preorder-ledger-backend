package com.jong.figurepreorderledgerbackend.vo.thumbnail;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/* 썸네일 업로드 결과: 이미지를 내려주는 주소 (/uploads/ + file_path) */
@Getter
@RequiredArgsConstructor
public class ThumbnailUrlResponse {

    private final String thumbnailUrl;
}
