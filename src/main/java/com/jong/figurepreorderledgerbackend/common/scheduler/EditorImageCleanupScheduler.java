package com.jong.figurepreorderledgerbackend.common.scheduler;

import com.jong.figurepreorderledgerbackend.service.editor.EditorImageCleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/* 매일 00시에 고아 에디터 이미지를 정리한다 */
@Component
@RequiredArgsConstructor
public class EditorImageCleanupScheduler {

    private final EditorImageCleanupService editorImageCleanupService;

    @Scheduled(cron = "0 0 0 * * *")
    public void cleanup() {
        editorImageCleanupService.cleanup();
    }
}
