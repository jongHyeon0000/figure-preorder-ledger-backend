package com.jong.figurepreorderledgerbackend.mapper.shop;

import com.jong.figurepreorderledgerbackend.vo.shop.ShopChannelResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShopChannelMapper {

    List<ShopChannelResponse> selectAll(@Param("userId") Long userId);

    ShopChannelResponse selectById(@Param("userId") Long userId, @Param("id") Long id);

    ShopChannelResponse selectByName(@Param("userId") Long userId, @Param("name") String name);

    int insert(@Param("userId") Long userId, @Param("name") String name);

    int updateName(@Param("userId") Long userId, @Param("id") Long id, @Param("name") String name);

    int delete(@Param("userId") Long userId, @Param("id") Long id);

    Long selectDefaultChildId(@Param("userId") Long userId, @Param("defaultName") String defaultName);

    int countUsage(@Param("userId") Long userId, @Param("id") Long id);

    int moveUsage(@Param("userId") Long userId, @Param("id") Long id, @Param("fallbackId") Long fallbackId);
}
