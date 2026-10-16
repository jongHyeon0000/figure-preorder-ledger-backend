package com.jong.mikumikureservationbackend.vo.figure;

import com.jong.mikumikureservationbackend.common.constant.FigureStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * 상품 등록, 수정 요청 본문. status는 등록에서만 쓰고(생략하면 CART) 수정에서는 무시한다.
 * [request] 표시가 붙은 필드는 컬럼이 아니다.
 */
@Schema(description = "상품 등록, 수정 요청 본문. status는 등록에서만 쓴다(수정에서는 무시)")
@Getter
@Setter
@NoArgsConstructor
public class FigureSaveRequestVO {

    /** 상태: CART 장바구니, UNPURCHASED 미구매, RESERVED 예약완료, PURCHASED 구매완료, DELIVERED 배송완료 */
    @Schema(description = "등록 상태. CART 또는 UNPURCHASED만 허용하며 생략하면 CART. 수정에서는 무시한다", example = "UNPURCHASED")
    private FigureStatus status;

    /** 상품명 */
    @Schema(description = "상품명 (255자 이하)", example = "넨도로이드 하츠네 미쿠", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Size(max = 255)
    private String name;

    /** 배송예정월 (해당 월 1일) */
    @Schema(description = "배송예정월 (해당 월 1일)", example = "2026-12-01")
    private LocalDate deliveryMonth;

    /** 예약마감일 */
    @Schema(description = "예약마감일", example = "2026-10-31")
    private LocalDate reservationDeadline;

    /** 개당 가격(원). 총액은 price × quantity */
    @Schema(description = "개당 가격(원, 0 이상)", example = "68000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Min(0)
    private Integer price;

    /** 수량 */
    @Schema(description = "수량 (1 이상)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Min(1)
    private Integer quantity;

    /** 형태 하위 */
    @Schema(description = "형태 하위 id (현재 사용자의 것)", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long figureTypeId;

    /** 구매처(스토어) */
    @Schema(description = "구매처(스토어) id (현재 사용자의 것)", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long shopId;

    /** 제조사 */
    @Schema(description = "제조사 id (현재 사용자의 것)", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long makerId;

    /** 구매 링크 */
    @Schema(description = "구매 링크 (2000자 이하)", example = "https://example.com/item/1")
    @Size(max = 2000)
    private String purchaseLink;

    /** 상세 설명 (에디터 HTML) */
    @Schema(description = "상세 설명 (에디터 HTML). 원본 그대로 저장하며 base64 이미지가 있으면 400")
    private String description;

    /** [request] 시리즈 id 목록 (등록, 수정 때 1개 이상 필수) */
    @Schema(description = "시리즈 id 목록 (1개 이상 필수)", example = "[1]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    private List<@NotNull Long> seriesIds;

    /** [request] 캐릭터 id 목록 (등록, 수정 때 1개 이상 필수) */
    @Schema(description = "캐릭터 id 목록 (1개 이상 필수)", example = "[4]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    private List<@NotNull Long> characterIds;
}
