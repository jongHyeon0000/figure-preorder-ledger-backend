package com.jong.figurepreorderledgerbackend.vo.figure;

import com.jong.figurepreorderledgerbackend.vo.genre.GenreCharacterGroupVO;
import com.jong.figurepreorderledgerbackend.vo.maker.MakerVO;
import com.jong.figurepreorderledgerbackend.vo.series.SeriesGroupVO;
import com.jong.figurepreorderledgerbackend.vo.shop.ShopGroupVO;
import com.jong.figurepreorderledgerbackend.vo.type.FigureTypeGroupVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "상품 등록, 수정 폼의 선택지 묶음 (5개 축)")
@Getter
@Setter
@NoArgsConstructor
public class FigureFormOptionsResponse {

    @Schema(description = "판매처 채널 > 스토어")
    private List<ShopGroupVO> shops;
    @Schema(description = "형태 > 하위 형태")
    private List<FigureTypeGroupVO> types;
    @Schema(description = "시리즈 그룹 > 시리즈")
    private List<SeriesGroupVO> series;
    @Schema(description = "장르 > 캐릭터")
    private List<GenreCharacterGroupVO> genres;
    @Schema(description = "제조사 (상하위 없이 평면)")
    private List<MakerVO> makers;
}
