package com.jong.figurepreorderledgerbackend.service.figure;

import com.jong.figurepreorderledgerbackend.common.constant.FigureStatus;
import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.mapper.figure.FigureCharacterMapper;
import com.jong.figurepreorderledgerbackend.mapper.figure.FigureMapper;
import com.jong.figurepreorderledgerbackend.mapper.figure.FigureSeriesMapper;
import com.jong.figurepreorderledgerbackend.mapper.genre.GenreCharacterMapper;
import com.jong.figurepreorderledgerbackend.mapper.maker.MakerMapper;
import com.jong.figurepreorderledgerbackend.mapper.series.SeriesMapper;
import com.jong.figurepreorderledgerbackend.mapper.shop.ShopMapper;
import com.jong.figurepreorderledgerbackend.mapper.type.FigureSubtypeMapper;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureCharacterInfo;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureCreateRequest;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureDetailResponse;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureListResponse;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureSeriesInfo;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FigureService {

    private final FigureMapper figureMapper;
    private final FigureSeriesMapper figureSeriesMapper;
    private final FigureCharacterMapper figureCharacterMapper;
    private final FigureSubtypeMapper figureSubtypeMapper;
    private final ShopMapper shopMapper;
    private final MakerMapper makerMapper;
    private final SeriesMapper seriesMapper;
    private final GenreCharacterMapper genreCharacterMapper;

    public List<FigureListResponse> getList(Long userId) {
        List<FigureListResponse> figures = figureMapper.selectList(userId);
        if (figures.isEmpty()) {
            return figures;
        }
        List<Long> figureIds = new ArrayList<>();
        for (FigureListResponse figure : figures) {
            figureIds.add(figure.getId());
        }
        Map<Long, List<FigureSeriesInfo>> seriesByFigure = new HashMap<>();
        for (FigureSeriesInfo series : figureSeriesMapper.selectByFigureIds(userId, figureIds)) {
            seriesByFigure.computeIfAbsent(series.getFigureId(), key -> new ArrayList<>()).add(series);
        }
        Map<Long, List<FigureCharacterInfo>> charactersByFigure = new HashMap<>();
        for (FigureCharacterInfo character : figureCharacterMapper.selectByFigureIds(userId, figureIds)) {
            charactersByFigure.computeIfAbsent(character.getFigureId(), key -> new ArrayList<>()).add(character);
        }
        for (FigureListResponse figure : figures) {
            figure.setSeries(seriesByFigure.getOrDefault(figure.getId(), new ArrayList<>()));
            figure.setCharacters(charactersByFigure.getOrDefault(figure.getId(), new ArrayList<>()));
        }
        return figures;
    }

    public FigureDetailResponse getDetail(Long userId, Long id) {
        FigureDetailResponse figure = figureMapper.selectDetailById(userId, id);
        if (figure == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        List<Long> figureIds = Collections.singletonList(id);
        figure.setSeries(figureSeriesMapper.selectByFigureIds(userId, figureIds));
        figure.setCharacters(figureCharacterMapper.selectByFigureIds(userId, figureIds));
        return figure;
    }

    /* 상태 전이 등 다른 서비스가 변경된 상품 한 건을 목록 항목 모양으로 돌려줄 때도 쓴다 */
    public FigureListResponse getListItem(Long userId, Long id) {
        FigureListResponse figure = figureMapper.selectListById(userId, id);
        if (figure == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        List<Long> figureIds = Collections.singletonList(id);
        figure.setSeries(figureSeriesMapper.selectByFigureIds(userId, figureIds));
        figure.setCharacters(figureCharacterMapper.selectByFigureIds(userId, figureIds));
        return figure;
    }

    @Transactional
    public FigureListResponse create(Long userId, FigureCreateRequest request) {
        FigureStatus status = request.getStatus() == null ? FigureStatus.CART : request.getStatus();
        if (status != FigureStatus.CART && status != FigureStatus.UNPURCHASED) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "등록 시 상태는 CART 또는 UNPURCHASED만 가능합니다");
        }
        List<Long> seriesIds = distinct(request.getSeriesIds());
        List<Long> characterIds = distinct(request.getCharacterIds());
        checkCategories(userId, request.getSubtypeId(), request.getShopId(), request.getMakerId(),
                seriesIds, characterIds);

        request.setName(request.getName().trim());
        request.setDeliveryMonth(firstDayOfMonth(request.getDeliveryMonth()));
        figureMapper.insert(userId, status, request);
        Long figureId = figureMapper.selectLastInsertId();
        figureSeriesMapper.insertAll(userId, figureId, seriesIds);
        figureCharacterMapper.insertAll(userId, figureId, characterIds);
        return getListItem(userId, figureId);
    }

    /* 상태와 결제 방식은 바꾸지 않는다. 시리즈와 캐릭터 연결은 지우고 다시 넣는다 */
    @Transactional
    public FigureListResponse update(Long userId, Long id, FigureUpdateRequest request) {
        if (figureMapper.selectCurrentStatus(userId, id) == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        List<Long> seriesIds = distinct(request.getSeriesIds());
        List<Long> characterIds = distinct(request.getCharacterIds());
        checkCategories(userId, request.getSubtypeId(), request.getShopId(), request.getMakerId(),
                seriesIds, characterIds);

        request.setName(request.getName().trim());
        request.setDeliveryMonth(firstDayOfMonth(request.getDeliveryMonth()));
        figureMapper.update(userId, id, request);
        figureSeriesMapper.deleteByFigure(userId, id);
        figureSeriesMapper.insertAll(userId, id, seriesIds);
        figureCharacterMapper.deleteByFigure(userId, id);
        figureCharacterMapper.insertAll(userId, id, characterIds);
        return getListItem(userId, id);
    }

    @Transactional
    public void delete(Long userId, Long id) {
        if (figureMapper.delete(userId, id) == 0) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
    }

    /* 하나라도 없는 상품이면 전체를 되돌린다 (예외로 트랜잭션 롤백) */
    @Transactional
    public void deleteAll(Long userId, List<Long> ids) {
        List<Long> distinctIds = distinct(ids);
        if (distinctIds.isEmpty()) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "삭제할 상품 id가 필요합니다");
        }
        if (figureMapper.deleteByIds(userId, distinctIds) != distinctIds.size()) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
    }

    /* 모든 분류 id가 현재 사용자의 것인지 확인한다. 없거나 남의 것이면 404 */
    private void checkCategories(Long userId, Long subtypeId, Long shopId, Long makerId,
                                 List<Long> seriesIds, List<Long> characterIds) {
        if (figureSubtypeMapper.selectById(userId, subtypeId) == null
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

    private List<Long> distinct(List<Long> ids) {
        if (ids == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(new LinkedHashSet<>(ids));
    }

    /* 배송예정월은 해당 월 1일로 저장한다 */
    private LocalDate firstDayOfMonth(LocalDate date) {
        return date == null ? null : date.withDayOfMonth(1);
    }
}
