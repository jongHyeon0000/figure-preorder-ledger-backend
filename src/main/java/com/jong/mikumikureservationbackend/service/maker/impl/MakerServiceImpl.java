package com.jong.mikumikureservationbackend.service.maker.impl;

import com.jong.mikumikureservationbackend.common.constant.CategoryConstant;
import com.jong.mikumikureservationbackend.common.constant.GlobalExceptionCode;
import com.jong.mikumikureservationbackend.common.constant.ThumbnailTarget;
import com.jong.mikumikureservationbackend.common.exception.BusinessException;
import com.jong.mikumikureservationbackend.mapper.maker.MakerMapper;
import com.jong.mikumikureservationbackend.service.maker.MakerService;
import com.jong.mikumikureservationbackend.service.thumbnail.ThumbnailImageService;
import com.jong.mikumikureservationbackend.vo.common.UsageResponse;
import com.jong.mikumikureservationbackend.vo.maker.MakerVO;
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
public class MakerServiceImpl implements MakerService {

    private final MakerMapper makerMapper;
    private final ThumbnailImageService thumbnailImageService;

    @Override
    public List<MakerVO> getAll(Long userId) {
        List<MakerVO> list = makerMapper.selectAll(userId);
        // 아무 카테고리에도 해당되지 않는 미분류(기타)는 항상 맨 뒤로 보낸다(안정 정렬이라 나머지는 생성 순 유지)
        list.sort(Comparator.comparing(v -> CategoryConstant.DEFAULT_NAME.equals(v.getName())));
        return list;
    }

    @Override
    @Transactional
    public MakerVO create(Long userId, MakerVO vo) {
        String trimmed = vo.getName().trim();
        try {
            makerMapper.insert(userId, trimmed, vo.getDescription());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
        return makerMapper.selectByName(userId, trimmed);
    }

    @Override
    @Transactional
    public void update(Long userId, Long makerId, MakerVO vo) {
        MakerVO maker = find(userId, makerId);
        if (CategoryConstant.DEFAULT_NAME.equals(maker.getName())
                && !CategoryConstant.DEFAULT_NAME.equals(vo.getName().trim())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            makerMapper.update(userId, makerId, vo.getName().trim(), vo.getDescription());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
    }

    @Override
    @Transactional
    public void delete(Long userId, Long makerId) {
        MakerVO maker = find(userId, makerId);
        if (CategoryConstant.DEFAULT_NAME.equals(maker.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        Long fallbackId = makerMapper.selectDefaultId(userId, CategoryConstant.DEFAULT_NAME);
        if (fallbackId == null) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }
        makerMapper.moveUsage(userId, makerId, fallbackId);
        makerMapper.delete(userId, makerId);
        thumbnailImageService.deleteByTargets(userId, ThumbnailTarget.MAKER, Collections.singletonList(makerId));
    }

    @Override
    public UsageResponse getUsage(Long userId, Long makerId) {
        find(userId, makerId);
        return new UsageResponse(makerMapper.countUsage(userId, makerId));
    }

    private MakerVO find(Long userId, Long makerId) {
        MakerVO maker = makerMapper.selectById(userId, makerId);
        if (maker == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        return maker;
    }
}
