package com.jong.figurepreorderledgerbackend.controller.type;

import com.jong.figurepreorderledgerbackend.common.response.ApiResponse;
import com.jong.figurepreorderledgerbackend.common.security.CurrentUser;
import com.jong.figurepreorderledgerbackend.service.type.FigureTypeService;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import com.jong.figurepreorderledgerbackend.vo.type.FigureTypeVO;
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
@RequestMapping("/api/figure-type")
@RequiredArgsConstructor
public class FigureTypeController {

    private final FigureTypeService figureTypeService;
    private final CurrentUser currentUser;

    /**
     * 형태 하위 목록 조회 (예: 푸치)
     * [GET] 목록 | /api/figure-type
     *
     * @return ApiResponse<List<FigureTypeVO>> figureTypeId, figureTypeGroupId(상위 형태 id), name
     */
    @GetMapping
    public ApiResponse<List<FigureTypeVO>> getAll() {
        return ApiResponse.ok(figureTypeService.getAll(currentUser.getId()));
    }

    /**
     * 형태 하위 추가 (예: 푸치)
     * [POST] 단일 | /api/figure-type
     *
     * @param request figureTypeGroupId: 상위 형태 id, name: 형태 하위 이름 (본문, 둘 다 필수, 이름은 100자 이하)
     *
     * @return ApiResponse<FigureTypeVO> 생성된 figureTypeId, figureTypeGroupId, name
     * @throws 404 상위 형태 없음 | 400 이름 중복, 입력값 오류
     */
    @PostMapping
    public ApiResponse<FigureTypeVO> create(@Valid @RequestBody FigureTypeVO request) {
        return ApiResponse.ok(figureTypeService.create(currentUser.getId(), request));
    }

    /**
     * 형태 하위 이름 수정 (예: 푸치)
     * [PUT] 단일 | /api/figure-type/{figureTypeId}
     *
     * @param figureTypeId 수정할 형태 하위 id (경로)
     * @param request      name: 새 이름 (본문, 필수, 100자 이하)
     *
     * @return ApiResponse<Void> 성공 시 data 없음
     * @throws 404 대상 없음 | 400 기타 수정 불가, 이름 중복, 입력값 오류
     */
    @PutMapping("/{figureTypeId}")
    public ApiResponse<Void> update(@PathVariable Long figureTypeId, @Valid @RequestBody FigureTypeVO request) {
        figureTypeService.update(currentUser.getId(), figureTypeId, request);
        return ApiResponse.ok();
    }

    /**
     * 형태 하위 삭제, 쓰던 상품은 '기타'로 이동 (예: 푸치)
     * [DELETE] 없음 | /api/figure-type/{figureTypeId}
     *
     * @param figureTypeId 삭제할 형태 하위 id (경로)
     *
     * @return ApiResponse<Void> 성공 시 data 없음
     * @throws 404 대상 없음 | 400 기타 삭제 불가, 상위의 마지막 하위 삭제 불가
     */
    @DeleteMapping("/{figureTypeId}")
    public ApiResponse<Void> delete(@PathVariable Long figureTypeId) {
        figureTypeService.delete(currentUser.getId(), figureTypeId);
        return ApiResponse.ok();
    }

    /**
     * 형태 하위 삭제 전 영향 상품 수 조회 (예: 푸치)
     * [GET] 단일 | /api/figure-type/{figureTypeId}/usage
     *
     * @param figureTypeId 조회할 형태 하위 id (경로)
     *
     * @return ApiResponse<UsageResponse> count: 이 형태 하위를 쓰는 상품 수
     * @throws 404 대상 없음
     */
    @GetMapping("/{figureTypeId}/usage")
    public ApiResponse<UsageResponse> getUsage(@PathVariable Long figureTypeId) {
        return ApiResponse.ok(figureTypeService.getUsage(currentUser.getId(), figureTypeId));
    }
}
