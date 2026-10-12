package com.jong.figurepreorderledgerbackend.mapper.genre;

import com.jong.figurepreorderledgerbackend.vo.genre.GenreCharacterGroupVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GenreCharacterGroupMapper {

    List<GenreCharacterGroupVO> selectAll(@Param("userId") Long userId);

    GenreCharacterGroupVO selectById(@Param("userId") Long userId, @Param("genreCharacterGroupId") Long genreCharacterGroupId);

    GenreCharacterGroupVO selectByName(@Param("userId") Long userId, @Param("name") String name);

    int insert(@Param("userId") Long userId, @Param("name") String name, @Param("description") String description);

    int update(@Param("userId") Long userId, @Param("genreCharacterGroupId") Long genreCharacterGroupId, @Param("name") String name, @Param("description") String description);

    int delete(@Param("userId") Long userId, @Param("genreCharacterGroupId") Long genreCharacterGroupId);

    Long selectDefaultChildId(@Param("userId") Long userId, @Param("defaultName") String defaultName);

    int countUsage(@Param("userId") Long userId, @Param("genreCharacterGroupId") Long genreCharacterGroupId);

    int moveInsertFallback(@Param("userId") Long userId, @Param("genreCharacterGroupId") Long genreCharacterGroupId, @Param("fallbackId") Long fallbackId);

    int moveDeleteLinks(@Param("userId") Long userId, @Param("genreCharacterGroupId") Long genreCharacterGroupId);
}
