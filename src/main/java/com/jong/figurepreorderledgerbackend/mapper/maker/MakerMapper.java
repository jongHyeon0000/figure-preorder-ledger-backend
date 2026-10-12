package com.jong.figurepreorderledgerbackend.mapper.maker;

import com.jong.figurepreorderledgerbackend.vo.maker.MakerResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MakerMapper {

    List<MakerResponse> selectAll(@Param("userId") Long userId);

    MakerResponse selectById(@Param("userId") Long userId, @Param("makerId") Long makerId);

    MakerResponse selectByName(@Param("userId") Long userId, @Param("name") String name);

    int insert(@Param("userId") Long userId, @Param("name") String name);

    int updateName(@Param("userId") Long userId, @Param("makerId") Long makerId, @Param("name") String name);

    int delete(@Param("userId") Long userId, @Param("makerId") Long makerId);

    Long selectDefaultId(@Param("userId") Long userId, @Param("defaultName") String defaultName);

    int countUsage(@Param("userId") Long userId, @Param("makerId") Long makerId);

    int moveUsage(@Param("userId") Long userId, @Param("makerId") Long makerId, @Param("fallbackId") Long fallbackId);
}
