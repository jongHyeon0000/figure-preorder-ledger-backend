package com.jong.figurepreorderledgerbackend.mapper.genre;

import com.jong.figurepreorderledgerbackend.vo.genre.GenreCharacterVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GenreCharacterMapper {

    List<GenreCharacterVO> selectAll(@Param("userId") Long userId);

    GenreCharacterVO selectById(@Param("userId") Long userId, @Param("genreCharacterId") Long genreCharacterId);

    GenreCharacterVO selectByParentAndName(@Param("userId") Long userId, @Param("genreCharacterGroupId") Long genreCharacterGroupId,
                                        @Param("name") String name);

    int countByParent(@Param("userId") Long userId, @Param("genreCharacterGroupId") Long genreCharacterGroupId);

    int insert(@Param("userId") Long userId, @Param("genreCharacterGroupId") Long genreCharacterGroupId, @Param("name") String name);

    int updateName(@Param("userId") Long userId, @Param("genreCharacterId") Long genreCharacterId, @Param("name") String name);

    int delete(@Param("userId") Long userId, @Param("genreCharacterId") Long genreCharacterId);

    Long selectDefaultId(@Param("userId") Long userId, @Param("defaultName") String defaultName);

    int countUsage(@Param("userId") Long userId, @Param("genreCharacterId") Long genreCharacterId);

    int moveInsertFallback(@Param("userId") Long userId, @Param("genreCharacterId") Long genreCharacterId, @Param("fallbackId") Long fallbackId);

    int moveDeleteLinks(@Param("userId") Long userId, @Param("genreCharacterId") Long genreCharacterId);
}
