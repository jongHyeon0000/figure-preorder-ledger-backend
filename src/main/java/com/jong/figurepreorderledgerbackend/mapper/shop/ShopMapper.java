package com.jong.figurepreorderledgerbackend.mapper.shop;

import com.jong.figurepreorderledgerbackend.vo.shop.ShopResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShopMapper {

    List<ShopResponse> selectAll(@Param("userId") Long userId);

    ShopResponse selectById(@Param("userId") Long userId, @Param("shopId") Long shopId);

    ShopResponse selectByParentAndName(@Param("userId") Long userId, @Param("shopGroupId") Long shopGroupId,
                                        @Param("name") String name);

    int countByParent(@Param("userId") Long userId, @Param("shopGroupId") Long shopGroupId);

    int insert(@Param("userId") Long userId, @Param("shopGroupId") Long shopGroupId, @Param("name") String name);

    int updateName(@Param("userId") Long userId, @Param("shopId") Long shopId, @Param("name") String name);

    int delete(@Param("userId") Long userId, @Param("shopId") Long shopId);

    Long selectDefaultId(@Param("userId") Long userId, @Param("defaultName") String defaultName);

    int countUsage(@Param("userId") Long userId, @Param("shopId") Long shopId);

    int moveUsage(@Param("userId") Long userId, @Param("shopId") Long shopId, @Param("fallbackId") Long fallbackId);
}
