package com.jong.figurepreorderledgerbackend.vo.figure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jong.figurepreorderledgerbackend.common.constant.FigureStatus;
import com.jong.figurepreorderledgerbackend.common.constant.PaymentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 피규어 상품 (테이블 figure)
 * 테이블과 1:1인 순수 VO. 매퍼의 단일 행 조회와 UPDATE 파라미터에 쓴다.
 */
@Schema(description = "피규어 상품 (테이블 figure과 1:1)")
@Getter
@Setter
@NoArgsConstructor
public class FigureVO {

    /** PK */
    @Schema(description = "상품 id", example = "12")
    private Long figureId;

    /** 소유 계정 */
    @Schema(description = "소유 계정 id. 요청 본문에 보내도 읽지 않는다", example = "1")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long userId;

    /** 상태: CART 장바구니, UNPURCHASED 미구매, RESERVED 예약완료, PURCHASED 구매완료, DELIVERED 배송완료 */
    @Schema(description = "상태: CART 장바구니, UNPURCHASED 미구매, RESERVED 예약완료, PURCHASED 구매완료, DELIVERED 배송완료", example = "UNPURCHASED")
    private FigureStatus status;

    /** 결제 방식: FULL 전액, DEPOSIT 예약금+잔금 (미구매 이전에는 NULL, 미구매에서 넘길 때 확정) */
    @Schema(description = "결제 방식: FULL 전액, DEPOSIT 예약금+잔금. 미구매 이전에는 null이고 미구매에서 넘길 때 확정된다", example = "FULL", nullable = true)
    private PaymentType paymentType;

    /** 상품명 */
    @Schema(description = "상품명", example = "넨도로이드 하츠네 미쿠")
    private String name;

    /** 배송예정월 (해당 월 1일) */
    @Schema(description = "배송예정월 (해당 월 1일)", example = "2026-12-01")
    private LocalDate deliveryMonth;

    /** 예약마감일 */
    @Schema(description = "예약마감일", example = "2026-10-31")
    private LocalDate reservationDeadline;

    /** 개당 가격(원). 총액은 price × quantity */
    @Schema(description = "개당 가격(원). 총액은 price × quantity", example = "68000")
    private Integer price;

    /** 수량 */
    @Schema(description = "수량", example = "1")
    private Integer quantity;

    /** 결제한 예약금 총액 (예약금형만) */
    @Schema(description = "결제한 예약금 총액(원). 예약금형만", example = "20000", nullable = true)
    private Integer depositAmount;

    /** 잔금 기한 (예약금형만) */
    @Schema(description = "잔금 기한. 예약금형만", example = "2026-12-01", nullable = true)
    private LocalDate balanceDueDate;

    /** 예약금 결제일 */
    @Schema(description = "예약금 결제일", example = "2026-10-08", nullable = true)
    private LocalDate depositPaidAt;

    /** 전액 결제일 */
    @Schema(description = "전액 결제일", example = "2026-10-08", nullable = true)
    private LocalDate paidAt;

    /** 배송완료일 */
    @Schema(description = "배송완료일", example = "2027-01-10", nullable = true)
    private LocalDate deliveredAt;

    /** 형태 하위 */
    @Schema(description = "형태 하위 id", example = "3")
    private Long figureTypeId;

    /** 구매처(스토어) */
    @Schema(description = "구매처(스토어) id", example = "5")
    private Long shopId;

    /** 제조사 */
    @Schema(description = "제조사 id", example = "2")
    private Long makerId;

    /** 구매 링크 */
    @Schema(description = "구매 링크", example = "https://example.com/item/1")
    private String purchaseLink;

    /** 상세 설명 (에디터 HTML) */
    @Schema(description = "상세 설명 (에디터 HTML). 목록 응답에는 포함되지 않는다")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;

    /** 생성일 */
    @Schema(description = "생성일시")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime createdAt;

    /** 수정일 */
    @Schema(description = "수정일시")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime updatedAt;
}
