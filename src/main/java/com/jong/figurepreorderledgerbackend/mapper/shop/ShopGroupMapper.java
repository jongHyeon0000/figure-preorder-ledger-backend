package com.jong.figurepreorderledgerbackend.mapper.shop;

import com.jong.figurepreorderledgerbackend.vo.shop.ShopGroupResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShopGroupMapper {

    List<ShopGroupResponse> selectAll(@Param("userId") Long userId);

    ShopGroupResponse selectById(@Param("userId") Long userId, @Param("shopGroupId") Long shopGroupId);

    ShopGroupResponse selectByName(@Param("userId") Long userId, @Param("name") String name);

    int insert(@Param("userId") Long userId, @Param("name") String name);

    int updateName(@Param("userId") Long userId, @Param("shopGroupId") Long shopGroupId, @Param("name") String name);

    int delete(@Param("userId") Long userId, @Param("shopGroupId") Long shopGroupId);

    Long selectDefaultChildId(@Param("userId") Long userId, @Param("defaultName") String defaultName);

    int countUsage(@Param("userId") Long userId, @Param("shopGroupId") Long shopGroupId);

    int moveUsage(@Param("userId") Long userId, @Param("shopGroupId") Long shopGroupId, @Param("fallbackId") Long fallbackId);
}
