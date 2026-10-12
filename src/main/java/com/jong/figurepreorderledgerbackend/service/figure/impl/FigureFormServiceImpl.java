package com.jong.figurepreorderledgerbackend.service.figure.impl;

import com.jong.figurepreorderledgerbackend.service.figure.FigureFormService;
import com.jong.figurepreorderledgerbackend.service.genre.GenreCharacterGroupService;
import com.jong.figurepreorderledgerbackend.service.maker.MakerService;
import com.jong.figurepreorderledgerbackend.service.series.SeriesGroupService;
import com.jong.figurepreorderledgerbackend.service.shop.ShopGroupService;
import com.jong.figurepreorderledgerbackend.service.type.FigureTypeGroupService;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureFormOptionsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FigureFormServiceImpl implements FigureFormService {

    private final ShopGroupService shopGroupService;
    private final FigureTypeGroupService figureTypeGroupService;
    private final SeriesGroupService seriesGroupService;
    private final GenreCharacterGroupService genreCharacterGroupService;
    private final MakerService makerService;

    @Override
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
