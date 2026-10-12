package com.jong.figurepreorderledgerbackend.vo.figure;

import com.jong.figurepreorderledgerbackend.common.constant.FigureStatus;
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
@Getter
@Setter
@NoArgsConstructor
public class FigureSaveRequestVO {

    /** 상태: CART 장바구니, UNPURCHASED 미구매, RESERVED 예약완료, PURCHASED 구매완료, DELIVERED 배송완료 */
    private FigureStatus status;

    /** 상품명 */
    @NotBlank
    @Size(max = 255)
    private String name;

    /** 배송예정월 (해당 월 1일) */
    private LocalDate deliveryMonth;

    /** 예약마감일 */
    private LocalDate reservationDeadline;

    /** 개당 가격(원). 총액은 price × quantity */
    @NotNull
    @Min(0)
    private Integer price;

    /** 수량 */
    @NotNull
    @Min(1)
    private Integer quantity;

    /** 형태 하위 */
    @NotNull
    private Long figureTypeId;

    /** 구매처(스토어) */
    @NotNull
    private Long shopId;

    /** 제조사 */
    @NotNull
    private Long makerId;

    /** 구매 링크 */
    @Size(max = 2000)
    private String purchaseLink;

    /** 상세 설명 (에디터 HTML) */
    private String description;

    /** [request] 시리즈 id 목록 (등록, 수정 때 1개 이상 필수) */
    @NotEmpty
    private List<@NotNull Long> seriesIds;

    /** [request] 캐릭터 id 목록 (등록, 수정 때 1개 이상 필수) */
    @NotEmpty
    private List<@NotNull Long> characterIds;
}
