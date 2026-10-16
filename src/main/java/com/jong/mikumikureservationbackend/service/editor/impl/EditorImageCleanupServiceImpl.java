package com.jong.mikumikureservationbackend.service.editor.impl;

import com.jong.mikumikureservationbackend.common.constant.UploadConstant;
import com.jong.mikumikureservationbackend.mapper.editor.EditorImageMapper;
import com.jong.mikumikureservationbackend.service.common.ImageFileStore;
import com.jong.mikumikureservationbackend.service.editor.EditorImageCleanupService;
import com.jong.mikumikureservationbackend.vo.editor.EditorImageCleanupResponse;
import com.jong.mikumikureservationbackend.vo.editor.EditorImageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/* 이미지마다 파일 삭제와 행 삭제를 따로 처리하므로 전체를 하나의 트랜잭션으로 묶지 않는다(한 건이 실패해도 나머지는 계속 처리) */
@Slf4j
@Service
@RequiredArgsConstructor
public class EditorImageCleanupServiceImpl implements EditorImageCleanupService {

    private final EditorImageMapper editorImageMapper;
    private final ImageFileStore imageFileStore;

    @Override
    public EditorImageCleanupResponse cleanup() {
        List<EditorImageVO> orphans = editorImageMapper.selectOrphans(UploadConstant.ORPHAN_RETENTION_DAYS);
        int deleted = 0;
        int failed = 0;
        for (EditorImageVO orphan : orphans) {
            if (deleteOne(orphan)) {
                deleted++;
            } else {
                failed++;
            }
        }
        log.info("에디터 이미지 정리: 대상 {}건, 삭제 {}건, 실패 {}건", orphans.size(), deleted, failed);
        return new EditorImageCleanupResponse(orphans.size(), deleted, failed);
    }

    /* 파일을 먼저 지우고 성공하면 행을 지운다. 파일 삭제에 실패하면 행을 남겨 다음 정리에서 다시 시도한다 */
    private boolean deleteOne(EditorImageVO orphan) {
        if (!imageFileStore.delete(orphan.getFilePath())) {
            return false;
        }
        if (editorImageMapper.deleteOrphanById(orphan.getEditorImageId()) == 0) {
            log.warn("정리 중 상품에 연결되어 행은 남김(파일은 이미 삭제됨): editorImageId={}", orphan.getEditorImageId());
            return false;
        }
        return true;
    }
}
