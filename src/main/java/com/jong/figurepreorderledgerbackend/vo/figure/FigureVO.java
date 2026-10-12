package com.jong.figurepreorderledgerbackend.vo.figure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jong.figurepreorderledgerbackend.common.constant.FigureStatus;
import com.jong.figurepreorderledgerbackend.common.constant.PaymentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 피규어 상품 (테이블 figure)
 * 테이블과 1:1인 순수 VO. 매퍼의 단일 행 조회와 UPDATE 파라미터에 쓴다.
 */
@Getter
@Setter
@NoArgsConstructor
public class FigureVO {

    /** PK */
    private Long figureId;

    /** 소유 계정 */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long userId;

    /** 상태: CART 장바구니, UNPURCHASED 미구매, RESERVED 예약완료, PURCHASED 구매완료, DELIVERED 배송완료 */
    private FigureStatus status;

    /** 결제 방식: FULL 전액, DEPOSIT 예약금+잔금 (미구매 이전에는 NULL, 미구매에서 넘길 때 확정) */
    private PaymentType paymentType;

    /** 상품명 */
    private String name;

    /** 배송예정월 (해당 월 1일) */
    private LocalDate deliveryMonth;

    /** 예약마감일 */
    private LocalDate reservationDeadline;

    /** 개당 가격(원). 총액은 price × quantity */
    private Integer price;

    /** 수량 */
    private Integer quantity;

    /** 결제한 예약금 총액 (예약금형만) */
    private Integer depositAmount;

    /** 잔금 기한 (예약금형만) */
    private LocalDate balanceDueDate;

    /** 예약금 결제일 */
    private LocalDate depositPaidAt;

    /** 전액 결제일 */
    private LocalDate paidAt;

    /** 배송완료일 */
    private LocalDate deliveredAt;

    /** 형태 하위 */
    private Long figureTypeId;

    /** 구매처(스토어) */
    private Long shopId;

    /** 제조사 */
    private Long makerId;

    /** 구매 링크 */
    private String purchaseLink;

    /** 상세 설명 (에디터 HTML) */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;

    /** 생성일 */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime createdAt;

    /** 수정일 */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime updatedAt;
}
