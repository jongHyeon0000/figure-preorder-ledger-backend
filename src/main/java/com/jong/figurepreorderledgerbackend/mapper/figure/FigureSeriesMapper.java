package com.jong.figurepreorderledgerbackend.mapper.figure;

import com.jong.figurepreorderledgerbackend.vo.figure.FigureSeriesInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FigureSeriesMapper {

    List<FigureSeriesInfo> selectByFigureIds(@Param("userId") Long userId, @Param("figureIds") List<Long> figureIds);

    int insertAll(@Param("userId") Long userId, @Param("figureId") Long figureId,
                  @Param("seriesIds") List<Long> seriesIds);

    int deleteByFigure(@Param("userId") Long userId, @Param("figureId") Long figureId);
}
