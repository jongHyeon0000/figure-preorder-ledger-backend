package com.jong.figurepreorderledgerbackend.vo.figure;

import com.jong.figurepreorderledgerbackend.common.constant.FigureStatus;
import com.jong.figurepreorderledgerbackend.common.constant.PaymentType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class FigureStatusRequest {

    @NotNull
    private FigureStatus status;

    private PaymentType paymentType;

    @Min(0)
    private Integer depositAmount;

    private LocalDate depositPaidAt;

    private LocalDate balanceDueDate;

    private LocalDate paidAt;

    private LocalDate deliveredAt;
}
