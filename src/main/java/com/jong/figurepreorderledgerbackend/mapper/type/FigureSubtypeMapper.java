package com.jong.figurepreorderledgerbackend.mapper.type;

import com.jong.figurepreorderledgerbackend.vo.type.FigureSubtypeResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FigureSubtypeMapper {

    List<FigureSubtypeResponse> selectAll(@Param("userId") Long userId);

    FigureSubtypeResponse selectById(@Param("userId") Long userId, @Param("id") Long id);

    FigureSubtypeResponse selectByParentAndName(@Param("userId") Long userId, @Param("parentId") Long parentId,
                                        @Param("name") String name);

    int countByParent(@Param("userId") Long userId, @Param("parentId") Long parentId);

    int insert(@Param("userId") Long userId, @Param("parentId") Long parentId, @Param("name") String name);

    int updateName(@Param("userId") Long userId, @Param("id") Long id, @Param("name") String name);

    int delete(@Param("userId") Long userId, @Param("id") Long id);

    Long selectDefaultId(@Param("userId") Long userId, @Param("defaultName") String defaultName);

    int countUsage(@Param("userId") Long userId, @Param("id") Long id);

    int moveUsage(@Param("userId") Long userId, @Param("id") Long id, @Param("fallbackId") Long fallbackId);
}
