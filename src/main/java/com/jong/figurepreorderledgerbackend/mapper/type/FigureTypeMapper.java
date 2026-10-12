package com.jong.figurepreorderledgerbackend.mapper.type;

import com.jong.figurepreorderledgerbackend.vo.type.FigureTypeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FigureTypeMapper {

    List<FigureTypeVO> selectAll(@Param("userId") Long userId);

    FigureTypeVO selectById(@Param("userId") Long userId, @Param("figureTypeId") Long figureTypeId);

    FigureTypeVO selectByParentAndName(@Param("userId") Long userId, @Param("figureTypeGroupId") Long figureTypeGroupId,
                                        @Param("name") String name);

    List<Long> selectIdsByParent(@Param("userId") Long userId, @Param("figureTypeGroupId") Long figureTypeGroupId);

    int countByParent(@Param("userId") Long userId, @Param("figureTypeGroupId") Long figureTypeGroupId);

    int insert(@Param("userId") Long userId, @Param("figureTypeGroupId") Long figureTypeGroupId, @Param("name") String name, @Param("description") String description);

    int update(@Param("userId") Long userId, @Param("figureTypeId") Long figureTypeId, @Param("name") String name, @Param("description") String description);

    int delete(@Param("userId") Long userId, @Param("figureTypeId") Long figureTypeId);

    Long selectDefaultId(@Param("userId") Long userId, @Param("defaultName") String defaultName);

    int countUsage(@Param("userId") Long userId, @Param("figureTypeId") Long figureTypeId);

    int moveUsage(@Param("userId") Long userId, @Param("figureTypeId") Long figureTypeId, @Param("fallbackId") Long fallbackId);
}
