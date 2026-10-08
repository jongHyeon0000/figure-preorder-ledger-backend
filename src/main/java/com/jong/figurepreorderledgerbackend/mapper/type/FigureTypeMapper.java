package com.jong.figurepreorderledgerbackend.mapper.type;

import com.jong.figurepreorderledgerbackend.vo.type.FigureTypeResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FigureTypeMapper {

    List<FigureTypeResponse> selectAll(@Param("userId") Long userId);

    FigureTypeResponse selectById(@Param("userId") Long userId, @Param("id") Long id);

    FigureTypeResponse selectByName(@Param("userId") Long userId, @Param("name") String name);

    int insert(@Param("userId") Long userId, @Param("name") String name);

    int updateName(@Param("userId") Long userId, @Param("id") Long id, @Param("name") String name);

    int delete(@Param("userId") Long userId, @Param("id") Long id);

    Long selectDefaultChildId(@Param("userId") Long userId, @Param("defaultName") String defaultName);

    int countUsage(@Param("userId") Long userId, @Param("id") Long id);

    int moveUsage(@Param("userId") Long userId, @Param("id") Long id, @Param("fallbackId") Long fallbackId);
}
