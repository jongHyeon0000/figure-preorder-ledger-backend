package com.jong.figurepreorderledgerbackend.vo.figure;

import com.jong.figurepreorderledgerbackend.common.constant.FigureStatus;
import com.jong.figurepreorderledgerbackend.common.constant.PaymentType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FigureBulkStatusRequest {

    @NotEmpty
    private List<@NotNull Long> ids;

    @NotNull
    private FigureStatus status;

    private PaymentType paymentType;

    private LocalDate paidAt;

    private LocalDate deliveredAt;
}
