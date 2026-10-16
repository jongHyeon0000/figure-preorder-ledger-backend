package com.jong.mikumikureservationbackend.service.thumbnail.impl;

import com.jong.mikumikureservationbackend.common.constant.GlobalExceptionCode;
import com.jong.mikumikureservationbackend.common.constant.ThumbnailTarget;
import com.jong.mikumikureservationbackend.common.constant.UploadConstant;
import com.jong.mikumikureservationbackend.common.exception.BusinessException;
import com.jong.mikumikureservationbackend.mapper.figure.FigureMapper;
import com.jong.mikumikureservationbackend.mapper.genre.GenreCharacterGroupMapper;
import com.jong.mikumikureservationbackend.mapper.genre.GenreCharacterMapper;
import com.jong.mikumikureservationbackend.mapper.maker.MakerMapper;
import com.jong.mikumikureservationbackend.mapper.series.SeriesGroupMapper;
import com.jong.mikumikureservationbackend.mapper.series.SeriesMapper;
import com.jong.mikumikureservationbackend.mapper.shop.ShopGroupMapper;
import com.jong.mikumikureservationbackend.mapper.shop.ShopMapper;
import com.jong.mikumikureservationbackend.mapper.thumbnail.ThumbnailImageMapper;
import com.jong.mikumikureservationbackend.mapper.type.FigureTypeGroupMapper;
import com.jong.mikumikureservationbackend.mapper.type.FigureTypeMapper;
import com.jong.mikumikureservationbackend.mapper.user.UserMapper;
import com.jong.mikumikureservationbackend.service.common.ImageFileStore;
import com.jong.mikumikureservationbackend.service.thumbnail.ThumbnailImageService;
import com.jong.mikumikureservationbackend.vo.thumbnail.ThumbnailImageVO;
import com.jong.mikumikureservationbackend.vo.thumbnail.ThumbnailUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ThumbnailImageServiceImpl implements ThumbnailImageService {

    private final ThumbnailImageMapper thumbnailImageMapper;
    private final ImageFileStore imageFileStore;

    // 대상이 내 것인지 확인할 때 쓰는 종류별 매퍼
    private final FigureMapper figureMapper;
    private final ShopGroupMapper shopGroupMapper;
    private final ShopMapper shopMapper;
    private final FigureTypeGroupMapper figureTypeGroupMapper;
    private final FigureTypeMapper figureTypeMapper;
    private final SeriesGroupMapper seriesGroupMapper;
    private final SeriesMapper seriesMapper;
    private final GenreCharacterGroupMapper genreCharacterGroupMapper;
    private final GenreCharacterMapper genreCharacterMapper;
    private final MakerMapper makerMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public ThumbnailUrlResponse upload(Long userId, ThumbnailTarget target, Long targetId, MultipartFile file) {
        checkTargetExists(userId, target, targetId);
        imageFileStore.validate(file);
        String originalName = imageFileStore.originalNameOf(file);
        String extension = imageFileStore.extensionOf(originalName);

        // 이미 썸네일이 있으면 행을 지우고 새로 넣는다(대상당 1개). 옛 파일은 새 파일 저장에 성공한 뒤 지운다
        ThumbnailImageVO old = thumbnailImageMapper.selectByTarget(userId, target, targetId);
        if (old != null) {
            thumbnailImageMapper.deleteByTarget(userId, target, targetId);
        }
        String filePath = buildFilePath(userId, target, extension);
        thumbnailImageMapper.insert(userId, target, targetId, filePath, originalName, file.getSize());

        imageFileStore.save(file, filePath);   // 실패하면 예외로 DB 변경도 롤백된다
        if (old != null) {
            imageFileStore.delete(old.getFilePath());
        }
        return new ThumbnailUrlResponse(UploadConstant.URL_PREFIX + filePath);
    }

    @Override
    @Transactional
    public void delete(Long userId, ThumbnailTarget target, Long targetId) {
        checkTargetExists(userId, target, targetId);
        ThumbnailImageVO old = thumbnailImageMapper.selectByTarget(userId, target, targetId);
        if (old != null) {
            thumbnailImageMapper.deleteByTarget(userId, target, targetId);
            imageFileStore.delete(old.getFilePath());
        }
    }

    @Override
    @Transactional
    public void deleteByTargets(Long userId, ThumbnailTarget target, List<Long> targetIds) {
        if (targetIds == null || targetIds.isEmpty()) {
            return;
        }
        List<String> filePaths = thumbnailImageMapper.selectFilePathsByTargets(userId, target, targetIds);
        if (filePaths.isEmpty()) {
            return;
        }
        thumbnailImageMapper.deleteByTargets(userId, target, targetIds);
        for (String filePath : filePaths) {
            imageFileStore.delete(filePath);
        }
    }

    /* 종류에 맞는 테이블에서 대상이 현재 사용자의 것인지 확인한다. 없거나 남의 것이면 404 */
    private void checkTargetExists(Long userId, ThumbnailTarget target, Long targetId) {
        boolean exists;
        switch (target) {
            case FIGURE:
                exists = figureMapper.selectCurrentStatus(userId, targetId) != null;
                break;
            case SHOP_GROUP:
                exists = shopGroupMapper.selectById(userId, targetId) != null;
                break;
            case SHOP:
                exists = shopMapper.selectById(userId, targetId) != null;
                break;
            case FIGURE_TYPE_GROUP:
                exists = figureTypeGroupMapper.selectById(userId, targetId) != null;
                break;
            case FIGURE_TYPE:
                exists = figureTypeMapper.selectById(userId, targetId) != null;
                break;
            case SERIES_GROUP:
                exists = seriesGroupMapper.selectById(userId, targetId) != null;
                break;
            case SERIES:
                exists = seriesMapper.selectById(userId, targetId) != null;
                break;
            case GENRE_CHARACTER_GROUP:
                exists = genreCharacterGroupMapper.selectById(userId, targetId) != null;
                break;
            case GENRE_CHARACTER:
                exists = genreCharacterMapper.selectById(userId, targetId) != null;
                break;
            case MAKER:
                exists = makerMapper.selectById(userId, targetId) != null;
                break;
            case USER:
                // 계정의 썸네일은 대상 id가 곧 내 userId다
                exists = userMapper.selectById(userId) != null && userId.equals(targetId);
                break;
            default:
                exists = false;
        }
        if (!exists) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
    }

    /* 저장 경로는 서버가 만든다: {userId}/thumbnail-image/{대상 종류}/{yyyy}/{MM}/{UUID}.{확장자} (원본 파일명은 경로에 쓰지 않는다) */
    private String buildFilePath(Long userId, ThumbnailTarget target, String extension) {
        LocalDate today = LocalDate.now();
        return String.format("%d/%s/%s/%d/%02d/%s.%s", userId, UploadConstant.THUMBNAIL_IMAGE_DIR, target.getUrlValue(),
                today.getYear(), today.getMonthValue(), UUID.randomUUID(), extension);
    }
}
