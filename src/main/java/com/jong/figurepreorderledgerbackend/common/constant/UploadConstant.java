package com.jong.figurepreorderledgerbackend.common.constant;

public final class UploadConstant {

    /* 업로드한 파일을 내려주는 주소의 앞부분. 상품 설명 HTML 안의 <img src>에 영구히 박히므로 바꾸지 않는다 */
    public static final String URL_PREFIX = "/uploads/";

    /* 업로드 폴더 아래 종류별 폴더 이름. 파일 경로는 {userId}/{종류 폴더}/{yyyy}/{MM}/{UUID}.{확장자} */
    public static final String EDITOR_IMAGE_DIR = "editor-image";
    public static final String THUMBNAIL_IMAGE_DIR = "thumbnail-image";

    private UploadConstant() {
    }
}
