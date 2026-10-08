package com.jong.figurepreorderledgerbackend.service.shop;

import com.jong.figurepreorderledgerbackend.common.constant.CategoryConstant;
import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.mapper.shop.ShopMapper;
import com.jong.figurepreorderledgerbackend.mapper.shop.ShopChannelMapper;
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
    private final ShopChannelMapper shopChannelMapper;

    public List<ShopResponse> getAll(Long userId) {
        return shopMapper.selectAll(userId);
    }

    @Transactional
    public ShopResponse create(Long userId, Long channelId, String name) {
        if (shopChannelMapper.selectById(userId, channelId) == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        String trimmed = name.trim();
        try {
            shopMapper.insert(userId, channelId, trimmed);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
        return shopMapper.selectByParentAndName(userId, channelId, trimmed);
    }

    @Transactional
    public void update(Long userId, Long id, String name) {
        ShopResponse child = find(userId, id);
        if (CategoryConstant.DEFAULT_NAME.equals(child.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            shopMapper.updateName(userId, id, name.trim());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
    }

    /* 쓰던 상품을 기본 항목(기타 > 기타)으로 옮긴 뒤 삭제한다 */
    @Transactional
    public void delete(Long userId, Long id) {
        ShopResponse child = find(userId, id);
        if (CategoryConstant.DEFAULT_NAME.equals(child.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        if (shopMapper.countByParent(userId, child.getChannelId()) <= 1) {
            throw new BusinessException(GlobalExceptionCode.LAST_CHILD_CATEGORY);
        }
        Long fallbackId = shopMapper.selectDefaultId(userId, CategoryConstant.DEFAULT_NAME);
        if (fallbackId == null) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }
        shopMapper.moveUsage(userId, id, fallbackId);
        shopMapper.delete(userId, id);
    }

    public UsageResponse getUsage(Long userId, Long id) {
        find(userId, id);
        return new UsageResponse(shopMapper.countUsage(userId, id));
    }

    private ShopResponse find(Long userId, Long id) {
        ShopResponse child = shopMapper.selectById(userId, id);
        if (child == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        return child;
    }
}
