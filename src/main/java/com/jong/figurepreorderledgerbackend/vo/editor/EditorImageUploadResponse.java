package com.jong.figurepreorderledgerbackend.vo.editor;

import lombok.AllArgsConstructor;
import lombok.Getter;

/* 업로드 결과: 에디터는 url을 이미지 src로 쓴다 */
@Getter
@AllArgsConstructor
public class EditorImageUploadResponse {

    private final Long editorImageId;
    private final String url;
}
