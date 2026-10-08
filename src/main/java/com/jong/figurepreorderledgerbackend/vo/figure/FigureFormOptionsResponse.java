package com.jong.figurepreorderledgerbackend.vo.figure;

import com.jong.figurepreorderledgerbackend.vo.genre.GenreResponse;
import com.jong.figurepreorderledgerbackend.vo.maker.MakerResponse;
import com.jong.figurepreorderledgerbackend.vo.series.SeriesGroupResponse;
import com.jong.figurepreorderledgerbackend.vo.shop.ShopChannelResponse;
import com.jong.figurepreorderledgerbackend.vo.type.FigureTypeResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FigureFormOptionsResponse {

    private List<ShopChannelResponse> shops;
    private List<FigureTypeResponse> types;
    private List<SeriesGroupResponse> series;
    private List<GenreResponse> genres;
    private List<MakerResponse> makers;
}
