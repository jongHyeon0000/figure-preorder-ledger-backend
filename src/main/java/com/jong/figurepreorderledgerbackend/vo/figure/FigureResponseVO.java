package com.jong.figurepreorderledgerbackend.vo.figure;

import com.jong.figurepreorderledgerbackend.vo.genre.GenreCharacterVO;
import com.jong.figurepreorderledgerbackend.vo.maker.MakerVO;
import com.jong.figurepreorderledgerbackend.vo.series.SeriesVO;
import com.jong.figurepreorderledgerbackend.vo.shop.ShopVO;
import com.jong.figurepreorderledgerbackend.vo.type.FigureTypeVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 상품 응답 (목록, 상세, 등록, 수정, 상태 변경 결과)
 * FigureVO의 컬럼에 조인한 테이블의 값을 더했다. [테이블명 joined] 표시가 붙은 필드는 컬럼이 아니다.
 */
@Getter
@Setter
@NoArgsConstructor
public class FigureResponseVO extends FigureVO {

    /** [figure_type joined] 형태 (figure_type_id로 조회, 상위 형태명 포함) */
    private FigureTypeVO figureType;

    /** [shop joined] 판매처 (shop_id로 조회, 상위 채널명 포함) */
    private ShopVO shop;

    /** [maker joined] 제조사 (maker_id로 조회) */
    private MakerVO maker;

    /** [thumbnail_image joined] 썸네일 이미지 주소 (/uploads/ + file_path, 없으면 null) */
    private String thumbnailUrl;

    /** [series joined] 시리즈 목록 (figure_series_map, 상위 시리즈명 포함) */
    private List<SeriesVO> series;

    /** [genre_character joined] 캐릭터 목록 (figure_character_map, 상위 장르명 포함) */
    private List<GenreCharacterVO> characters;
}
