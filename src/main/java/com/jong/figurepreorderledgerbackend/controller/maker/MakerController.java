package com.jong.figurepreorderledgerbackend.controller.maker;

import com.jong.figurepreorderledgerbackend.common.response.ApiResponse;
import com.jong.figurepreorderledgerbackend.common.security.CurrentUser;
import com.jong.figurepreorderledgerbackend.service.maker.MakerService;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import com.jong.figurepreorderledgerbackend.vo.maker.MakerVO;
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
     * @return ApiResponse<List<MakerVO>> makerId, name, description, thumbnailUrl
     */
    @GetMapping
    public ApiResponse<List<MakerVO>> getAll() {
        return ApiResponse.ok(makerService.getAll(currentUser.getId()));
    }

    /**
     * 제조사 추가 (예: 세가, 후류)
     * [POST] 단일 | /api/maker
     *
     * @param request name: 제조사 이름 (본문, 필수, 100자 이하), description: 설명 (본문, 선택, 1000자 이하)
     *
     * @return ApiResponse<MakerVO> 생성된 makerId, name, description, thumbnailUrl
     * @throws 400 이름 중복, 입력값 오류
     */
    @PostMapping
    public ApiResponse<MakerVO> create(@Valid @RequestBody MakerVO request) {
        return ApiResponse.ok(makerService.create(currentUser.getId(), request));
    }

    /**
     * 제조사 이름과 설명 수정 (예: 세가, 후류)
     * [PUT] 단일 | /api/maker/{makerId}
     *
     * @param makerId 수정할 제조사 id (경로)
     * @param request name: 새 이름 (본문, 필수, 100자 이하), description: 새 설명 (본문, 선택, 1000자 이하, 생략하면 설명 삭제)
     *
     * @return ApiResponse<Void> 성공 시 data 없음
     * @throws 404 대상 없음 | 400 기타 이름 변경 불가, 이름 중복, 입력값 오류
     */
    @PutMapping("/{makerId}")
    public ApiResponse<Void> update(@PathVariable Long makerId, @Valid @RequestBody MakerVO request) {
        makerService.update(currentUser.getId(), makerId, request);
        return ApiResponse.ok();
    }

    /**
     * 제조사 삭제, 쓰던 상품은 '기타'로 이동 (예: 세가, 후류)
     * [DELETE] 없음 | /api/maker/{makerId}
     *
     * @param makerId 삭제할 제조사 id (경로)
     *
     * @return ApiResponse<Void> 성공 시 data 없음
     * @throws 404 대상 없음 | 400 기타 삭제 불가
     */
    @DeleteMapping("/{makerId}")
    public ApiResponse<Void> delete(@PathVariable Long makerId) {
        makerService.delete(currentUser.getId(), makerId);
        return ApiResponse.ok();
    }

    /**
     * 제조사 삭제 전 영향 상품 수 조회 (예: 세가, 후류)
     * [GET] 단일 | /api/maker/{makerId}/usage
     *
     * @param makerId 조회할 제조사 id (경로)
     *
     * @return ApiResponse<UsageResponse> count: 이 제조사를 쓰는 상품 수
     * @throws 404 대상 없음
     */
    @GetMapping("/{makerId}/usage")
    public ApiResponse<UsageResponse> getUsage(@PathVariable Long makerId) {
        return ApiResponse.ok(makerService.getUsage(currentUser.getId(), makerId));
    }
}
