package com.jong.figurepreorderledgerbackend.vo.figure;

import com.jong.figurepreorderledgerbackend.common.constant.FigureStatus;
import com.jong.figurepreorderledgerbackend.common.constant.PaymentType;
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
@Getter
@Setter
@NoArgsConstructor
public class FigureStatusRequestVO {

    /** 상태: CART 장바구니, UNPURCHASED 미구매, RESERVED 예약완료, PURCHASED 구매완료, DELIVERED 배송완료 */
    @NotNull
    private FigureStatus status;

    /** 결제 방식: FULL 전액, DEPOSIT 예약금+잔금 (미구매 이전에는 NULL, 미구매에서 넘길 때 확정) */
    private PaymentType paymentType;

    /** 결제한 예약금 총액 (예약금형만) */
    @Min(0)
    private Integer depositAmount;

    /** 예약금 결제일 */
    private LocalDate depositPaidAt;

    /** 잔금 기한 (예약금형만) */
    private LocalDate balanceDueDate;

    /** 전액 결제일 */
    private LocalDate paidAt;

    /** 배송완료일 */
    private LocalDate deliveredAt;

    /** [request] 일괄 상태 변경 대상 상품 id 목록 */
    private List<Long> figureIds;
}
