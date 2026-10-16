package com.jong.mikumikureservationbackend.common.constant;

public final class UploadConstant {

    /* 업로드한 파일을 내려주는 주소의 앞부분. 상품 설명 HTML 안의 <img src>에 영구히 박히므로 바꾸지 않는다 */
    public static final String URL_PREFIX = "/uploads/";

    /* 어떤 상품의 설명에도 쓰이지 않는 에디터 이미지를, 업로드 후 이 일수가 지나면 정리 대상으로 본다 */
    public static final int ORPHAN_RETENTION_DAYS = 1;

    /* 업로드 폴더 아래 종류별 폴더 이름. 파일 경로는 {userId}/{종류 폴더}/{yyyy}/{MM}/{UUID}.{확장자} */
    public static final String EDITOR_IMAGE_DIR = "editor-image";
    public static final String THUMBNAIL_IMAGE_DIR = "thumbnail-image";

    private UploadConstant() {
    }
}
