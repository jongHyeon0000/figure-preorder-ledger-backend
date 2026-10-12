package com.jong.figurepreorderledgerbackend.mapper.series;

import com.jong.figurepreorderledgerbackend.vo.series.SeriesGroupResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SeriesGroupMapper {

    List<SeriesGroupResponse> selectAll(@Param("userId") Long userId);

    SeriesGroupResponse selectById(@Param("userId") Long userId, @Param("seriesGroupId") Long seriesGroupId);

    SeriesGroupResponse selectByName(@Param("userId") Long userId, @Param("name") String name);

    int insert(@Param("userId") Long userId, @Param("name") String name);

    int updateName(@Param("userId") Long userId, @Param("seriesGroupId") Long seriesGroupId, @Param("name") String name);

    int delete(@Param("userId") Long userId, @Param("seriesGroupId") Long seriesGroupId);

    Long selectDefaultChildId(@Param("userId") Long userId, @Param("defaultName") String defaultName);

    int countUsage(@Param("userId") Long userId, @Param("seriesGroupId") Long seriesGroupId);

    int moveInsertFallback(@Param("userId") Long userId, @Param("seriesGroupId") Long seriesGroupId, @Param("fallbackId") Long fallbackId);

    int moveDeleteLinks(@Param("userId") Long userId, @Param("seriesGroupId") Long seriesGroupId);
}
