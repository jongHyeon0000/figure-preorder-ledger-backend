package com.jong.figurepreorderledgerbackend.controller.editor;

import com.jong.figurepreorderledgerbackend.common.response.ApiResponse;
import com.jong.figurepreorderledgerbackend.service.editor.EditorImageCleanupService;
import com.jong.figurepreorderledgerbackend.vo.editor.EditorImageCleanupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/* 개발용: local 프로파일에서만 뜬다. 권한검증 없음(관리자 개념이 생기면 그때 붙인다) */
@Profile("local")
@RestController
@RequestMapping("/api/dev/editor-image/cleanup")
@RequiredArgsConstructor
public class EditorImageCleanupController {

    private final EditorImageCleanupService editorImageCleanupService;

    /**
     * 고아 에디터 이미지 정리 수동 실행 (예: 어떤 상품에도 안 쓰이는 이미지 지우기)
     * [POST] 단일 | /api/dev/editor-image/cleanup
     *
     * @return ApiResponse<EditorImageCleanupResponse> scannedCount: 정리 대상 수, deletedCount: 삭제 수, failedCount: 실패 수
     */
    @PostMapping
    public ApiResponse<EditorImageCleanupResponse> cleanup() {
        return ApiResponse.ok(editorImageCleanupService.cleanup());
    }
}
