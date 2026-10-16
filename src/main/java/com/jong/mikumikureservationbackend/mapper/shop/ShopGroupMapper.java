package com.jong.mikumikureservationbackend.mapper.shop;

import com.jong.mikumikureservationbackend.vo.shop.ShopGroupVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShopGroupMapper {

    List<ShopGroupVO> selectAll(@Param("userId") Long userId);

    ShopGroupVO selectById(@Param("userId") Long userId, @Param("shopGroupId") Long shopGroupId);

    ShopGroupVO selectByName(@Param("userId") Long userId, @Param("name") String name);

    int insert(@Param("userId") Long userId, @Param("name") String name, @Param("description") String description);

    int update(@Param("userId") Long userId, @Param("shopGroupId") Long shopGroupId, @Param("name") String name, @Param("description") String description);

    int delete(@Param("userId") Long userId, @Param("shopGroupId") Long shopGroupId);

    Long selectDefaultChildId(@Param("userId") Long userId, @Param("defaultName") String defaultName);

    int countUsage(@Param("userId") Long userId, @Param("shopGroupId") Long shopGroupId);

    int moveUsage(@Param("userId") Long userId, @Param("shopGroupId") Long shopGroupId, @Param("fallbackId") Long fallbackId);
}
