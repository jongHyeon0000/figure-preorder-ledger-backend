package com.jong.figurepreorderledgerbackend.mapper.series;

import com.jong.figurepreorderledgerbackend.vo.series.SeriesResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SeriesMapper {

    List<SeriesResponse> selectAll(@Param("userId") Long userId);

    SeriesResponse selectById(@Param("userId") Long userId, @Param("id") Long id);

    SeriesResponse selectByParentAndName(@Param("userId") Long userId, @Param("parentId") Long parentId,
                                        @Param("name") String name);

    int countByParent(@Param("userId") Long userId, @Param("parentId") Long parentId);

    int insert(@Param("userId") Long userId, @Param("parentId") Long parentId, @Param("name") String name);

    int updateName(@Param("userId") Long userId, @Param("id") Long id, @Param("name") String name);

    int delete(@Param("userId") Long userId, @Param("id") Long id);

    Long selectDefaultId(@Param("userId") Long userId, @Param("defaultName") String defaultName);

    int countUsage(@Param("userId") Long userId, @Param("id") Long id);

    int moveInsertFallback(@Param("userId") Long userId, @Param("id") Long id, @Param("fallbackId") Long fallbackId);

    int moveDeleteLinks(@Param("userId") Long userId, @Param("id") Long id);
}
