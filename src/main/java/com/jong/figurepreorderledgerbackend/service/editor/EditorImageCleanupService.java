package com.jong.figurepreorderledgerbackend.service.editor;

import com.jong.figurepreorderledgerbackend.vo.editor.EditorImageCleanupResponse;

public interface EditorImageCleanupService {

    /*
     * 어떤 상품과도 연결되지 않은 채 보관 기간(UploadConstant.ORPHAN_RETENTION_DAYS)이 지난 에디터 이미지의 파일과 행을 지운다.
     * 매일 00시 스케줄러와 local 전용 수동 호출이 같은 메서드를 쓴다.
     */
    EditorImageCleanupResponse cleanup();
}
