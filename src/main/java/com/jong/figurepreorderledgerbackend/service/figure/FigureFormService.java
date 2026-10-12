package com.jong.figurepreorderledgerbackend.service.figure;

import com.jong.figurepreorderledgerbackend.vo.figure.FigureFormOptionsResponse;

/* 등록, 수정 폼의 선택지 5개 축을 분류 서비스들에서 모아 한 번에 돌려준다 */
public interface FigureFormService {

    FigureFormOptionsResponse getOptions(Long userId);
}
