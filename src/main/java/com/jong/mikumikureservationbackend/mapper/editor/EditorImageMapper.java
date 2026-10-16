package com.jong.mikumikureservationbackend.mapper.editor;

import com.jong.mikumikureservationbackend.vo.editor.EditorImageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EditorImageMapper {

    int insert(@Param("userId") Long userId, @Param("filePath") String filePath,
               @Param("originalName") String originalName, @Param("fileSize") Long fileSize);

    Long selectLastInsertId();

    List<EditorImageVO> selectOrphans(@Param("retentionDays") int retentionDays);

    int deleteOrphanById(@Param("editorImageId") Long editorImageId);
}
