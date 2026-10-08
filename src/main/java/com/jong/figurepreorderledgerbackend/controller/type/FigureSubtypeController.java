package com.jong.figurepreorderledgerbackend.controller.type;

import com.jong.figurepreorderledgerbackend.common.response.ApiResponse;
import com.jong.figurepreorderledgerbackend.common.security.CurrentUser;
import com.jong.figurepreorderledgerbackend.service.type.FigureSubtypeService;
import com.jong.figurepreorderledgerbackend.vo.type.FigureSubtypeCreateRequest;
import com.jong.figurepreorderledgerbackend.vo.type.FigureSubtypeResponse;
import com.jong.figurepreorderledgerbackend.vo.type.FigureSubtypeUpdateRequest;
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
@RequestMapping("/api/figure-subtype")
@RequiredArgsConstructor
public class FigureSubtypeController {

    private final FigureSubtypeService figureSubtypeService;
    private final CurrentUser currentUser;

    /**
     * 형태 하위 목록 조회 (예: 푸치)
     * [GET] 목록 | /api/figure-subtype
     *
     * @return ApiResponse<List<FigureSubtypeResponse>> id, typeId(상위 형태 id), name
     */
    @GetMapping
    public ApiResponse<List<FigureSubtypeResponse>> getAll() {
        return ApiResponse.ok(figureSubtypeService.getAll(currentUser.getId()));
    }

    /**
     * 형태 하위 추가 (예: 푸치)
     * [POST] 단일 | /api/figure-subtype
     *
     * @param request typeId: 상위 형태 id, name: 형태 하위 이름 (본문, 둘 다 필수, 이름은 100자 이하)
     *
     * @return ApiResponse<FigureSubtypeResponse> 생성된 id, typeId, name
     * @throws 404 상위 형태 없음 | 400 이름 중복, 입력값 오류
     */
    @PostMapping
    public ApiResponse<FigureSubtypeResponse> create(@Valid @RequestBody FigureSubtypeCreateRequest request) {
        return ApiResponse.ok(figureSubtypeService.create(currentUser.getId(), request.getTypeId(), request.getName()));
    }

    /**
     * 형태 하위 이름 수정 (예: 푸치)
     * [PUT] 단일 | /api/figure-subtype/{id}
     *
     * @param id      수정할 형태 하위 id (경로)
     * @param request name: 새 이름 (본문, 필수, 100자 이하)
     *
     * @return ApiResponse<Void> 성공 시 data 없음
     * @throws 404 대상 없음 | 400 기타 수정 불가, 이름 중복, 입력값 오류
     */
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody FigureSubtypeUpdateRequest request) {
        figureSubtypeService.update(currentUser.getId(), id, request.getName());
        return ApiResponse.ok();
    }

    /**
     * 형태 하위 삭제, 쓰던 상품은 '기타'로 이동 (예: 푸치)
     * [DELETE] 없음 | /api/figure-subtype/{id}
     *
     * @param id 삭제할 형태 하위 id (경로)
     *
     * @return ApiResponse<Void> 성공 시 data 없음
     * @throws 404 대상 없음 | 400 기타 삭제 불가, 상위의 마지막 하위 삭제 불가
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        figureSubtypeService.delete(currentUser.getId(), id);
        return ApiResponse.ok();
    }

    /**
     * 형태 하위 삭제 전 영향 상품 수 조회 (예: 푸치)
     * [GET] 단일 | /api/figure-subtype/{id}/usage
     *
     * @param id 조회할 형태 하위 id (경로)
     *
     * @return ApiResponse<UsageResponse> count: 이 형태 하위를 쓰는 상품 수
     * @throws 404 대상 없음
     */
    @GetMapping("/{id}/usage")
    public ApiResponse<UsageResponse> getUsage(@PathVariable Long id) {
        return ApiResponse.ok(figureSubtypeService.getUsage(currentUser.getId(), id));
    }
}
