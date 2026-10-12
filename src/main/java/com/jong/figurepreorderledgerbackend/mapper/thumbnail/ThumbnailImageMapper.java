package com.jong.figurepreorderledgerbackend.mapper.thumbnail;

import com.jong.figurepreorderledgerbackend.common.constant.ThumbnailTarget;
import com.jong.figurepreorderledgerbackend.vo.thumbnail.ThumbnailImageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ThumbnailImageMapper {

    ThumbnailImageVO selectByTarget(@Param("userId") Long userId, @Param("targetType") ThumbnailTarget targetType,
                                    @Param("targetId") Long targetId);

    List<String> selectFilePathsByTargets(@Param("userId") Long userId, @Param("targetType") ThumbnailTarget targetType,
                                          @Param("targetIds") List<Long> targetIds);

    int insert(@Param("userId") Long userId, @Param("targetType") ThumbnailTarget targetType,
               @Param("targetId") Long targetId, @Param("filePath") String filePath,
               @Param("originalName") String originalName, @Param("fileSize") Long fileSize);

    int deleteByTarget(@Param("userId") Long userId, @Param("targetType") ThumbnailTarget targetType,
                       @Param("targetId") Long targetId);

    int deleteByTargets(@Param("userId") Long userId, @Param("targetType") ThumbnailTarget targetType,
                        @Param("targetIds") List<Long> targetIds);
}
