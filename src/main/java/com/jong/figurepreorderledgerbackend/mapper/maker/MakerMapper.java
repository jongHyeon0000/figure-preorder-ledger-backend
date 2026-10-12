package com.jong.figurepreorderledgerbackend.mapper.maker;

import com.jong.figurepreorderledgerbackend.vo.maker.MakerVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MakerMapper {

    List<MakerVO> selectAll(@Param("userId") Long userId);

    MakerVO selectById(@Param("userId") Long userId, @Param("makerId") Long makerId);

    MakerVO selectByName(@Param("userId") Long userId, @Param("name") String name);

    int insert(@Param("userId") Long userId, @Param("name") String name);

    int updateName(@Param("userId") Long userId, @Param("makerId") Long makerId, @Param("name") String name);

    int delete(@Param("userId") Long userId, @Param("makerId") Long makerId);

    Long selectDefaultId(@Param("userId") Long userId, @Param("defaultName") String defaultName);

    int countUsage(@Param("userId") Long userId, @Param("makerId") Long makerId);

    int moveUsage(@Param("userId") Long userId, @Param("makerId") Long makerId, @Param("fallbackId") Long fallbackId);
}
