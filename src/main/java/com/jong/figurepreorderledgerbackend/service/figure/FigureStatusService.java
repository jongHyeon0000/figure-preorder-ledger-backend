package com.jong.figurepreorderledgerbackend.service.figure;

import com.jong.figurepreorderledgerbackend.vo.figure.FigureResponseVO;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureStatusRequestVO;

import java.util.List;

public interface FigureStatusService {

    /* 상품 한 건의 상태를 전이하고 변경된 상품을 목록 항목 모양으로 돌려준다 */
    FigureResponseVO change(Long userId, Long figureId, FigureStatusRequestVO request);

    /*
     * 여러 상품을 같은 상태로 전이한다. 전부 검사한 뒤에만 반영하고,
     * 하나라도 안 되면 아무것도 바꾸지 않고 문제가 된 id를 메시지에 담아 400으로 응답한다.
     */
    List<Long> changeAll(Long userId, FigureStatusRequestVO request);
}
