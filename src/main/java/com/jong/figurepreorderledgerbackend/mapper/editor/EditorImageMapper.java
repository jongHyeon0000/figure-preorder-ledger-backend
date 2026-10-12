package com.jong.figurepreorderledgerbackend.mapper.editor;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EditorImageMapper {

    int insert(@Param("userId") Long userId, @Param("filePath") String filePath,
               @Param("originalName") String originalName, @Param("fileSize") Long fileSize);

    Long selectLastInsertId();
}
