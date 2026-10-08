package com.jong.figurepreorderledgerbackend.mapper.genre;

import com.jong.figurepreorderledgerbackend.vo.genre.GenreResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GenreMapper {

    List<GenreResponse> selectAll(@Param("userId") Long userId);

    GenreResponse selectById(@Param("userId") Long userId, @Param("id") Long id);

    GenreResponse selectByName(@Param("userId") Long userId, @Param("name") String name);

    int insert(@Param("userId") Long userId, @Param("name") String name);

    int updateName(@Param("userId") Long userId, @Param("id") Long id, @Param("name") String name);

    int delete(@Param("userId") Long userId, @Param("id") Long id);

    Long selectDefaultChildId(@Param("userId") Long userId, @Param("defaultName") String defaultName);

    int countUsage(@Param("userId") Long userId, @Param("id") Long id);

    int moveInsertFallback(@Param("userId") Long userId, @Param("id") Long id, @Param("fallbackId") Long fallbackId);

    int moveDeleteLinks(@Param("userId") Long userId, @Param("id") Long id);
}
