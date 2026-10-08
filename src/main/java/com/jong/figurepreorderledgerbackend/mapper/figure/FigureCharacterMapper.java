package com.jong.figurepreorderledgerbackend.mapper.figure;

import com.jong.figurepreorderledgerbackend.vo.figure.FigureCharacterInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FigureCharacterMapper {

    List<FigureCharacterInfo> selectByFigureIds(@Param("userId") Long userId, @Param("figureIds") List<Long> figureIds);

    int insertAll(@Param("userId") Long userId, @Param("figureId") Long figureId,
                  @Param("characterIds") List<Long> characterIds);

    int deleteByFigure(@Param("userId") Long userId, @Param("figureId") Long figureId);
}
