package com.jong.figurepreorderledgerbackend.vo.figure;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 상품 썸네일 이미지 (테이블 thumbnail_image)
 * 테이블과 1:1인 순수 VO. 매퍼의 단일 행 조회 결과에 쓴다.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThumbnailImageVO {

    /** PK */
    private Long thumbnailImageId;

    /** 소유 계정 */
    private Long userId;

    /** 썸네일이 속한 상품 */
    private Long figureId;

    /** 서버 저장 경로 */
    private String filePath;

    /** 원본 파일명 */
    private String originalName;

    /** 파일 크기(byte) */
    private Long fileSize;

    /** 업로드일 */
    private LocalDateTime createdAt;
}
