package com.jong.figurepreorderledgerbackend.service.shop.impl;

import com.jong.figurepreorderledgerbackend.common.constant.CategoryConstant;
import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.constant.ThumbnailTarget;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.mapper.shop.ShopGroupMapper;
import com.jong.figurepreorderledgerbackend.mapper.shop.ShopMapper;
import com.jong.figurepreorderledgerbackend.service.shop.ShopService;
import com.jong.figurepreorderledgerbackend.service.thumbnail.ThumbnailImageService;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import com.jong.figurepreorderledgerbackend.vo.shop.ShopVO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopServiceImpl implements ShopService {

    private final ShopMapper shopMapper;
    private final ShopGroupMapper shopGroupMapper;
    private final ThumbnailImageService thumbnailImageService;

    @Override
    public List<ShopVO> getAll(Long userId) {
        List<ShopVO> list = shopMapper.selectAll(userId);
        // 아무 카테고리에도 해당되지 않는 미분류(기타)는 항상 맨 뒤로 보낸다(안정 정렬이라 나머지는 생성 순 유지)
        list.sort(Comparator.comparing(v -> CategoryConstant.DEFAULT_NAME.equals(v.getName())));
        return list;
    }

    @Override
    @Transactional
    public ShopVO create(Long userId, ShopVO vo) {
        Long shopGroupId = vo.getGroupId();
        if (shopGroupId == null) {
            throw new BusinessException(GlobalExceptionCode.VALIDATION_FAILED, "shopGroupId: 필수 값입니다");
        }
        if (shopGroupMapper.selectById(userId, shopGroupId) == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        String trimmed = vo.getName().trim();
        try {
            shopMapper.insert(userId, shopGroupId, trimmed, vo.getDescription());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
        return shopMapper.selectByParentAndName(userId, shopGroupId, trimmed);
    }

    @Override
    @Transactional
    public void update(Long userId, Long shopId, ShopVO vo) {
        ShopVO child = find(userId, shopId);
        if (CategoryConstant.DEFAULT_NAME.equals(child.getName())
                && !CategoryConstant.DEFAULT_NAME.equals(vo.getName().trim())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            shopMapper.update(userId, shopId, vo.getName().trim(), vo.getDescription());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
    }

    @Override
    @Transactional
    public void delete(Long userId, Long shopId) {
        ShopVO child = find(userId, shopId);
        if (CategoryConstant.DEFAULT_NAME.equals(child.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        if (shopMapper.countByParent(userId, child.getGroupId()) <= 1) {
            throw new BusinessException(GlobalExceptionCode.LAST_CHILD_CATEGORY);
        }
        Long fallbackId = shopMapper.selectDefaultId(userId, CategoryConstant.DEFAULT_NAME);
        if (fallbackId == null) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }
        shopMapper.moveUsage(userId, shopId, fallbackId);
        shopMapper.delete(userId, shopId);
        thumbnailImageService.deleteByTargets(userId, ThumbnailTarget.SHOP, Collections.singletonList(shopId));
    }

    @Override
    public UsageResponse getUsage(Long userId, Long shopId) {
        find(userId, shopId);
        return new UsageResponse(shopMapper.countUsage(userId, shopId));
    }

    private ShopVO find(Long userId, Long shopId) {
        ShopVO child = shopMapper.selectById(userId, shopId);
        if (child == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        return child;
    }
}
