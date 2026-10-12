package com.jong.figurepreorderledgerbackend.service.figure.impl;

import com.jong.figurepreorderledgerbackend.common.constant.FigureStatus;
import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.constant.UploadConstant;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.config.properties.UploadProperties;
import com.jong.figurepreorderledgerbackend.mapper.figure.FigureCharacterMapper;
import com.jong.figurepreorderledgerbackend.mapper.figure.FigureEditorImageMapper;
import com.jong.figurepreorderledgerbackend.mapper.figure.FigureMapper;
import com.jong.figurepreorderledgerbackend.mapper.figure.FigureSeriesMapper;
import com.jong.figurepreorderledgerbackend.mapper.figure.ThumbnailImageMapper;
import com.jong.figurepreorderledgerbackend.mapper.genre.GenreCharacterMapper;
import com.jong.figurepreorderledgerbackend.mapper.maker.MakerMapper;
import com.jong.figurepreorderledgerbackend.mapper.series.SeriesMapper;
import com.jong.figurepreorderledgerbackend.mapper.shop.ShopMapper;
import com.jong.figurepreorderledgerbackend.mapper.type.FigureTypeMapper;
import com.jong.figurepreorderledgerbackend.service.editor.EditorImageService;
import com.jong.figurepreorderledgerbackend.service.figure.FigureService;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureResponseVO;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureSaveRequestVO;
import com.jong.figurepreorderledgerbackend.vo.genre.GenreCharacterVO;
import com.jong.figurepreorderledgerbackend.vo.series.SeriesVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FigureServiceImpl implements FigureService {

    private final FigureMapper figureMapper;
    private final FigureSeriesMapper figureSeriesMapper;
    private final FigureCharacterMapper figureCharacterMapper;
    private final FigureEditorImageMapper figureEditorImageMapper;
    private final FigureTypeMapper figureTypeMapper;
    private final ShopMapper shopMapper;
    private final MakerMapper makerMapper;
    private final SeriesMapper seriesMapper;
    private final GenreCharacterMapper genreCharacterMapper;
    private final EditorImageService editorImageService;
    private final ThumbnailImageMapper thumbnailImageMapper;
    private final UploadProperties uploadProperties;

    @Override
    public List<FigureResponseVO> getList(Long userId) {
        List<FigureResponseVO> figures = figureMapper.selectList(userId);
        if (figures.isEmpty()) {
            return figures;
        }
        List<Long> figureIds = new ArrayList<>();
        for (FigureResponseVO figure : figures) {
            figureIds.add(figure.getFigureId());
        }
        Map<Long, List<SeriesVO>> seriesByFigure = new HashMap<>();
        for (SeriesVO series : figureSeriesMapper.selectByFigureIds(userId, figureIds)) {
            seriesByFigure.computeIfAbsent(series.getFigureId(), key -> new ArrayList<>()).add(series);
        }
        Map<Long, List<GenreCharacterVO>> charactersByFigure = new HashMap<>();
        for (GenreCharacterVO character : figureCharacterMapper.selectByFigureIds(userId, figureIds)) {
            charactersByFigure.computeIfAbsent(character.getFigureId(), key -> new ArrayList<>()).add(character);
        }
        for (FigureResponseVO figure : figures) {
            figure.setSeries(seriesByFigure.getOrDefault(figure.getFigureId(), new ArrayList<>()));
            figure.setCharacters(charactersByFigure.getOrDefault(figure.getFigureId(), new ArrayList<>()));
            applyThumbnailUrl(figure);
        }
        return figures;
    }

    @Override
    public FigureResponseVO getDetail(Long userId, Long figureId) {
        FigureResponseVO figure = figureMapper.selectDetailById(userId, figureId);
        if (figure == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        List<Long> figureIds = Collections.singletonList(figureId);
        figure.setSeries(figureSeriesMapper.selectByFigureIds(userId, figureIds));
        figure.setCharacters(figureCharacterMapper.selectByFigureIds(userId, figureIds));
        applyThumbnailUrl(figure);
        return figure;
    }

    @Override
    public FigureResponseVO getListItem(Long userId, Long figureId) {
        FigureResponseVO figure = figureMapper.selectListById(userId, figureId);
        if (figure == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        List<Long> figureIds = Collections.singletonList(figureId);
        figure.setSeries(figureSeriesMapper.selectByFigureIds(userId, figureIds));
        figure.setCharacters(figureCharacterMapper.selectByFigureIds(userId, figureIds));
        applyThumbnailUrl(figure);
        return figure;
    }

    @Override
    @Transactional
    public FigureResponseVO create(Long userId, FigureSaveRequestVO request) {
        FigureStatus status = request.getStatus() == null ? FigureStatus.CART : request.getStatus();
        if (status != FigureStatus.CART && status != FigureStatus.UNPURCHASED) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "등록 시 상태는 CART 또는 UNPURCHASED만 가능합니다");
        }
        List<Long> seriesIds = distinct(request.getSeriesIds());
        List<Long> characterIds = distinct(request.getCharacterIds());
        checkCategories(userId, request.getFigureTypeId(), request.getShopId(), request.getMakerId(),
                seriesIds, characterIds);
        editorImageService.checkNoBase64(request.getDescription());

        request.setName(request.getName().trim());
        request.setDeliveryMonth(firstDayOfMonth(request.getDeliveryMonth()));
        figureMapper.insert(userId, status, request);
        Long figureId = figureMapper.selectLastInsertId();
        figureSeriesMapper.insertAll(userId, figureId, seriesIds);
        figureCharacterMapper.insertAll(userId, figureId, characterIds);
        linkEditorImages(userId, figureId, request.getDescription());
        return getListItem(userId, figureId);
    }

    @Override
    @Transactional
    public FigureResponseVO update(Long userId, Long figureId, FigureSaveRequestVO request) {
        if (figureMapper.selectCurrentStatus(userId, figureId) == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        List<Long> seriesIds = distinct(request.getSeriesIds());
        List<Long> characterIds = distinct(request.getCharacterIds());
        checkCategories(userId, request.getFigureTypeId(), request.getShopId(), request.getMakerId(),
                seriesIds, characterIds);
        editorImageService.checkNoBase64(request.getDescription());

        request.setName(request.getName().trim());
        request.setDeliveryMonth(firstDayOfMonth(request.getDeliveryMonth()));
        figureMapper.update(userId, figureId, request);
        figureSeriesMapper.deleteByFigure(userId, figureId);
        figureSeriesMapper.insertAll(userId, figureId, seriesIds);
        figureCharacterMapper.deleteByFigure(userId, figureId);
        figureCharacterMapper.insertAll(userId, figureId, characterIds);
        figureEditorImageMapper.deleteByFigure(userId, figureId);
        linkEditorImages(userId, figureId, request.getDescription());
        return getListItem(userId, figureId);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long figureId) {
        List<String> thumbnailPaths = thumbnailImageMapper.selectFilePathsByFigureIds(userId,
                Collections.singletonList(figureId));
        if (figureMapper.delete(userId, figureId) == 0) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        deleteThumbnailFiles(thumbnailPaths);
    }

    @Override
    @Transactional
    public void deleteAll(Long userId, List<Long> figureIds) {
        List<Long> distinctIds = distinct(figureIds);
        if (distinctIds.isEmpty()) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "삭제할 상품 id가 필요합니다");
        }
        List<String> thumbnailPaths = thumbnailImageMapper.selectFilePathsByFigureIds(userId, distinctIds);
        if (figureMapper.deleteByIds(userId, distinctIds) != distinctIds.size()) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        deleteThumbnailFiles(thumbnailPaths);
    }

    /* 설명 HTML에 쓰인 에디터 이미지를 이 상품에 연결한다. 업로드 경로가 정확히 같은 이미지만 연결하고 외부 주소는 무시한다 */
    private void linkEditorImages(Long userId, Long figureId, String description) {
        List<String> filePaths = editorImageService.extractFilePaths(description);
        if (!filePaths.isEmpty()) {
            figureEditorImageMapper.insertAllByFilePaths(userId, figureId, filePaths);
        }
    }

    /* DB에는 상대 경로만 있으므로 응답에서 /uploads/ 를 붙인다 */
    private void applyThumbnailUrl(FigureResponseVO figure) {
        if (figure.getThumbnailUrl() != null) {
            figure.setThumbnailUrl(UploadConstant.URL_PREFIX + figure.getThumbnailUrl());
        }
    }

    /* 상품 삭제로 썸네일 행은 DB가 지웠으므로 파일만 지운다. 실패해도 삭제 결과는 그대로 두고 로그만 남긴다 */
    private void deleteThumbnailFiles(List<String> filePaths) {
        Path root = uploadProperties.getDir().toAbsolutePath().normalize();
        for (String filePath : filePaths) {
            Path target = root.resolve(filePath).normalize();
            if (!target.startsWith(root)) {
                log.warn("업로드 폴더 밖 경로라 지우지 않음: {}", filePath);
                continue;
            }
            try {
                Files.deleteIfExists(target);
            } catch (IOException e) {
                log.warn("썸네일 파일 정리 실패: {}", target, e);
            }
        }
    }

    /* 모든 분류 id가 현재 사용자의 것인지 확인한다. 없거나 남의 것이면 404 */
    private void checkCategories(Long userId, Long figureTypeId, Long shopId, Long makerId,
                                 List<Long> seriesIds, List<Long> characterIds) {
        if (figureTypeMapper.selectById(userId, figureTypeId) == null
                || shopMapper.selectById(userId, shopId) == null
                || makerMapper.selectById(userId, makerId) == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        for (Long seriesId : seriesIds) {
            if (seriesMapper.selectById(userId, seriesId) == null) {
                throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
            }
        }
        for (Long characterId : characterIds) {
            if (genreCharacterMapper.selectById(userId, characterId) == null) {
                throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
            }
        }
    }

    private List<Long> distinct(List<Long> figureIds) {
        if (figureIds == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(new LinkedHashSet<>(figureIds));
    }

    /* 배송예정월은 해당 월 1일로 저장한다 */
    private LocalDate firstDayOfMonth(LocalDate date) {
        return date == null ? null : date.withDayOfMonth(1);
    }
}
