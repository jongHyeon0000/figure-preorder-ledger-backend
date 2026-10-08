package com.jong.figurepreorderledgerbackend.vo.figure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FigureSeriesInfo {

    /* 상품별로 묶을 때만 쓰고 응답에는 내보내지 않는다 */
    @JsonIgnore
    private Long figureId;
    private Long id;
    private String name;
    private Long groupId;
    private String groupName;
}
