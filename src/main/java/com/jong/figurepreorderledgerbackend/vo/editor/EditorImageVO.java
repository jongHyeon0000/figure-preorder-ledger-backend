package com.jong.figurepreorderledgerbackend.vo.editor;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 에디터 본문 이미지 (테이블 editor_image)
 * 요청, 응답, 매퍼 조회 결과에 같이 쓴다. [테이블명 joined](조인한 테이블), [request](요청 전용), [response](응답 전용) 표시가 붙은 필드는 컬럼이 아니다.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EditorImageVO {

    /** PK */
    private Long editorImageId;

    /** 소유 계정 */
    private Long userId;

    /** 서버 저장 경로 */
    private String filePath;

    /** 원본 파일명 */
    private String originalName;

    /** 파일 크기(byte) */
    private Long fileSize;

    /** 업로드일 (고아 파일 정리 기준) */
    private LocalDateTime createdAt;

    /** [response] 이미지를 내려주는 주소 (/uploads/ + file_path) */
    private String url;
}
