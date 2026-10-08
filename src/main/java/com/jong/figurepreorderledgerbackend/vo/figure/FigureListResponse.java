package com.jong.figurepreorderledgerbackend.vo.figure;

import com.jong.figurepreorderledgerbackend.common.constant.FigureStatus;
import com.jong.figurepreorderledgerbackend.common.constant.PaymentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FigureListResponse {

    private Long id;
    private FigureStatus status;
    private PaymentType paymentType;
    private String name;
    private LocalDate deliveryMonth;
    private LocalDate reservationDeadline;
    private Integer price;
    private Integer quantity;
    private Integer depositAmount;
    private LocalDate balanceDueDate;
    private LocalDate depositPaidAt;
    private LocalDate paidAt;
    private LocalDate deliveredAt;
    private String purchaseLink;
    private String thumbnailUrl;
    private FigureSubtypeInfo subtype;
    private FigureShopInfo shop;
    private FigureMakerInfo maker;
    private List<FigureSeriesInfo> series;
    private List<FigureCharacterInfo> characters;
}
