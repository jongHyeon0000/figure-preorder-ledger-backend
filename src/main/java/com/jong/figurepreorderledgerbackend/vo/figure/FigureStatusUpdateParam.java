package com.jong.figurepreorderledgerbackend.vo.figure;

import com.jong.figurepreorderledgerbackend.common.constant.FigureStatus;
import com.jong.figurepreorderledgerbackend.common.constant.PaymentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/* 상태 변경 UPDATE에 넘기는 값. null인 칸은 기존 값을 그대로 둔다 */
@Getter
@Setter
@NoArgsConstructor
public class FigureStatusUpdateParam {

    private Long userId;
    private Long id;
    private FigureStatus status;
    private PaymentType paymentType;
    private Integer depositAmount;
    private LocalDate depositPaidAt;
    private LocalDate balanceDueDate;
    private LocalDate paidAt;
    private LocalDate deliveredAt;
}
