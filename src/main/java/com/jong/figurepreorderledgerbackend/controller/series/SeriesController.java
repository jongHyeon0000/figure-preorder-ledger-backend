package com.jong.figurepreorderledgerbackend.controller.series;

import com.jong.figurepreorderledgerbackend.common.response.ApiResponse;
import com.jong.figurepreorderledgerbackend.common.security.CurrentUser;
import com.jong.figurepreorderledgerbackend.service.series.SeriesService;
import com.jong.figurepreorderledgerbackend.vo.series.SeriesCreateRequest;
import com.jong.figurepreorderledgerbackend.vo.series.SeriesResponse;
import com.jong.figurepreorderledgerbackend.vo.series.SeriesUpdateRequest;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
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
     * @return ApiResponse<List<SeriesResponse>> id, groupId(상위 시리즈 그룹 id), name
     */
    @GetMapping
    public ApiResponse<List<SeriesResponse>> getAll() {
        return ApiResponse.ok(seriesService.getAll(currentUser.getId()));
    }

    /**
     * 시리즈 추가 (예: 팝업퍼레이드)
     * [POST] 단일 | /api/series
     *
     * @param request groupId: 상위 시리즈 그룹 id, name: 시리즈 이름 (본문, 둘 다 필수, 이름은 100자 이하)
     *
     * @return ApiResponse<SeriesResponse> 생성된 id, groupId, name
     * @throws 404 상위 시리즈 그룹 없음 | 400 이름 중복, 입력값 오류
     */
    @PostMapping
    public ApiResponse<SeriesResponse> create(@Valid @RequestBody SeriesCreateRequest request) {
        return ApiResponse.ok(seriesService.create(currentUser.getId(), request.getGroupId(), request.getName()));
    }

    /**
     * 시리즈 이름 수정 (예: 팝업퍼레이드)
     * [PUT] 단일 | /api/series/{id}
     *
     * @param id      수정할 시리즈 id (경로)
     * @param request name: 새 이름 (본문, 필수, 100자 이하)
     *
     * @return ApiResponse<Void> 성공 시 data 없음
     * @throws 404 대상 없음 | 400 기타 수정 불가, 이름 중복, 입력값 오류
     */
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody SeriesUpdateRequest request) {
        seriesService.update(currentUser.getId(), id, request.getName());
        return ApiResponse.ok();
    }

    /**
     * 시리즈 삭제, 쓰던 상품은 '기타'로 이동 (예: 팝업퍼레이드)
     * [DELETE] 없음 | /api/series/{id}
     *
     * @param id 삭제할 시리즈 id (경로)
     *
     * @return ApiResponse<Void> 성공 시 data 없음
     * @throws 404 대상 없음 | 400 기타 삭제 불가, 상위의 마지막 하위 삭제 불가
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        seriesService.delete(currentUser.getId(), id);
        return ApiResponse.ok();
    }

    /**
     * 시리즈 삭제 전 영향 상품 수 조회 (예: 팝업퍼레이드)
     * [GET] 단일 | /api/series/{id}/usage
     *
     * @param id 조회할 시리즈 id (경로)
     *
     * @return ApiResponse<UsageResponse> count: 이 시리즈를 쓰는 상품 수
     * @throws 404 대상 없음
     */
    @GetMapping("/{id}/usage")
    public ApiResponse<UsageResponse> getUsage(@PathVariable Long id) {
        return ApiResponse.ok(seriesService.getUsage(currentUser.getId(), id));
    }
}
