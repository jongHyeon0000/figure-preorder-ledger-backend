package com.jong.mikumikureservationbackend.vo.figure;

import com.jong.mikumikureservationbackend.vo.genre.GenreCharacterVO;
import com.jong.mikumikureservationbackend.vo.maker.MakerVO;
import com.jong.mikumikureservationbackend.vo.series.SeriesVO;
import com.jong.mikumikureservationbackend.vo.shop.ShopVO;
import com.jong.mikumikureservationbackend.vo.type.FigureTypeVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 상품 응답 (목록, 상세, 등록, 수정, 상태 변경 결과)
 * FigureVO의 컬럼에 조인한 테이블의 값을 더했다. [테이블명 joined] 표시가 붙은 필드는 컬럼이 아니다.
 */
@Schema(description = "상품 응답 (목록, 상세, 등록, 수정, 상태 전이 결과). FigureVO의 모든 필드에 조인한 값이 더해진다")
@Getter
@Setter
@NoArgsConstructor
public class FigureResponseVO extends FigureVO {

    /** [figure_type joined] 형태 (figure_type_id로 조회, 상위 형태명 포함) */
    @Schema(description = "형태 (상위 형태명 포함)")
    private FigureTypeVO figureType;

    /** [shop joined] 판매처 (shop_id로 조회, 상위 채널명 포함) */
    @Schema(description = "판매처 (상위 채널명 포함)")
    private ShopVO shop;

    /** [maker joined] 제조사 (maker_id로 조회) */
    @Schema(description = "제조사")
    private MakerVO maker;

    /** [thumbnail_image joined] 썸네일 이미지 주소 (/uploads/ + file_path, 없으면 null) */
    @Schema(description = "썸네일 이미지 주소 (/uploads/...). 썸네일이 없으면 null", example = "/uploads/1/thumbnail-image/figure/2026/10/uuid.png", nullable = true)
    private String thumbnailUrl;

    /** [series joined] 시리즈 목록 (figure_series_map, 상위 시리즈명 포함) */
    @Schema(description = "시리즈 목록 (상위 시리즈 그룹명 포함)")
    private List<SeriesVO> series;

    /** [genre_character joined] 캐릭터 목록 (figure_character_map, 상위 장르명 포함) */
    @Schema(description = "캐릭터 목록 (상위 장르명 포함)")
    private List<GenreCharacterVO> characters;
}
