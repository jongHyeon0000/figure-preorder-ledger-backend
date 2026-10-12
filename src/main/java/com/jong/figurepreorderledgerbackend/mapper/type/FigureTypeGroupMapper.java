package com.jong.figurepreorderledgerbackend.mapper.type;

import com.jong.figurepreorderledgerbackend.vo.type.FigureTypeGroupResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FigureTypeGroupMapper {

    List<FigureTypeGroupResponse> selectAll(@Param("userId") Long userId);

    FigureTypeGroupResponse selectById(@Param("userId") Long userId, @Param("figureTypeGroupId") Long figureTypeGroupId);

    FigureTypeGroupResponse selectByName(@Param("userId") Long userId, @Param("name") String name);

    int insert(@Param("userId") Long userId, @Param("name") String name);

    int updateName(@Param("userId") Long userId, @Param("figureTypeGroupId") Long figureTypeGroupId, @Param("name") String name);

    int delete(@Param("userId") Long userId, @Param("figureTypeGroupId") Long figureTypeGroupId);

    Long selectDefaultChildId(@Param("userId") Long userId, @Param("defaultName") String defaultName);

    int countUsage(@Param("userId") Long userId, @Param("figureTypeGroupId") Long figureTypeGroupId);

    int moveUsage(@Param("userId") Long userId, @Param("figureTypeGroupId") Long figureTypeGroupId, @Param("fallbackId") Long fallbackId);
}
