package com.jong.figurepreorderledgerbackend.controller.editor;

import com.jong.figurepreorderledgerbackend.common.response.ApiResponse;
import com.jong.figurepreorderledgerbackend.common.security.CurrentUser;
import com.jong.figurepreorderledgerbackend.service.editor.EditorImageService;
import com.jong.figurepreorderledgerbackend.vo.editor.EditorImageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload/editor-image")
@RequiredArgsConstructor
public class EditorImageController {

    private final EditorImageService editorImageService;
    private final CurrentUser currentUser;

    /**
     * 에디터 이미지 업로드 (예: 상품 설명에 넣을 사진)
     * [POST] 단일 | /api/upload/editor-image
     *
     * @param file 업로드할 이미지 파일 (본문 multipart, 필드명 file, 필수, jpg/jpeg/png/gif/webp, 최대 5MB)
     *
     * @return ApiResponse<EditorImageVO> editorImageId, url(에디터가 이미지 src로 쓸 /uploads/ 주소)
     * @throws 400 파일 없음, 허용하지 않는 확장자, 파일 내용이 확장자와 다름, 크기 초과
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<EditorImageVO> upload(@RequestParam("file") MultipartFile file) {
        return ApiResponse.ok(editorImageService.upload(currentUser.getId(), file));
    }
}
