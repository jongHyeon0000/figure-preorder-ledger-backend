package com.jong.figurepreorderledgerbackend.vo.shop;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 판매처 상위(채널) (테이블 shop_group)
 * 요청, 응답, 매퍼 조회 결과에 같이 쓴다. [테이블명 joined](조인한 테이블), [request](요청 전용), [response](응답 전용) 표시가 붙은 필드는 컬럼이 아니다.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShopGroupVO {

    /** PK */
    private Long shopGroupId;

    /** 소유 계정 */
    private Long userId;

    /** 채널명 (네이버 스토어, 일반 스토어) */
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

    /** [response] 하위 스토어 목록 */
    private List<ShopVO> children;

    /** [thumbnail_image joined] 썸네일 이미지 주소 (/uploads/ + file_path, 없으면 null) */
    private String thumbnailUrl;
}
