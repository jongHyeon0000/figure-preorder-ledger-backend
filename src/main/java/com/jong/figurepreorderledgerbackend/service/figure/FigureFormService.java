package com.jong.figurepreorderledgerbackend.service.figure;

import com.jong.figurepreorderledgerbackend.service.genre.GenreCharacterGroupService;
import com.jong.figurepreorderledgerbackend.service.maker.MakerService;
import com.jong.figurepreorderledgerbackend.service.series.SeriesGroupService;
import com.jong.figurepreorderledgerbackend.service.shop.ShopGroupService;
import com.jong.figurepreorderledgerbackend.service.type.FigureTypeGroupService;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureFormOptionsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/* 등록, 수정 폼의 선택지 5개 축을 분류 서비스들에서 모아 한 번에 돌려준다 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FigureFormService {

    private final ShopGroupService shopGroupService;
    private final FigureTypeGroupService figureTypeGroupService;
    private final SeriesGroupService seriesGroupService;
    private final GenreCharacterGroupService genreCharacterGroupService;
    private final MakerService makerService;

    public FigureFormOptionsResponse getOptions(Long userId) {
        FigureFormOptionsResponse options = new FigureFormOptionsResponse();
        options.setShops(shopGroupService.getTree(userId));
        options.setTypes(figureTypeGroupService.getTree(userId));
        options.setSeries(seriesGroupService.getTree(userId));
        options.setGenres(genreCharacterGroupService.getTree(userId));
        options.setMakers(makerService.getAll(userId));
        return options;
    }
}
