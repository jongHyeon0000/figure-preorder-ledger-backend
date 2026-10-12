package com.jong.figurepreorderledgerbackend.vo.editor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/* 고아 에디터 이미지 정리 결과 */
@Getter
@RequiredArgsConstructor
public class EditorImageCleanupResponse {

    /** 정리 대상으로 조회된 이미지 수 */
    private final int scannedCount;

    /** 파일과 행을 모두 지운 수 */
    private final int deletedCount;

    /** 지우지 못한 수 (파일 삭제 실패 또는 조회 후 상품에 연결됨). 실패하면 다음 정리에서 다시 시도된다 */
    private final int failedCount;
}
