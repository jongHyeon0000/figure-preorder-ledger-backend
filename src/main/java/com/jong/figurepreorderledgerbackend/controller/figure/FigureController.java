package com.jong.figurepreorderledgerbackend.controller.figure;

import com.jong.figurepreorderledgerbackend.common.response.ApiResponse;
import com.jong.figurepreorderledgerbackend.common.security.CurrentUser;
import com.jong.figurepreorderledgerbackend.service.figure.FigureService;
import com.jong.figurepreorderledgerbackend.service.figure.FigureStatusService;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureBulkStatusRequest;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureCreateRequest;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureDetailResponse;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureListResponse;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureStatusRequest;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/figure")
@RequiredArgsConstructor
public class FigureController {

    private final FigureService figureService;
    private final FigureStatusService figureStatusService;
    private final CurrentUser currentUser;

    /**
     * 상품 전체 목록 조회, 설명(HTML) 제외 (예: 넨도로이드 하츠네 미쿠)
     * [GET] 목록 | /api/figure
     *
     * @return ApiResponse<List<FigureListResponse>> 상품 정보와 형태(subtype), 판매처(shop), 제조사(maker), 시리즈(series), 캐릭터(characters, 장르 포함), 최신 등록순
     */
    @GetMapping
    public ApiResponse<List<FigureListResponse>> getList() {
        return ApiResponse.ok(figureService.getList(currentUser.getId()));
    }

    /**
     * 상품 상세 조회, 설명(HTML) 포함 (예: 넨도로이드 하츠네 미쿠)
     * [GET] 단일 | /api/figure/{id}
     *
     * @param id 조회할 상품 id (경로)
     *
     * @return ApiResponse<FigureDetailResponse> 목록 항목의 모든 정보 + description, createdAt, updatedAt
     * @throws 404 대상 없음
     */
    @GetMapping("/{id}")
    public ApiResponse<FigureDetailResponse> getDetail(@PathVariable Long id) {
        return ApiResponse.ok(figureService.getDetail(currentUser.getId(), id));
    }

    /**
     * 상품 등록 (예: 넨도로이드 하츠네 미쿠)
     * [POST] 단일 | /api/figure
     *
     * @param request name: 상품명, price: 개당 가격, quantity: 수량, subtypeId: 형태 하위 id, shopId: 판매처 스토어 id, makerId: 제조사 id,
     *                seriesIds: 시리즈 id 목록(1개 이상), characterIds: 캐릭터 id 목록(1개 이상) (본문, 필수),
     *                status: CART 또는 UNPURCHASED (생략 시 CART), deliveryMonth, reservationDeadline, purchaseLink, thumbnailUrl, description (본문, 선택)
     *
     * @return ApiResponse<FigureListResponse> 등록된 상품 한 건 (목록 항목 모양)
     * @throws 404 분류 id가 없거나 다른 사용자의 것 | 400 허용되지 않는 등록 상태, 입력값 오류
     */
    @PostMapping
    public ApiResponse<FigureListResponse> create(@Valid @RequestBody FigureCreateRequest request) {
        return ApiResponse.ok(figureService.create(currentUser.getId(), request));
    }

    /**
     * 상품 수정, 상태와 결제 방식은 바꾸지 않음 (예: 넨도로이드 하츠네 미쿠)
     * [PUT] 단일 | /api/figure/{id}
     *
     * @param id      수정할 상품 id (경로)
     * @param request 등록과 같은 항목에서 status 제외 (본문)
     *
     * @return ApiResponse<FigureListResponse> 수정된 상품 한 건 (목록 항목 모양)
     * @throws 404 대상 없음, 분류 id가 없거나 다른 사용자의 것 | 400 입력값 오류
     */
    @PutMapping("/{id}")
    public ApiResponse<FigureListResponse> update(@PathVariable Long id, @Valid @RequestBody FigureUpdateRequest request) {
        return ApiResponse.ok(figureService.update(currentUser.getId(), id, request));
    }

    /**
     * 상품 상태 전이 (예: 미구매 → 전액 결제 구매완료)
     * [PATCH] 단일 | /api/figure/{id}/status
     *
     * @param id      전이할 상품 id (경로)
     * @param request status: 다음 상태 (본문, 필수),
     *                paymentType: 미구매에서 넘길 때만 (RESERVED=DEPOSIT, PURCHASED=FULL),
     *                depositAmount: RESERVED 전이에서 필수, depositPaidAt, balanceDueDate, paidAt, deliveredAt: 생략 시 오늘 날짜 (본문, 선택)
     *
     * @return ApiResponse<FigureListResponse> 전이된 상품 한 건 (목록 항목 모양)
     * @throws 404 대상 없음 | 400 허용되지 않는 전이, 결제 방식 불일치, 예약금 누락, 입력값 오류
     */
    @PatchMapping("/{id}/status")
    public ApiResponse<FigureListResponse> changeStatus(@PathVariable Long id, @Valid @RequestBody FigureStatusRequest request) {
        return ApiResponse.ok(figureStatusService.change(currentUser.getId(), id, request));
    }

    /**
     * 상품 일괄 상태 전이, 전부 성공 또는 전부 실패 (예: 선택한 상품 모두 전액 결제)
     * [PATCH] 목록 | /api/figure/status
     *
     * @param request ids: 상품 id 목록, status: 다음 상태 (본문, 필수), paymentType: 미구매에서 넘길 때만, paidAt, deliveredAt: 생략 시 오늘 날짜 (본문, 선택)
     *
     * @return ApiResponse<List<Long>> 상태가 바뀐 상품 id 목록
     * @throws 400 하나라도 전이할 수 없음(없는 id, 다른 사용자의 상품 포함, 메시지에 문제 id), 예약금 결제(RESERVED)는 일괄 불가, 입력값 오류
     */
    @PatchMapping("/status")
    public ApiResponse<List<Long>> changeStatusAll(@Valid @RequestBody FigureBulkStatusRequest request) {
        return ApiResponse.ok(figureStatusService.changeAll(currentUser.getId(), request));
    }

    /**
     * 상품 삭제 (예: 넨도로이드 하츠네 미쿠)
     * [DELETE] 없음 | /api/figure/{id}
     *
     * @param id 삭제할 상품 id (경로)
     *
     * @return ApiResponse<Void> 성공 시 data 없음
     * @throws 404 대상 없음
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        figureService.delete(currentUser.getId(), id);
        return ApiResponse.ok();
    }

    /**
     * 상품 일괄 삭제, 전부 성공 또는 전부 실패 (예: 선택한 상품 모두 삭제)
     * [DELETE] 없음 | /api/figure?ids=1,2,3
     *
     * @param ids 삭제할 상품 id 목록 (쿼리, 필수)
     *
     * @return ApiResponse<Void> 성공 시 data 없음
     * @throws 404 하나라도 없는 상품(아무것도 삭제하지 않음) | 400 ids 누락
     */
    @DeleteMapping
    public ApiResponse<Void> deleteAll(@RequestParam List<Long> ids) {
        figureService.deleteAll(currentUser.getId(), ids);
        return ApiResponse.ok();
    }
}
