package com.jong.mikumikureservationbackend.vo.series;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 시리즈 상위 (테이블 series_group)
 * 요청, 응답, 매퍼 조회 결과에 같이 쓴다. [테이블명 joined](조인한 테이블), [request](요청 전용), [response](응답 전용) 표시가 붙은 필드는 컬럼이 아니다.
 */
@Schema(description = "시리즈 그룹(상위). 요청, 응답에 같이 쓴다. 응답에는 하위 시리즈 목록(children)이 중첩된다")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SeriesGroupVO {

    /** PK (쿼리에서 series_group_id AS id) */
    @Schema(description = "시리즈 그룹 id", example = "1")
    private Long id;

    /** 소유 계정 */
    @Schema(description = "소유 계정 id. 요청 본문에 보내도 읽지 않는다", example = "1")
    private Long userId;

    /** 시리즈 상위명 (굿스마일 라인업, 미쿠 시리즈) */
    @Schema(description = "시리즈 상위명 (굿스마일 라인업, 미쿠 시리즈)", example = "굿스마일 라인업", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Size(max = 100)
    private String name;

    /** 설명 (일반 텍스트) */
    @Schema(description = "설명 (일반 텍스트)", example = "자유롭게 적는 설명")
    @Size(max = 1000)
    private String description;

    /** 생성일 */
    @Schema(description = "생성일시", example = "2026-10-12T21:30:00")
    private LocalDateTime createdAt;

    /** 수정일 */
    @Schema(description = "수정일시", example = "2026-10-12T21:30:00")
    private LocalDateTime updatedAt;

    /** [response] 하위 시리즈 목록 */
    @Schema(description = "하위 시리즈 목록 (응답에서만 나온다)")
    private List<SeriesVO> children;

    /** [thumbnail_image joined] 썸네일 이미지 주소 (/uploads/ + file_path, 없으면 null) */
    @Schema(description = "썸네일 이미지 주소 (/uploads/ + file_path, 없으면 null) (조회 응답에서만 나온다)")
    private String thumbnailUrl;
}
