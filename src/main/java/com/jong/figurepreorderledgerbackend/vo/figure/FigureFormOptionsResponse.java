package com.jong.figurepreorderledgerbackend.vo.figure;

import com.jong.figurepreorderledgerbackend.vo.genre.GenreCharacterGroupVO;
import com.jong.figurepreorderledgerbackend.vo.maker.MakerVO;
import com.jong.figurepreorderledgerbackend.vo.series.SeriesGroupVO;
import com.jong.figurepreorderledgerbackend.vo.shop.ShopGroupVO;
import com.jong.figurepreorderledgerbackend.vo.type.FigureTypeGroupVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FigureFormOptionsResponse {

    private List<ShopGroupVO> shops;
    private List<FigureTypeGroupVO> types;
    private List<SeriesGroupVO> series;
    private List<GenreCharacterGroupVO> genres;
    private List<MakerVO> makers;
}
