package com.jong.mikumikureservationbackend.service.editor;

import com.jong.mikumikureservationbackend.vo.editor.EditorImageVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EditorImageService {

    /*
     * 이미지를 검사하고 저장한다. 이 시점에는 상품과 연결하지 않는다(상품을 저장할 때 설명에 쓰인 이미지만 연결).
     * 확장자와 파일 내용(앞부분 바이트)이 모두 허용된 이미지여야 한다.
     */
    EditorImageVO upload(Long userId, MultipartFile file);

    /* 상품 저장 전에 호출: 설명에 base64 이미지가 있으면 400 */
    void checkNoBase64(String html);

    /* 설명 HTML의 <img src> 중 업로드 주소(/uploads/...)에서 접두어를 뗀 file_path 목록(중복 없음). 외부 주소는 제외 */
    List<String> extractFilePaths(String html);
}
