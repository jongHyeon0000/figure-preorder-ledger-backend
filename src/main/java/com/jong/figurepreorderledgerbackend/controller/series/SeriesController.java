package com.jong.figurepreorderledgerbackend.controller.series;

import com.jong.figurepreorderledgerbackend.common.response.ApiResponse;
import com.jong.figurepreorderledgerbackend.common.security.CurrentUser;
import com.jong.figurepreorderledgerbackend.service.series.SeriesService;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import com.jong.figurepreorderledgerbackend.vo.series.SeriesVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/series")
@RequiredArgsConstructor
public class SeriesController {

    private final SeriesService seriesService;
    private final CurrentUser currentUser;

    /**
     * 시리즈 목록 조회 (예: 팝업퍼레이드)
     * [GET] 목록 | /api/series
     *
     * @return ApiResponse<List<SeriesVO>> seriesId, seriesGroupId(상위 시리즈 그룹 id), name, description, thumbnailUrl
     */
    @GetMapping
    public ApiResponse<List<SeriesVO>> getAll() {
        return ApiResponse.ok(seriesService.getAll(currentUser.getId()));
    }

    /**
     * 시리즈 추가 (예: 팝업퍼레이드)
     * [POST] 단일 | /api/series
     *
     * @param request seriesGroupId: 상위 시리즈 그룹 id, name: 시리즈 이름 (본문, 둘 다 필수, 이름은 100자 이하), description: 설명 (본문, 선택, 1000자 이하)
     *
     * @return ApiResponse<SeriesVO> 생성된 seriesId, seriesGroupId, name, description, thumbnailUrl
     * @throws 404 상위 시리즈 그룹 없음 | 400 이름 중복, 입력값 오류
     */
    @PostMapping
    public ApiResponse<SeriesVO> create(@Valid @RequestBody SeriesVO request) {
        return ApiResponse.ok(seriesService.create(currentUser.getId(), request));
    }

    /**
     * 시리즈 이름과 설명 수정 (예: 팝업퍼레이드)
     * [PUT] 단일 | /api/series/{seriesId}
     *
     * @param seriesId 수정할 시리즈 id (경로)
     * @param request  name: 새 이름 (본문, 필수, 100자 이하), description: 새 설명 (본문, 선택, 1000자 이하, 생략하면 설명 삭제)
     *
     * @return ApiResponse<Void> 성공 시 data 없음
     * @throws 404 대상 없음 | 400 기타 이름 변경 불가, 이름 중복, 입력값 오류
     */
    @PutMapping("/{seriesId}")
    public ApiResponse<Void> update(@PathVariable Long seriesId, @Valid @RequestBody SeriesVO request) {
        seriesService.update(currentUser.getId(), seriesId, request);
        return ApiResponse.ok();
    }

    /**
     * 시리즈 삭제, 쓰던 상품은 '기타'로 이동 (예: 팝업퍼레이드)
     * [DELETE] 없음 | /api/series/{seriesId}
     *
     * @param seriesId 삭제할 시리즈 id (경로)
     *
     * @return ApiResponse<Void> 성공 시 data 없음
     * @throws 404 대상 없음 | 400 기타 삭제 불가, 상위의 마지막 하위 삭제 불가
     */
    @DeleteMapping("/{seriesId}")
    public ApiResponse<Void> delete(@PathVariable Long seriesId) {
        seriesService.delete(currentUser.getId(), seriesId);
        return ApiResponse.ok();
    }

    /**
     * 시리즈 삭제 전 영향 상품 수 조회 (예: 팝업퍼레이드)
     * [GET] 단일 | /api/series/{seriesId}/usage
     *
     * @param seriesId 조회할 시리즈 id (경로)
     *
     * @return ApiResponse<UsageResponse> count: 이 시리즈를 쓰는 상품 수
     * @throws 404 대상 없음
     */
    @GetMapping("/{seriesId}/usage")
    public ApiResponse<UsageResponse> getUsage(@PathVariable Long seriesId) {
        return ApiResponse.ok(seriesService.getUsage(currentUser.getId(), seriesId));
    }
}
