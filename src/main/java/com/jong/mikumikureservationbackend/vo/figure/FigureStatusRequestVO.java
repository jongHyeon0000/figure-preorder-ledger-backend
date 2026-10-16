package com.jong.mikumikureservationbackend.vo.figure;

import com.jong.mikumikureservationbackend.common.constant.FigureStatus;
import com.jong.mikumikureservationbackend.common.constant.PaymentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * 상품 상태 변경 요청 본문 (단건, 일괄). 일괄 변경에서만 figureIds를 쓴다.
 * [request] 표시가 붙은 필드는 컬럼이 아니다.
 */
@Schema(description = "상품 상태 전이 요청 본문 (단건, 일괄). 일괄 전이에서만 figureIds를 쓴다")
@Getter
@Setter
@NoArgsConstructor
public class FigureStatusRequestVO {

    /** 상태: CART 장바구니, UNPURCHASED 미구매, RESERVED 예약완료, PURCHASED 구매완료, DELIVERED 배송완료 */
    @Schema(description = "다음 상태", example = "PURCHASED", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private FigureStatus status;

    /** 결제 방식: FULL 전액, DEPOSIT 예약금+잔금 (미구매 이전에는 NULL, 미구매에서 넘길 때 확정) */
    @Schema(description = "결제 방식. 미구매에서 넘길 때만 받는다 (RESERVED는 DEPOSIT, PURCHASED는 FULL)", example = "FULL")
    private PaymentType paymentType;

    /** 결제한 예약금 총액 (예약금형만) */
    @Schema(description = "예약금 총액(원). RESERVED 전이에서 필수", example = "20000")
    @Min(0)
    private Integer depositAmount;

    /** 예약금 결제일 */
    @Schema(description = "예약금 결제일. 생략하면 서버의 오늘 날짜", example = "2026-10-08")
    private LocalDate depositPaidAt;

    /** 잔금 기한 (예약금형만) */
    @Schema(description = "잔금 기한 (예약금형만)", example = "2026-12-01")
    private LocalDate balanceDueDate;

    /** 전액 결제일 */
    @Schema(description = "전액 결제일. 생략하면 서버의 오늘 날짜", example = "2026-10-08")
    private LocalDate paidAt;

    /** 배송완료일 */
    @Schema(description = "배송완료일. 생략하면 서버의 오늘 날짜", example = "2027-01-10")
    private LocalDate deliveredAt;

    /** [request] 일괄 상태 변경 대상 상품 id 목록 */
    @Schema(description = "일괄 전이 대상 상품 id 목록 (일괄 전이에서만 사용)", example = "[1, 2, 3]")
    private List<Long> figureIds;
}
