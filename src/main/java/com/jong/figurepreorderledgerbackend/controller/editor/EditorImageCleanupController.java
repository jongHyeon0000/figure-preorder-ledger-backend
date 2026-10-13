package com.jong.figurepreorderledgerbackend.controller.editor;

import com.jong.figurepreorderledgerbackend.common.response.ApiResponse;
import com.jong.figurepreorderledgerbackend.service.editor.EditorImageCleanupService;
import com.jong.figurepreorderledgerbackend.vo.editor.EditorImageCleanupResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/* 개발용: local 프로파일에서만 뜬다. 권한검증 없음(관리자 개념이 생기면 그때 붙인다) */
@Profile("local")
@Tag(name = "개발용", description = "local 프로파일에서만 열리는 개발 보조 API. 권한 검증이 없다.")
@RestController
@RequestMapping("/api/dev/editor-image/cleanup")
@RequiredArgsConstructor
public class EditorImageCleanupController {

    private final EditorImageCleanupService editorImageCleanupService;

    @Operation(
            summary = "고아 에디터 이미지 정리 수동 실행 (예: 어떤 상품에도 안 쓰이는 이미지 지우기)",
            description = "응답: scannedCount: 정리 대상 수, deletedCount: 삭제 수, failedCount: 실패 수")
    @PostMapping
    public ApiResponse<EditorImageCleanupResponse> cleanup() {
        return ApiResponse.ok("정리가 완료되었습니다.", editorImageCleanupService.cleanup());
    }
}
