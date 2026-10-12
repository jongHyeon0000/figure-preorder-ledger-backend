package com.jong.figurepreorderledgerbackend.service.editor;

import com.jong.figurepreorderledgerbackend.vo.editor.EditorImageVO;
import org.springframework.web.multipart.MultipartFile;

public interface EditorImageService {

    /*
     * 이미지를 검사하고 저장한다. 이 시점에는 상품에 묶지 않는다(figure_id NULL).
     * 확장자와 파일 내용(앞부분 바이트)이 모두 허용된 이미지여야 한다.
     */
    EditorImageVO upload(Long userId, MultipartFile file);

    /* 상품 저장 전에 호출: 설명에 base64 이미지가 있으면 400 */
    void checkNoBase64(String html);
}
