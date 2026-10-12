package com.jong.figurepreorderledgerbackend.controller.shop;

import com.jong.figurepreorderledgerbackend.common.response.ApiResponse;
import com.jong.figurepreorderledgerbackend.common.security.CurrentUser;
import com.jong.figurepreorderledgerbackend.service.shop.ShopGroupService;
import com.jong.figurepreorderledgerbackend.vo.shop.ShopGroupRequest;
import com.jong.figurepreorderledgerbackend.vo.shop.ShopGroupResponse;
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
@RequestMapping("/api/shop-group")
@RequiredArgsConstructor
public class ShopGroupController {

    private final ShopGroupService shopGroupService;
    private final CurrentUser currentUser;

    /**
     * 판매처 채널 목록 조회, 하위 판매처 스토어 중첩 (예: 네이버 스토어)
     * [GET] 목록 | /api/shop-group
     *
     * @return ApiResponse<List<ShopGroupResponse>> shopGroupId, name, children(판매처 스토어 목록: shopId, shopGroupId, name)
     */
    @GetMapping
    public ApiResponse<List<ShopGroupResponse>> getTree() {
        return ApiResponse.ok(shopGroupService.getTree(currentUser.getId()));
    }

    /**
     * 판매처 채널 추가, 기본 하위 '기타' 자동 생성 (예: 네이버 스토어)
     * [POST] 단일 | /api/shop-group
     *
     * @param request name: 판매처 채널 이름 (본문, 필수, 100자 이하)
     *
     * @return ApiResponse<ShopGroupResponse> 생성된 shopGroupId, name, children(기본 '기타' 하나)
     * @throws 400 이름 중복, 입력값 오류
     */
    @PostMapping
    public ApiResponse<ShopGroupResponse> create(@Valid @RequestBody ShopGroupRequest request) {
        return ApiResponse.ok(shopGroupService.create(currentUser.getId(), request.getName()));
    }

    /**
     * 판매처 채널 이름 수정 (예: 네이버 스토어)
     * [PUT] 단일 | /api/shop-group/{shopGroupId}
     *
     * @param shopGroupId 수정할 판매처 채널 id (경로)
     * @param request     name: 새 이름 (본문, 필수, 100자 이하)
     *
     * @return ApiResponse<Void> 성공 시 data 없음
     * @throws 404 대상 없음 | 400 기타 수정 불가, 이름 중복, 입력값 오류
     */
    @PutMapping("/{shopGroupId}")
    public ApiResponse<Void> update(@PathVariable Long shopGroupId, @Valid @RequestBody ShopGroupRequest request) {
        shopGroupService.update(currentUser.getId(), shopGroupId, request.getName());
        return ApiResponse.ok();
    }

    /**
     * 판매처 채널 삭제, 하위와 쓰던 상품은 '기타'로 이동 (예: 네이버 스토어)
     * [DELETE] 없음 | /api/shop-group/{shopGroupId}
     *
     * @param shopGroupId 삭제할 판매처 채널 id (경로)
     *
     * @return ApiResponse<Void> 성공 시 data 없음
     * @throws 404 대상 없음 | 400 기타 삭제 불가
     */
    @DeleteMapping("/{shopGroupId}")
    public ApiResponse<Void> delete(@PathVariable Long shopGroupId) {
        shopGroupService.delete(currentUser.getId(), shopGroupId);
        return ApiResponse.ok();
    }

    /**
     * 판매처 채널 삭제 전 영향 상품 수 조회 (예: 네이버 스토어)
     * [GET] 단일 | /api/shop-group/{shopGroupId}/usage
     *
     * @param shopGroupId 조회할 판매처 채널 id (경로)
     *
     * @return ApiResponse<UsageResponse> count: 이 판매처 채널의 하위를 쓰는 상품 수
     * @throws 404 대상 없음
     */
    @GetMapping("/{shopGroupId}/usage")
    public ApiResponse<UsageResponse> getUsage(@PathVariable Long shopGroupId) {
        return ApiResponse.ok(shopGroupService.getUsage(currentUser.getId(), shopGroupId));
    }
}
