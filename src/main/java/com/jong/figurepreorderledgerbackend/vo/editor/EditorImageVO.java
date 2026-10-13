package com.jong.figurepreorderledgerbackend.vo.editor;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 에디터 본문 이미지 (테이블 editor_image)
 * 요청, 응답, 매퍼 조회 결과에 같이 쓴다. [테이블명 joined](조인한 테이블), [request](요청 전용), [response](응답 전용) 표시가 붙은 필드는 컬럼이 아니다.
 */
@Schema(description = "에디터 본문 이미지 업로드 결과")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EditorImageVO {

    /** PK */
    @Schema(description = "에디터 이미지 id", example = "1")
    private Long editorImageId;

    /** 소유 계정 */
    @Schema(description = "소유 계정 id. 요청 본문에 보내도 읽지 않는다", example = "1")
    private Long userId;

    /** 서버 저장 경로 */
    @Schema(description = "서버 저장 경로")
    private String filePath;

    /** 원본 파일명 */
    @Schema(description = "원본 파일명", example = "photo.png")
    private String originalName;

    /** 파일 크기(byte) */
    @Schema(description = "파일 크기(byte)", example = "102400")
    private Long fileSize;

    /** 업로드일 (고아 파일 정리 기준) */
    @Schema(description = "업로드일 (고아 파일 정리 기준)", example = "2026-10-12T21:30:00")
    private LocalDateTime createdAt;

    /** [response] 이미지를 내려주는 주소 (/uploads/ + file_path) */
    @Schema(description = "이미지를 내려주는 주소 (/uploads/ + file_path) (응답에서만 나온다)", example = "/uploads/1/editor-image/2026/10/uuid.png")
    private String url;
}
