package com.jong.figurepreorderledgerbackend.mapper.shop;

import com.jong.figurepreorderledgerbackend.vo.shop.ShopResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShopMapper {

    List<ShopResponse> selectAll(@Param("userId") Long userId);

    ShopResponse selectById(@Param("userId") Long userId, @Param("id") Long id);

    ShopResponse selectByParentAndName(@Param("userId") Long userId, @Param("parentId") Long parentId,
                                        @Param("name") String name);

    int countByParent(@Param("userId") Long userId, @Param("parentId") Long parentId);

    int insert(@Param("userId") Long userId, @Param("parentId") Long parentId, @Param("name") String name);

    int updateName(@Param("userId") Long userId, @Param("id") Long id, @Param("name") String name);

    int delete(@Param("userId") Long userId, @Param("id") Long id);

    Long selectDefaultId(@Param("userId") Long userId, @Param("defaultName") String defaultName);

    int countUsage(@Param("userId") Long userId, @Param("id") Long id);

    int moveUsage(@Param("userId") Long userId, @Param("id") Long id, @Param("fallbackId") Long fallbackId);
}
