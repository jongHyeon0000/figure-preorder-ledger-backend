package com.jong.mikumikureservationbackend.mapper.shop;

import com.jong.mikumikureservationbackend.vo.shop.ShopVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShopMapper {

    List<ShopVO> selectAll(@Param("userId") Long userId);

    ShopVO selectById(@Param("userId") Long userId, @Param("shopId") Long shopId);

    ShopVO selectByParentAndName(@Param("userId") Long userId, @Param("shopGroupId") Long shopGroupId,
                                        @Param("name") String name);

    List<Long> selectIdsByParent(@Param("userId") Long userId, @Param("shopGroupId") Long shopGroupId);

    int countByParent(@Param("userId") Long userId, @Param("shopGroupId") Long shopGroupId);

    int insert(@Param("userId") Long userId, @Param("shopGroupId") Long shopGroupId, @Param("name") String name, @Param("description") String description);

    int update(@Param("userId") Long userId, @Param("shopId") Long shopId, @Param("name") String name, @Param("description") String description);

    int delete(@Param("userId") Long userId, @Param("shopId") Long shopId);

    Long selectDefaultId(@Param("userId") Long userId, @Param("defaultName") String defaultName);

    int countUsage(@Param("userId") Long userId, @Param("shopId") Long shopId);

    int moveUsage(@Param("userId") Long userId, @Param("shopId") Long shopId, @Param("fallbackId") Long fallbackId);
}
