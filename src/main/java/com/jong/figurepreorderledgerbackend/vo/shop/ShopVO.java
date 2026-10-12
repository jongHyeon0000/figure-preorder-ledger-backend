package com.jong.figurepreorderledgerbackend.vo.shop;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 판매처 하위(스토어) (테이블 shop)
 * 요청, 응답, 매퍼 조회 결과에 같이 쓴다. [테이블명 joined](조인한 테이블), [request](요청 전용), [response](응답 전용) 표시가 붙은 필드는 컬럼이 아니다.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShopVO {

    /** PK */
    private Long shopId;

    /** 소유 계정 */
    private Long userId;

    /** 상위 채널 */
    private Long shopGroupId;

    /** 스토어명 (굿스마일 pw, 코믹스아트 등) */
    @NotBlank
    @Size(max = 100)
    private String name;

    /** 생성일 */
    private LocalDateTime createdAt;

    /** 수정일 */
    private LocalDateTime updatedAt;

    /** [shop_group joined] 상위 채널명 */
    private String shopGroupName;
}
