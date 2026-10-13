package com.jong.figurepreorderledgerbackend.vo.series;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 시리즈 하위 (테이블 series)
 * 요청, 응답, 매퍼 조회 결과에 같이 쓴다. [테이블명 joined](조인한 테이블), [request](요청 전용), [response](응답 전용) 표시가 붙은 필드는 컬럼이 아니다.
 */
@Schema(description = "시리즈(하위). 요청, 응답에 같이 쓴다")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SeriesVO {

    /** PK */
    @Schema(description = "시리즈 id", example = "1")
    private Long seriesId;

    /** 소유 계정 */
    @Schema(description = "소유 계정 id. 요청 본문에 보내도 읽지 않는다", example = "1")
    private Long userId;

    /** 상위 시리즈 */
    @Schema(description = "상위 시리즈", example = "1")
    private Long seriesGroupId;

    /** 시리즈 하위명 (팝업퍼레이드, 매지컬미라이 등) */
    @Schema(description = "시리즈 하위명 (팝업퍼레이드, 매지컬미라이 등)", example = "팝업퍼레이드", requiredMode = Schema.RequiredMode.REQUIRED)
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

    /** [series_group joined] 상위 시리즈명 */
    @Schema(description = "상위 시리즈명 (조회 응답에서만 나온다)")
    private String seriesGroupName;

    /** [figure_series_map joined] 이 시리즈가 연결된 상품 (figure_id). 상품별로 묶을 때만 쓰고 응답에는 내보내지 않는다 */
    @JsonIgnore
    private Long figureId;

    /** [thumbnail_image joined] 썸네일 이미지 주소 (/uploads/ + file_path, 없으면 null) */
    @Schema(description = "썸네일 이미지 주소 (/uploads/ + file_path, 없으면 null) (조회 응답에서만 나온다)")
    private String thumbnailUrl;
}
