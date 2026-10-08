package com.jong.figurepreorderledgerbackend.controller.maker;

import com.jong.figurepreorderledgerbackend.common.response.ApiResponse;
import com.jong.figurepreorderledgerbackend.common.security.CurrentUser;
import com.jong.figurepreorderledgerbackend.service.maker.MakerService;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import com.jong.figurepreorderledgerbackend.vo.maker.MakerRequest;
import com.jong.figurepreorderledgerbackend.vo.maker.MakerResponse;
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
@RequestMapping("/api/maker")
@RequiredArgsConstructor
public class MakerController {

    private final MakerService makerService;
    private final CurrentUser currentUser;

    /**
     * 제조사 목록 조회 (예: 세가, 후류)
     * [GET] 목록 | /api/maker
     *
     * @return ApiResponse<List<MakerResponse>> id, name
     */
    @GetMapping
    public ApiResponse<List<MakerResponse>> getAll() {
        return ApiResponse.ok(makerService.getAll(currentUser.getId()));
    }

    /**
     * 제조사 추가 (예: 세가, 후류)
     * [POST] 단일 | /api/maker
     *
     * @param request name: 제조사 이름 (본문, 필수, 100자 이하)
     *
     * @return ApiResponse<MakerResponse> 생성된 id, name
     * @throws 400 이름 중복, 입력값 오류
     */
    @PostMapping
    public ApiResponse<MakerResponse> create(@Valid @RequestBody MakerRequest request) {
        return ApiResponse.ok(makerService.create(currentUser.getId(), request.getName()));
    }

    /**
     * 제조사 이름 수정 (예: 세가, 후류)
     * [PUT] 단일 | /api/maker/{id}
     *
     * @param id      수정할 제조사 id (경로)
     * @param request name: 새 이름 (본문, 필수, 100자 이하)
     *
     * @return ApiResponse<Void> 성공 시 data 없음
     * @throws 404 대상 없음 | 400 기타 수정 불가, 이름 중복, 입력값 오류
     */
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody MakerRequest request) {
        makerService.update(currentUser.getId(), id, request.getName());
        return ApiResponse.ok();
    }

    /**
     * 제조사 삭제, 쓰던 상품은 '기타'로 이동 (예: 세가, 후류)
     * [DELETE] 없음 | /api/maker/{id}
     *
     * @param id 삭제할 제조사 id (경로)
     *
     * @return ApiResponse<Void> 성공 시 data 없음
     * @throws 404 대상 없음 | 400 기타 삭제 불가
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        makerService.delete(currentUser.getId(), id);
        return ApiResponse.ok();
    }

    /**
     * 제조사 삭제 전 영향 상품 수 조회 (예: 세가, 후류)
     * [GET] 단일 | /api/maker/{id}/usage
     *
     * @param id 조회할 제조사 id (경로)
     *
     * @return ApiResponse<UsageResponse> count: 이 제조사를 쓰는 상품 수
     * @throws 404 대상 없음
     */
    @GetMapping("/{id}/usage")
    public ApiResponse<UsageResponse> getUsage(@PathVariable Long id) {
        return ApiResponse.ok(makerService.getUsage(currentUser.getId(), id));
    }
}
