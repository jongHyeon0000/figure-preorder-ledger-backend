package com.jong.figurepreorderledgerbackend.service.shop;

import com.jong.figurepreorderledgerbackend.common.constant.CategoryConstant;
import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.mapper.shop.ShopMapper;
import com.jong.figurepreorderledgerbackend.mapper.shop.ShopChannelMapper;
import com.jong.figurepreorderledgerbackend.vo.shop.ShopResponse;
import com.jong.figurepreorderledgerbackend.vo.shop.ShopChannelResponse;
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
public class ShopChannelService {

    private final ShopChannelMapper shopChannelMapper;
    private final ShopMapper shopMapper;

    public List<ShopChannelResponse> getTree(Long userId) {
        List<ShopChannelResponse> parents = shopChannelMapper.selectAll(userId);
        List<ShopResponse> children = shopMapper.selectAll(userId);
        for (ShopChannelResponse parent : parents) {
            List<ShopResponse> own = new ArrayList<>();
            for (ShopResponse child : children) {
                if (parent.getId().equals(child.getChannelId())) {
                    own.add(child);
                }
            }
            parent.setChildren(own);
        }
        return parents;
    }

    /* 상위를 만들면 기본 하위(기타)도 같이 만든다 */
    @Transactional
    public ShopChannelResponse create(Long userId, String name) {
        String trimmed = name.trim();
        try {
            shopChannelMapper.insert(userId, trimmed);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
        ShopChannelResponse parent = shopChannelMapper.selectByName(userId, trimmed);

        shopMapper.insert(userId, parent.getId(), CategoryConstant.DEFAULT_NAME);
        List<ShopResponse> children = new ArrayList<>();
        children.add(shopMapper.selectByParentAndName(userId, parent.getId(), CategoryConstant.DEFAULT_NAME));
        parent.setChildren(children);
        return parent;
    }

    @Transactional
    public void update(Long userId, Long id, String name) {
        ShopChannelResponse parent = find(userId, id);
        if (CategoryConstant.DEFAULT_NAME.equals(parent.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            shopChannelMapper.updateName(userId, id, name.trim());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
    }

    /* 쓰던 상품을 기본 항목(기타 > 기타)으로 옮긴 뒤 삭제한다. 하위는 DB에서 함께 삭제된다 */
    @Transactional
    public void delete(Long userId, Long id) {
        ShopChannelResponse parent = find(userId, id);
        if (CategoryConstant.DEFAULT_NAME.equals(parent.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        Long fallbackId = shopChannelMapper.selectDefaultChildId(userId, CategoryConstant.DEFAULT_NAME);
        if (fallbackId == null) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }
        shopChannelMapper.moveUsage(userId, id, fallbackId);
        shopChannelMapper.delete(userId, id);
    }

    public UsageResponse getUsage(Long userId, Long id) {
        find(userId, id);
        return new UsageResponse(shopChannelMapper.countUsage(userId, id));
    }

    private ShopChannelResponse find(Long userId, Long id) {
        ShopChannelResponse parent = shopChannelMapper.selectById(userId, id);
        if (parent == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        return parent;
    }
}
