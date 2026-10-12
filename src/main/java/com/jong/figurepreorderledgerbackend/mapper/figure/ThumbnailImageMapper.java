package com.jong.figurepreorderledgerbackend.mapper.figure;

import com.jong.figurepreorderledgerbackend.vo.figure.ThumbnailImageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ThumbnailImageMapper {

    ThumbnailImageVO selectByFigureId(@Param("userId") Long userId, @Param("figureId") Long figureId);

    List<String> selectFilePathsByFigureIds(@Param("userId") Long userId, @Param("figureIds") List<Long> figureIds);

    int insert(@Param("userId") Long userId, @Param("figureId") Long figureId, @Param("filePath") String filePath,
               @Param("originalName") String originalName, @Param("fileSize") Long fileSize);

    int deleteByFigureId(@Param("userId") Long userId, @Param("figureId") Long figureId);
}
