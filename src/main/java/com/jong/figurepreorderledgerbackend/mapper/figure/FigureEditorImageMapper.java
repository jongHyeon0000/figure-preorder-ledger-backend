package com.jong.figurepreorderledgerbackend.mapper.figure;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FigureEditorImageMapper {

    int insertAllByFilePaths(@Param("userId") Long userId, @Param("figureId") Long figureId,
                             @Param("filePaths") List<String> filePaths);

    int deleteByFigure(@Param("userId") Long userId, @Param("figureId") Long figureId);
}
