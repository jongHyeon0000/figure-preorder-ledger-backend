package com.jong.figurepreorderledgerbackend.common.constant;

import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;

/* 썸네일이 붙는 대상의 종류. DB thumbnail_image.target_type에는 이름(FIGURE 등)이 저장되고, URL과 업로드 폴더에는 urlValue를 쓴다 */
public enum ThumbnailTarget {

    FIGURE("figure"),
    SHOP_GROUP("shop-group"),
    SHOP("shop"),
    FIGURE_TYPE_GROUP("figure-type-group"),
    FIGURE_TYPE("figure-type"),
    SERIES_GROUP("series-group"),
    SERIES("series"),
    GENRE_CHARACTER_GROUP("genre-character-group"),
    GENRE_CHARACTER("genre-character"),
    MAKER("maker"),
    USER("user");

    private final String urlValue;

    ThumbnailTarget(String urlValue) {
        this.urlValue = urlValue;
    }

    public String getUrlValue() {
        return urlValue;
    }

    /* URL 경로 값(shop-group 등)을 종류로 바꾼다. 모르는 값이면 400 */
    public static ThumbnailTarget fromUrlValue(String value) {
        for (ThumbnailTarget target : values()) {
            if (target.urlValue.equals(value)) {
                return target;
            }
        }
        throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "알 수 없는 썸네일 대상 종류입니다");
    }
}
