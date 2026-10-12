package com.jong.figurepreorderledgerbackend.controller.series;

import com.jong.figurepreorderledgerbackend.common.response.ApiResponse;
import com.jong.figurepreorderledgerbackend.common.security.CurrentUser;
import com.jong.figurepreorderledgerbackend.service.series.SeriesGroupService;
import com.jong.figurepreorderledgerbackend.vo.series.request.SeriesGroupRequest;
import com.jong.figurepreorderledgerbackend.vo.series.response.SeriesGroupResponse;
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
@RequestMapping("/api/series-group")
@RequiredArgsConstructor
public class SeriesGroupController {

    private final SeriesGroupService seriesGroupService;
    private final CurrentUser currentUser;

    /**
     * 시리즈 그룹 목록 조회, 하위 시리즈 중첩 (예: 굿스마일 라인업)
     * [GET] 목록 | /api/series-group
     *
     * @return ApiResponse<List<SeriesGroupResponse>> seriesGroupId, name, children(시리즈 목록: seriesId, seriesGroupId, name)
     */
    @GetMapping
    public ApiResponse<List<SeriesGroupResponse>> getTree() {
        return ApiResponse.ok(seriesGroupService.getTree(currentUser.getId()));
    }

    /**
     * 시리즈 그룹 추가, 기본 하위 '기타' 자동 생성 (예: 굿스마일 라인업)
     * [POST] 단일 | /api/series-group
     *
     * @param request name: 시리즈 그룹 이름 (본문, 필수, 100자 이하)
     *
     * @return ApiResponse<SeriesGroupResponse> 생성된 seriesGroupId, name, children(기본 '기타' 하나)
     * @throws 400 이름 중복, 입력값 오류
     */
    @PostMapping
    public ApiResponse<SeriesGroupResponse> create(@Valid @RequestBody SeriesGroupRequest request) {
        return ApiResponse.ok(seriesGroupService.create(currentUser.getId(), request.getName()));
    }

    /**
     * 시리즈 그룹 이름 수정 (예: 굿스마일 라인업)
     * [PUT] 단일 | /api/series-group/{seriesGroupId}
     *
     * @param seriesGroupId 수정할 시리즈 그룹 id (경로)
     * @param request       name: 새 이름 (본문, 필수, 100자 이하)
     *
     * @return ApiResponse<Void> 성공 시 data 없음
     * @throws 404 대상 없음 | 400 기타 수정 불가, 이름 중복, 입력값 오류
     */
    @PutMapping("/{seriesGroupId}")
    public ApiResponse<Void> update(@PathVariable Long seriesGroupId, @Valid @RequestBody SeriesGroupRequest request) {
        seriesGroupService.update(currentUser.getId(), seriesGroupId, request.getName());
        return ApiResponse.ok();
    }

    /**
     * 시리즈 그룹 삭제, 하위와 쓰던 상품은 '기타'로 이동 (예: 굿스마일 라인업)
     * [DELETE] 없음 | /api/series-group/{seriesGroupId}
     *
     * @param seriesGroupId 삭제할 시리즈 그룹 id (경로)
     *
     * @return ApiResponse<Void> 성공 시 data 없음
     * @throws 404 대상 없음 | 400 기타 삭제 불가
     */
    @DeleteMapping("/{seriesGroupId}")
    public ApiResponse<Void> delete(@PathVariable Long seriesGroupId) {
        seriesGroupService.delete(currentUser.getId(), seriesGroupId);
        return ApiResponse.ok();
    }

    /**
     * 시리즈 그룹 삭제 전 영향 상품 수 조회 (예: 굿스마일 라인업)
     * [GET] 단일 | /api/series-group/{seriesGroupId}/usage
     *
     * @param seriesGroupId 조회할 시리즈 그룹 id (경로)
     *
     * @return ApiResponse<UsageResponse> count: 이 시리즈 그룹의 하위를 쓰는 상품 수
     * @throws 404 대상 없음
     */
    @GetMapping("/{seriesGroupId}/usage")
    public ApiResponse<UsageResponse> getUsage(@PathVariable Long seriesGroupId) {
        return ApiResponse.ok(seriesGroupService.getUsage(currentUser.getId(), seriesGroupId));
    }
}
