package com.jong.figurepreorderledgerbackend.service.maker.impl;

import com.jong.figurepreorderledgerbackend.common.constant.CategoryConstant;
import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.mapper.maker.MakerMapper;
import com.jong.figurepreorderledgerbackend.service.maker.MakerService;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import com.jong.figurepreorderledgerbackend.vo.maker.MakerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MakerServiceImpl implements MakerService {

    private final MakerMapper makerMapper;

    @Override
    public List<MakerResponse> getAll(Long userId) {
        return makerMapper.selectAll(userId);
    }

    @Override
    @Transactional
    public MakerResponse create(Long userId, String name) {
        String trimmed = name.trim();
        try {
            makerMapper.insert(userId, trimmed);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
        return makerMapper.selectByName(userId, trimmed);
    }

    @Override
    @Transactional
    public void update(Long userId, Long makerId, String name) {
        MakerResponse maker = find(userId, makerId);
        if (CategoryConstant.DEFAULT_NAME.equals(maker.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            makerMapper.updateName(userId, makerId, name.trim());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
    }

    @Override
    @Transactional
    public void delete(Long userId, Long makerId) {
        MakerResponse maker = find(userId, makerId);
        if (CategoryConstant.DEFAULT_NAME.equals(maker.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        Long fallbackId = makerMapper.selectDefaultId(userId, CategoryConstant.DEFAULT_NAME);
        if (fallbackId == null) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }
        makerMapper.moveUsage(userId, makerId, fallbackId);
        makerMapper.delete(userId, makerId);
    }

    @Override
    public UsageResponse getUsage(Long userId, Long makerId) {
        find(userId, makerId);
        return new UsageResponse(makerMapper.countUsage(userId, makerId));
    }

    private MakerResponse find(Long userId, Long makerId) {
        MakerResponse maker = makerMapper.selectById(userId, makerId);
        if (maker == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        return maker;
    }
}
