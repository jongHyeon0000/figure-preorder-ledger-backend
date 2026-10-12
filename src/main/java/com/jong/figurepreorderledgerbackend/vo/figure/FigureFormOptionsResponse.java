package com.jong.figurepreorderledgerbackend.vo.figure;

import com.jong.figurepreorderledgerbackend.vo.genre.GenreCharacterGroupResponse;
import com.jong.figurepreorderledgerbackend.vo.maker.MakerResponse;
import com.jong.figurepreorderledgerbackend.vo.series.response.SeriesGroupResponse;
import com.jong.figurepreorderledgerbackend.vo.shop.ShopGroupResponse;
import com.jong.figurepreorderledgerbackend.vo.type.FigureTypeGroupResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FigureFormOptionsResponse {

    private List<ShopGroupResponse> shops;
    private List<FigureTypeGroupResponse> types;
    private List<SeriesGroupResponse> series;
    private List<GenreCharacterGroupResponse> genres;
    private List<MakerResponse> makers;
}
