package com.jong.figurepreorderledgerbackend.service.figure;

import com.jong.figurepreorderledgerbackend.vo.figure.FigureCreateRequest;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureDetailResponse;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureListResponse;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureUpdateRequest;

import java.util.List;

public interface FigureService {

    List<FigureListResponse> getList(Long userId);

    FigureDetailResponse getDetail(Long userId, Long figureId);

    /* 상태 전이 등 다른 서비스가 변경된 상품 한 건을 목록 항목 모양으로 돌려줄 때도 쓴다 */
    FigureListResponse getListItem(Long userId, Long figureId);

    FigureListResponse create(Long userId, FigureCreateRequest request);

    /* 상태와 결제 방식은 바꾸지 않는다. 시리즈와 캐릭터 연결은 지우고 다시 넣는다 */
    FigureListResponse update(Long userId, Long figureId, FigureUpdateRequest request);

    void delete(Long userId, Long figureId);

    /* 하나라도 없는 상품이면 전체를 되돌린다 (예외로 트랜잭션 롤백) */
    void deleteAll(Long userId, List<Long> figureIds);
}
