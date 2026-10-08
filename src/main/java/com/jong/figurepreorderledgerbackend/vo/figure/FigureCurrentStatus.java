package com.jong.figurepreorderledgerbackend.vo.figure;

import com.jong.figurepreorderledgerbackend.common.constant.FigureStatus;
import com.jong.figurepreorderledgerbackend.common.constant.PaymentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* 상태 전이 판단에 쓰는 현재 상태 (매퍼 조회 결과) */
@Getter
@Setter
@NoArgsConstructor
public class FigureCurrentStatus {

    private Long id;
    private FigureStatus status;
    private PaymentType paymentType;
}
