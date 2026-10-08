package com.jong.figurepreorderledgerbackend.service.figure;

import com.jong.figurepreorderledgerbackend.service.genre.GenreService;
import com.jong.figurepreorderledgerbackend.service.maker.MakerService;
import com.jong.figurepreorderledgerbackend.service.series.SeriesGroupService;
import com.jong.figurepreorderledgerbackend.service.shop.ShopChannelService;
import com.jong.figurepreorderledgerbackend.service.type.FigureTypeService;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureFormOptionsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/* 등록, 수정 폼의 선택지 5개 축을 분류 서비스들에서 모아 한 번에 돌려준다 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FigureFormService {

    private final ShopChannelService shopChannelService;
    private final FigureTypeService figureTypeService;
    private final SeriesGroupService seriesGroupService;
    private final GenreService genreService;
    private final MakerService makerService;

    public FigureFormOptionsResponse getOptions(Long userId) {
        FigureFormOptionsResponse options = new FigureFormOptionsResponse();
        options.setShops(shopChannelService.getTree(userId));
        options.setTypes(figureTypeService.getTree(userId));
        options.setSeries(seriesGroupService.getTree(userId));
        options.setGenres(genreService.getTree(userId));
        options.setMakers(makerService.getAll(userId));
        return options;
    }
}
