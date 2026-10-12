package com.jong.figurepreorderledgerbackend.mapper.genre;

import com.jong.figurepreorderledgerbackend.vo.genre.GenreCharacterGroupResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GenreCharacterGroupMapper {

    List<GenreCharacterGroupResponse> selectAll(@Param("userId") Long userId);

    GenreCharacterGroupResponse selectById(@Param("userId") Long userId, @Param("genreCharacterGroupId") Long genreCharacterGroupId);

    GenreCharacterGroupResponse selectByName(@Param("userId") Long userId, @Param("name") String name);

    int insert(@Param("userId") Long userId, @Param("name") String name);

    int updateName(@Param("userId") Long userId, @Param("genreCharacterGroupId") Long genreCharacterGroupId, @Param("name") String name);

    int delete(@Param("userId") Long userId, @Param("genreCharacterGroupId") Long genreCharacterGroupId);

    Long selectDefaultChildId(@Param("userId") Long userId, @Param("defaultName") String defaultName);

    int countUsage(@Param("userId") Long userId, @Param("genreCharacterGroupId") Long genreCharacterGroupId);

    int moveInsertFallback(@Param("userId") Long userId, @Param("genreCharacterGroupId") Long genreCharacterGroupId, @Param("fallbackId") Long fallbackId);

    int moveDeleteLinks(@Param("userId") Long userId, @Param("genreCharacterGroupId") Long genreCharacterGroupId);
}
