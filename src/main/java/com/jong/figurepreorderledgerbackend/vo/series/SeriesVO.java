package com.jong.figurepreorderledgerbackend.vo.series;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SeriesVO {

    /** PK */
    private Long seriesId;

    /** 소유 계정 */
    private Long userId;

    /** 상위 시리즈 */
    private Long seriesGroupId;

    /** 시리즈 하위명 (팝업퍼레이드, 매지컬미라이 등) */
    @NotBlank
    @Size(max = 100)
    private String name;

    /** 설명 (일반 텍스트) */
    @Size(max = 1000)
    private String description;

    /** 생성일 */
    private LocalDateTime createdAt;

    /** 수정일 */
    private LocalDateTime updatedAt;

    /** [series_group joined] 상위 시리즈명 */
    private String seriesGroupName;

    /** [figure_series_map joined] 이 시리즈가 연결된 상품 (figure_id). 상품별로 묶을 때만 쓰고 응답에는 내보내지 않는다 */
    @JsonIgnore
    private Long figureId;

    /** [thumbnail_image joined] 썸네일 이미지 주소 (/uploads/ + file_path, 없으면 null) */
    private String thumbnailUrl;
}
