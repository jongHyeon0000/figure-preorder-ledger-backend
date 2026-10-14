package com.jong.figurepreorderledgerbackend.service.shop.impl;

import com.jong.figurepreorderledgerbackend.common.constant.CategoryConstant;
import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.constant.ThumbnailTarget;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.mapper.shop.ShopGroupMapper;
import com.jong.figurepreorderledgerbackend.mapper.shop.ShopMapper;
import com.jong.figurepreorderledgerbackend.service.shop.ShopGroupService;
import com.jong.figurepreorderledgerbackend.service.thumbnail.ThumbnailImageService;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import com.jong.figurepreorderledgerbackend.vo.shop.ShopGroupVO;
import com.jong.figurepreorderledgerbackend.vo.shop.ShopVO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopGroupServiceImpl implements ShopGroupService {

    private final ShopGroupMapper shopGroupMapper;
    private final ShopMapper shopMapper;
    private final ThumbnailImageService thumbnailImageService;

    @Override
    public List<ShopGroupVO> getTree(Long userId) {
        List<ShopGroupVO> parents = shopGroupMapper.selectAll(userId);
        List<ShopVO> children = shopMapper.selectAll(userId);
        // 아무 카테고리에도 해당되지 않는 미분류(기타)는 항상 맨 뒤로 보낸다(안정 정렬이라 나머지는 생성 순 유지)
        parents.sort(Comparator.comparing(p -> CategoryConstant.DEFAULT_NAME.equals(p.getName())));
        children.sort(Comparator.comparing(c -> CategoryConstant.DEFAULT_NAME.equals(c.getName())));
        for (ShopGroupVO parent : parents) {
            List<ShopVO> own = new ArrayList<>();
            for (ShopVO child : children) {
                if (parent.getShopGroupId().equals(child.getShopGroupId())) {
                    own.add(child);
                }
            }
            parent.setChildren(own);
        }
        return parents;
    }

    @Override
    @Transactional
    public ShopGroupVO create(Long userId, ShopGroupVO vo) {
        String trimmed = vo.getName().trim();
        try {
            shopGroupMapper.insert(userId, trimmed, vo.getDescription());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
        ShopGroupVO parent = shopGroupMapper.selectByName(userId, trimmed);

        shopMapper.insert(userId, parent.getShopGroupId(), CategoryConstant.DEFAULT_NAME, null);
        List<ShopVO> children = new ArrayList<>();
        children.add(shopMapper.selectByParentAndName(userId, parent.getShopGroupId(), CategoryConstant.DEFAULT_NAME));
        parent.setChildren(children);
        return parent;
    }

    @Override
    @Transactional
    public void update(Long userId, Long shopGroupId, ShopGroupVO vo) {
        ShopGroupVO parent = find(userId, shopGroupId);
        if (CategoryConstant.DEFAULT_NAME.equals(parent.getName())
                && !CategoryConstant.DEFAULT_NAME.equals(vo.getName().trim())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            shopGroupMapper.update(userId, shopGroupId, vo.getName().trim(), vo.getDescription());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
    }

    @Override
    @Transactional
    public void delete(Long userId, Long shopGroupId) {
        ShopGroupVO parent = find(userId, shopGroupId);
        if (CategoryConstant.DEFAULT_NAME.equals(parent.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        Long fallbackId = shopGroupMapper.selectDefaultChildId(userId, CategoryConstant.DEFAULT_NAME);
        if (fallbackId == null) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }
        shopGroupMapper.moveUsage(userId, shopGroupId, fallbackId);
        List<Long> childIds = shopMapper.selectIdsByParent(userId, shopGroupId);   // 상위를 지우면 하위도 CASCADE로 지워지므로 미리 모아 둔다
        shopGroupMapper.delete(userId, shopGroupId);
        thumbnailImageService.deleteByTargets(userId, ThumbnailTarget.SHOP_GROUP, Collections.singletonList(shopGroupId));
        thumbnailImageService.deleteByTargets(userId, ThumbnailTarget.SHOP, childIds);
    }

    @Override
    public UsageResponse getUsage(Long userId, Long shopGroupId) {
        find(userId, shopGroupId);
        return new UsageResponse(shopGroupMapper.countUsage(userId, shopGroupId));
    }

    private ShopGroupVO find(Long userId, Long shopGroupId) {
        ShopGroupVO parent = shopGroupMapper.selectById(userId, shopGroupId);
        if (parent == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        return parent;
    }
}
