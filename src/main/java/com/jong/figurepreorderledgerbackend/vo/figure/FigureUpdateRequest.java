package com.jong.figurepreorderledgerbackend.vo.figure;

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

@Getter
@Setter
@NoArgsConstructor
public class FigureUpdateRequest {

    @NotBlank
    @Size(max = 255)
    private String name;

    private LocalDate deliveryMonth;

    private LocalDate reservationDeadline;

    @NotNull
    @Min(0)
    private Integer price;

    @NotNull
    @Min(1)
    private Integer quantity;

    @Size(max = 2000)
    private String purchaseLink;

    @Size(max = 2000)
    private String thumbnailUrl;

    private String description;

    @NotNull
    private Long subtypeId;

    @NotNull
    private Long shopId;

    @NotNull
    private Long makerId;

    @NotEmpty
    private List<@NotNull Long> seriesIds;

    @NotEmpty
    private List<@NotNull Long> characterIds;
}
