package com.jong.mikumikureservationbackend.mapper.figure;

import com.jong.mikumikureservationbackend.vo.genre.GenreCharacterVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FigureCharacterMapper {

    List<GenreCharacterVO> selectByFigureIds(@Param("userId") Long userId, @Param("figureIds") List<Long> figureIds);

    int insertAll(@Param("userId") Long userId, @Param("figureId") Long figureId,
                  @Param("genreCharacterIds") List<Long> genreCharacterIds);

    int deleteByFigure(@Param("userId") Long userId, @Param("figureId") Long figureId);
}
