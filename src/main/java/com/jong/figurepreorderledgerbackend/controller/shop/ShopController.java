package com.jong.figurepreorderledgerbackend.controller.shop;

import com.jong.figurepreorderledgerbackend.common.response.ApiResponse;
import com.jong.figurepreorderledgerbackend.common.security.CurrentUser;
import com.jong.figurepreorderledgerbackend.service.shop.ShopService;
import com.jong.figurepreorderledgerbackend.vo.shop.ShopCreateRequest;
import com.jong.figurepreorderledgerbackend.vo.shop.ShopResponse;
import com.jong.figurepreorderledgerbackend.vo.shop.ShopUpdateRequest;
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
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;
    private final CurrentUser currentUser;

    /**
     * 판매처 스토어 목록 조회 (예: 굿스마일 pw)
     * [GET] 목록 | /api/shop
     *
     * @return ApiResponse<List<ShopResponse>> id, channelId(상위 판매처 채널 id), name
     */
    @GetMapping
    public ApiResponse<List<ShopResponse>> getAll() {
        return ApiResponse.ok(shopService.getAll(currentUser.getId()));
    }

    /**
     * 판매처 스토어 추가 (예: 굿스마일 pw)
     * [POST] 단일 | /api/shop
     *
     * @param request channelId: 상위 판매처 채널 id, name: 판매처 스토어 이름 (본문, 둘 다 필수, 이름은 100자 이하)
     *
     * @return ApiResponse<ShopResponse> 생성된 id, channelId, name
     * @throws 404 상위 판매처 채널 없음 | 400 이름 중복, 입력값 오류
     */
    @PostMapping
    public ApiResponse<ShopResponse> create(@Valid @RequestBody ShopCreateRequest request) {
        return ApiResponse.ok(shopService.create(currentUser.getId(), request.getChannelId(), request.getName()));
    }

    /**
     * 판매처 스토어 이름 수정 (예: 굿스마일 pw)
     * [PUT] 단일 | /api/shop/{id}
     *
     * @param id      수정할 판매처 스토어 id (경로)
     * @param request name: 새 이름 (본문, 필수, 100자 이하)
     *
     * @return ApiResponse<Void> 성공 시 data 없음
     * @throws 404 대상 없음 | 400 기타 수정 불가, 이름 중복, 입력값 오류
     */
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody ShopUpdateRequest request) {
        shopService.update(currentUser.getId(), id, request.getName());
        return ApiResponse.ok();
    }

    /**
     * 판매처 스토어 삭제, 쓰던 상품은 '기타'로 이동 (예: 굿스마일 pw)
     * [DELETE] 없음 | /api/shop/{id}
     *
     * @param id 삭제할 판매처 스토어 id (경로)
     *
     * @return ApiResponse<Void> 성공 시 data 없음
     * @throws 404 대상 없음 | 400 기타 삭제 불가, 상위의 마지막 하위 삭제 불가
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        shopService.delete(currentUser.getId(), id);
        return ApiResponse.ok();
    }

    /**
     * 판매처 스토어 삭제 전 영향 상품 수 조회 (예: 굿스마일 pw)
     * [GET] 단일 | /api/shop/{id}/usage
     *
     * @param id 조회할 판매처 스토어 id (경로)
     *
     * @return ApiResponse<UsageResponse> count: 이 판매처 스토어를 쓰는 상품 수
     * @throws 404 대상 없음
     */
    @GetMapping("/{id}/usage")
    public ApiResponse<UsageResponse> getUsage(@PathVariable Long id) {
        return ApiResponse.ok(shopService.getUsage(currentUser.getId(), id));
    }
}
