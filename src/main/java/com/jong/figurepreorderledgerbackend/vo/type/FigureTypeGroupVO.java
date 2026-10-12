package com.jong.figurepreorderledgerbackend.vo.type;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 형태 상위 (테이블 figure_type_group)
 * 요청, 응답, 매퍼 조회 결과에 같이 쓴다. [테이블명 joined](조인한 테이블), [request](요청 전용), [response](응답 전용) 표시가 붙은 필드는 컬럼이 아니다.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FigureTypeGroupVO {

    /** PK */
    private Long figureTypeGroupId;

    /** 소유 계정 */
    private Long userId;

    /** 형태 상위명 (스케일, 넨도로이드) */
    @NotBlank
    @Size(max = 100)
    private String name;

    /** 생성일 */
    private LocalDateTime createdAt;

    /** 수정일 */
    private LocalDateTime updatedAt;

    /** [response] 하위 형태 목록 */
    private List<FigureTypeVO> children;
}
