package com.jong.figurepreorderledgerbackend.service.type;

import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import com.jong.figurepreorderledgerbackend.vo.type.FigureTypeGroupVO;

import java.util.List;

public interface FigureTypeGroupService {

    List<FigureTypeGroupVO> getTree(Long userId);

    /* 상위를 만들면 기본 하위(기타)도 같이 만든다 */
    FigureTypeGroupVO create(Long userId, FigureTypeGroupVO vo);

    void update(Long userId, Long figureTypeGroupId, FigureTypeGroupVO vo);

    /* 쓰던 상품을 기본 항목(기타 > 기타)으로 옮긴 뒤 삭제한다. 하위는 DB에서 함께 삭제된다 */
    void delete(Long userId, Long figureTypeGroupId);

    UsageResponse getUsage(Long userId, Long figureTypeGroupId);
}
