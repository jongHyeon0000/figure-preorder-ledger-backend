package com.jong.figurepreorderledgerbackend.vo.editor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/* 고아 에디터 이미지 정리 결과 */
@Schema(description = "고아 에디터 이미지 정리 결과")
@Getter
@RequiredArgsConstructor
public class EditorImageCleanupResponse {

    /** 정리 대상으로 조회된 이미지 수 */
    @Schema(description = "정리 대상으로 조회된 이미지 수", example = "5")
    private final int scannedCount;

    /** 파일과 행을 모두 지운 수 */
    @Schema(description = "파일과 행을 모두 지운 수", example = "4")
    private final int deletedCount;

    /** 지우지 못한 수 (파일 삭제 실패 또는 조회 후 상품에 연결됨). 실패하면 다음 정리에서 다시 시도된다 */
    @Schema(description = "지우지 못한 수 (파일 삭제 실패 또는 조회 후 상품에 연결됨). 실패하면 다음 정리에서 다시 시도된다", example = "1")
    private final int failedCount;
}
