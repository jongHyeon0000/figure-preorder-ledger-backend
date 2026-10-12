package com.jong.figurepreorderledgerbackend.mapper.series;

import com.jong.figurepreorderledgerbackend.vo.series.SeriesVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SeriesMapper {

    List<SeriesVO> selectAll(@Param("userId") Long userId);

    SeriesVO selectById(@Param("userId") Long userId, @Param("seriesId") Long seriesId);

    SeriesVO selectByParentAndName(@Param("userId") Long userId, @Param("seriesGroupId") Long seriesGroupId,
                                        @Param("name") String name);

    List<Long> selectIdsByParent(@Param("userId") Long userId, @Param("seriesGroupId") Long seriesGroupId);

    int countByParent(@Param("userId") Long userId, @Param("seriesGroupId") Long seriesGroupId);

    int insert(@Param("userId") Long userId, @Param("seriesGroupId") Long seriesGroupId, @Param("name") String name, @Param("description") String description);

    int update(@Param("userId") Long userId, @Param("seriesId") Long seriesId, @Param("name") String name, @Param("description") String description);

    int delete(@Param("userId") Long userId, @Param("seriesId") Long seriesId);

    Long selectDefaultId(@Param("userId") Long userId, @Param("defaultName") String defaultName);

    int countUsage(@Param("userId") Long userId, @Param("seriesId") Long seriesId);

    int moveInsertFallback(@Param("userId") Long userId, @Param("seriesId") Long seriesId, @Param("fallbackId") Long fallbackId);

    int moveDeleteLinks(@Param("userId") Long userId, @Param("seriesId") Long seriesId);
}
