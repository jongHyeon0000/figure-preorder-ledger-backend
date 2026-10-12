package com.jong.figurepreorderledgerbackend.service.shop;

import com.jong.figurepreorderledgerbackend.common.constant.CategoryConstant;
import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.mapper.shop.ShopMapper;
import com.jong.figurepreorderledgerbackend.mapper.shop.ShopGroupMapper;
import com.jong.figurepreorderledgerbackend.vo.shop.ShopResponse;
import com.jong.figurepreorderledgerbackend.vo.shop.ShopGroupResponse;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopGroupService {

    private final ShopGroupMapper shopGroupMapper;
    private final ShopMapper shopMapper;

    public List<ShopGroupResponse> getTree(Long userId) {
        List<ShopGroupResponse> parents = shopGroupMapper.selectAll(userId);
        List<ShopResponse> children = shopMapper.selectAll(userId);
        for (ShopGroupResponse parent : parents) {
            List<ShopResponse> own = new ArrayList<>();
            for (ShopResponse child : children) {
                if (parent.getShopGroupId().equals(child.getShopGroupId())) {
                    own.add(child);
                }
            }
            parent.setChildren(own);
        }
        return parents;
    }

    /* 상위를 만들면 기본 하위(기타)도 같이 만든다 */
    @Transactional
    public ShopGroupResponse create(Long userId, String name) {
        String trimmed = name.trim();
        try {
            shopGroupMapper.insert(userId, trimmed);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
        ShopGroupResponse parent = shopGroupMapper.selectByName(userId, trimmed);

        shopMapper.insert(userId, parent.getShopGroupId(), CategoryConstant.DEFAULT_NAME);
        List<ShopResponse> children = new ArrayList<>();
        children.add(shopMapper.selectByParentAndName(userId, parent.getShopGroupId(), CategoryConstant.DEFAULT_NAME));
        parent.setChildren(children);
        return parent;
    }

    @Transactional
    public void update(Long userId, Long shopGroupId, String name) {
        ShopGroupResponse parent = find(userId, shopGroupId);
        if (CategoryConstant.DEFAULT_NAME.equals(parent.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            shopGroupMapper.updateName(userId, shopGroupId, name.trim());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
    }

    /* 쓰던 상품을 기본 항목(기타 > 기타)으로 옮긴 뒤 삭제한다. 하위는 DB에서 함께 삭제된다 */
    @Transactional
    public void delete(Long userId, Long shopGroupId) {
        ShopGroupResponse parent = find(userId, shopGroupId);
        if (CategoryConstant.DEFAULT_NAME.equals(parent.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        Long fallbackId = shopGroupMapper.selectDefaultChildId(userId, CategoryConstant.DEFAULT_NAME);
        if (fallbackId == null) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }
        shopGroupMapper.moveUsage(userId, shopGroupId, fallbackId);
        shopGroupMapper.delete(userId, shopGroupId);
    }

    public UsageResponse getUsage(Long userId, Long shopGroupId) {
        find(userId, shopGroupId);
        return new UsageResponse(shopGroupMapper.countUsage(userId, shopGroupId));
    }

    private ShopGroupResponse find(Long userId, Long shopGroupId) {
        ShopGroupResponse parent = shopGroupMapper.selectById(userId, shopGroupId);
        if (parent == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        return parent;
    }
}
