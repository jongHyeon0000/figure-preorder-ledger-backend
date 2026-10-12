package com.jong.figurepreorderledgerbackend.controller.type;

import com.jong.figurepreorderledgerbackend.common.response.ApiResponse;
import com.jong.figurepreorderledgerbackend.common.security.CurrentUser;
import com.jong.figurepreorderledgerbackend.service.type.FigureTypeGroupService;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import com.jong.figurepreorderledgerbackend.vo.type.FigureTypeGroupVO;
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
@RequestMapping("/api/figure-type-group")
@RequiredArgsConstructor
public class FigureTypeGroupController {

    private final FigureTypeGroupService figureTypeGroupService;
    private final CurrentUser currentUser;

    /**
     * 형태 목록 조회, 하위 형태 하위 중첩 (예: 스케일, 넨도로이드)
     * [GET] 목록 | /api/figure-type-group
     *
     * @return ApiResponse<List<FigureTypeGroupVO>> figureTypeGroupId, name, children(형태 하위 목록: figureTypeId, figureTypeGroupId, name)
     */
    @GetMapping
    public ApiResponse<List<FigureTypeGroupVO>> getTree() {
        return ApiResponse.ok(figureTypeGroupService.getTree(currentUser.getId()));
    }

    /**
     * 형태 추가, 기본 하위 '기타' 자동 생성 (예: 스케일, 넨도로이드)
     * [POST] 단일 | /api/figure-type-group
     *
     * @param request name: 형태 이름 (본문, 필수, 100자 이하)
     *
     * @return ApiResponse<FigureTypeGroupVO> 생성된 figureTypeGroupId, name, children(기본 '기타' 하나)
     * @throws 400 이름 중복, 입력값 오류
     */
    @PostMapping
    public ApiResponse<FigureTypeGroupVO> create(@Valid @RequestBody FigureTypeGroupVO request) {
        return ApiResponse.ok(figureTypeGroupService.create(currentUser.getId(), request));
    }

    /**
     * 형태 이름 수정 (예: 스케일, 넨도로이드)
     * [PUT] 단일 | /api/figure-type-group/{figureTypeGroupId}
     *
     * @param figureTypeGroupId 수정할 형태 id (경로)
     * @param request           name: 새 이름 (본문, 필수, 100자 이하)
     *
     * @return ApiResponse<Void> 성공 시 data 없음
     * @throws 404 대상 없음 | 400 기타 수정 불가, 이름 중복, 입력값 오류
     */
    @PutMapping("/{figureTypeGroupId}")
    public ApiResponse<Void> update(@PathVariable Long figureTypeGroupId, @Valid @RequestBody FigureTypeGroupVO request) {
        figureTypeGroupService.update(currentUser.getId(), figureTypeGroupId, request);
        return ApiResponse.ok();
    }

    /**
     * 형태 삭제, 하위와 쓰던 상품은 '기타'로 이동 (예: 스케일, 넨도로이드)
     * [DELETE] 없음 | /api/figure-type-group/{figureTypeGroupId}
     *
     * @param figureTypeGroupId 삭제할 형태 id (경로)
     *
     * @return ApiResponse<Void> 성공 시 data 없음
     * @throws 404 대상 없음 | 400 기타 삭제 불가
     */
    @DeleteMapping("/{figureTypeGroupId}")
    public ApiResponse<Void> delete(@PathVariable Long figureTypeGroupId) {
        figureTypeGroupService.delete(currentUser.getId(), figureTypeGroupId);
        return ApiResponse.ok();
    }

    /**
     * 형태 삭제 전 영향 상품 수 조회 (예: 스케일, 넨도로이드)
     * [GET] 단일 | /api/figure-type-group/{figureTypeGroupId}/usage
     *
     * @param figureTypeGroupId 조회할 형태 id (경로)
     *
     * @return ApiResponse<UsageResponse> count: 이 형태의 하위를 쓰는 상품 수
     * @throws 404 대상 없음
     */
    @GetMapping("/{figureTypeGroupId}/usage")
    public ApiResponse<UsageResponse> getUsage(@PathVariable Long figureTypeGroupId) {
        return ApiResponse.ok(figureTypeGroupService.getUsage(currentUser.getId(), figureTypeGroupId));
    }
}
