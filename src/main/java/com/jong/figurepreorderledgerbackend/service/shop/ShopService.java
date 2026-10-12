package com.jong.figurepreorderledgerbackend.service.shop;

import com.jong.figurepreorderledgerbackend.common.constant.CategoryConstant;
import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.mapper.shop.ShopMapper;
import com.jong.figurepreorderledgerbackend.mapper.shop.ShopGroupMapper;
import com.jong.figurepreorderledgerbackend.vo.shop.ShopResponse;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopService {

    private final ShopMapper shopMapper;
    private final ShopGroupMapper shopGroupMapper;

    public List<ShopResponse> getAll(Long userId) {
        return shopMapper.selectAll(userId);
    }

    @Transactional
    public ShopResponse create(Long userId, Long shopGroupId, String name) {
        if (shopGroupMapper.selectById(userId, shopGroupId) == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        String trimmed = name.trim();
        try {
            shopMapper.insert(userId, shopGroupId, trimmed);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
        return shopMapper.selectByParentAndName(userId, shopGroupId, trimmed);
    }

    @Transactional
    public void update(Long userId, Long shopId, String name) {
        ShopResponse child = find(userId, shopId);
        if (CategoryConstant.DEFAULT_NAME.equals(child.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            shopMapper.updateName(userId, shopId, name.trim());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
    }

    /* 쓰던 상품을 기본 항목(기타 > 기타)으로 옮긴 뒤 삭제한다 */
    @Transactional
    public void delete(Long userId, Long shopId) {
        ShopResponse child = find(userId, shopId);
        if (CategoryConstant.DEFAULT_NAME.equals(child.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        if (shopMapper.countByParent(userId, child.getShopGroupId()) <= 1) {
            throw new BusinessException(GlobalExceptionCode.LAST_CHILD_CATEGORY);
        }
        Long fallbackId = shopMapper.selectDefaultId(userId, CategoryConstant.DEFAULT_NAME);
        if (fallbackId == null) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }
        shopMapper.moveUsage(userId, shopId, fallbackId);
        shopMapper.delete(userId, shopId);
    }

    public UsageResponse getUsage(Long userId, Long shopId) {
        find(userId, shopId);
        return new UsageResponse(shopMapper.countUsage(userId, shopId));
    }

    private ShopResponse find(Long userId, Long shopId) {
        ShopResponse child = shopMapper.selectById(userId, shopId);
        if (child == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        return child;
    }
}
