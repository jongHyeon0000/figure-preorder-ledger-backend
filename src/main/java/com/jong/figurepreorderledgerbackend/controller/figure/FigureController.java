package com.jong.figurepreorderledgerbackend.controller.figure;

import com.jong.figurepreorderledgerbackend.common.response.ApiResponse;
import com.jong.figurepreorderledgerbackend.common.security.CurrentUser;
import com.jong.figurepreorderledgerbackend.service.figure.FigureService;
import com.jong.figurepreorderledgerbackend.service.figure.FigureStatusService;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureResponseVO;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureSaveRequestVO;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureStatusRequestVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "상품", description = "상품(figure) 목록, 상세, 등록, 수정, 상태 전이, 삭제. 모든 조회와 변경은 현재 사용자의 상품만 대상으로 한다.")
@RestController
@RequestMapping("/api/figure")
@RequiredArgsConstructor
public class FigureController {

    private final FigureService figureService;
    private final FigureStatusService figureStatusService;
    private final CurrentUser currentUser;

    @Operation(
            summary = "상품 전체 목록 조회, 설명(HTML) 제외 (예: 넨도로이드 하츠네 미쿠)",
            description = "현재 사용자의 상품 전체를 **최신 등록순**으로 돌려준다. 필터와 정렬은 프론트가 처리한다.\n\n"
                    + "각 항목에는 상품 정보와 형태(`figureType`), 판매처(`shop`), 제조사(`maker`), 시리즈(`series`), "
                    + "캐릭터(`characters`, 상위 장르 포함)가 들어 있다. 상세 설명(`description`)은 제외한다.")
    @GetMapping
    public ApiResponse<List<FigureResponseVO>> getList() {
        return ApiResponse.ok("조회가 완료되었습니다.", figureService.getList(currentUser.getId()));
    }

    @Operation(
            summary = "상품 상세 조회, 설명(HTML) 포함 (예: 넨도로이드 하츠네 미쿠)",
            description = "목록 항목의 모든 정보에 상세 설명(`description`), 생성일(`createdAt`), 수정일(`updatedAt`)이 더해진다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 한 건 (설명 포함)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    @GetMapping("/{figureId}")
    public ApiResponse<FigureResponseVO> getDetail(
            @Parameter(description = "조회할 상품 id", example = "12") @PathVariable Long figureId) {
        return ApiResponse.ok("조회가 완료되었습니다.", figureService.getDetail(currentUser.getId(), figureId));
    }

    @Operation(
            summary = "상품 등록 (예: 넨도로이드 하츠네 미쿠)",
            description = "등록된 상품 한 건을 돌려준다(목록 항목 모양).\n\n"
                    + "- 등록 때 상태는 `CART` 또는 `UNPURCHASED`만 허용한다(생략하면 `CART`). 결제 방식은 `null`로 시작한다.\n"
                    + "- `seriesIds`, `characterIds`는 1개 이상 필수다. 모든 분류 id는 현재 사용자의 것이어야 한다.\n"
                    + "- 설명(`description`) HTML은 원본 그대로 저장한다. 설명에 base64 이미지(`<img src=\"data:...\">`)가 있으면 400이다.\n"
                    + "- 썸네일은 본문으로 받지 않는다. 상품을 저장한 뒤 썸네일 업로드 API로 올린다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "등록 예",
                    value = "{\"name\":\"넨도로이드 하츠네 미쿠\",\"price\":6800,\"quantity\":1,"
                            + "\"figureTypeId\":3,\"shopId\":5,\"makerId\":2,"
                            + "\"seriesIds\":[1],\"characterIds\":[4],\"status\":\"UNPURCHASED\","
                            + "\"deliveryMonth\":\"2026-12-01\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "등록된 상품 한 건 (목록 항목 모양)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "허용되지 않는 등록 상태, 입력값 오류"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "분류 id가 없거나 다른 사용자의 것")
    })
    @PostMapping
    public ApiResponse<FigureResponseVO> create(@Valid @RequestBody FigureSaveRequestVO request) {
        return ApiResponse.ok("저장이 완료되었습니다.", figureService.create(currentUser.getId(), request));
    }

    @Operation(
            summary = "상품 수정, 상태와 결제 방식은 바꾸지 않음 (예: 넨도로이드 하츠네 미쿠)",
            description = "등록과 같은 항목에서 `status`를 제외하고 보낸다(보내도 무시한다). 수정된 상품 한 건을 돌려준다(목록 항목 모양).\n\n"
                    + "상태와 결제 방식은 상태 전이 API로만 바꿀 수 있다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정된 상품 한 건 (목록 항목 모양)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 오류"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음, 분류 id가 없거나 다른 사용자의 것")
    })
    @PutMapping("/{figureId}")
    public ApiResponse<FigureResponseVO> update(
            @Parameter(description = "수정할 상품 id", example = "12") @PathVariable Long figureId,
            @Valid @RequestBody FigureSaveRequestVO request) {
        return ApiResponse.ok("수정이 완료되었습니다.", figureService.update(currentUser.getId(), figureId, request));
    }

    @Operation(
            summary = "상품 상태 전이 (예: 미구매 → 전액 결제 구매완료)",
            description = "상품 한 건의 상태를 다음 단계로 바꾸고, 바뀐 상품 한 건을 돌려준다(목록 항목 모양).\n\n"
                    + "**허용 전이**\n\n"
                    + "| 현재 | 다음 | 결제 방식 |\n"
                    + "|---|---|---|\n"
                    + "| CART | UNPURCHASED | - |\n"
                    + "| UNPURCHASED | PURCHASED | FULL (전액) |\n"
                    + "| UNPURCHASED | RESERVED | DEPOSIT (예약금) |\n"
                    + "| RESERVED | PURCHASED | - |\n"
                    + "| PURCHASED | DELIVERED | - |\n\n"
                    + "- 되돌리는 전이는 400이다(실수는 삭제 후 재등록).\n"
                    + "- `paymentType`은 미구매에서 넘길 때만 받는다(RESERVED는 DEPOSIT, PURCHASED는 FULL과만 짝이 맞는다).\n"
                    + "- `depositAmount`는 RESERVED 전이에서 필수다.\n"
                    + "- `depositPaidAt`, `balanceDueDate`, `paidAt`, `deliveredAt`을 생략하면 서버의 오늘 날짜를 쓴다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "전액 결제",
                    value = "{\"status\":\"PURCHASED\",\"paymentType\":\"FULL\",\"paidAt\":\"2026-10-08\"}"),
            @ExampleObject(name = "예약금 결제",
                    value = "{\"status\":\"RESERVED\",\"paymentType\":\"DEPOSIT\",\"depositAmount\":20000,"
                            + "\"depositPaidAt\":\"2026-10-08\",\"balanceDueDate\":\"2026-12-01\"}"),
            @ExampleObject(name = "잔금 결제", value = "{\"status\":\"PURCHASED\",\"paidAt\":\"2026-12-01\"}"),
            @ExampleObject(name = "배송 완료", value = "{\"status\":\"DELIVERED\",\"deliveredAt\":\"2027-01-10\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "전이된 상품 한 건 (목록 항목 모양)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "허용되지 않는 전이, 결제 방식 불일치, 예약금 누락, 입력값 오류"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    @PatchMapping("/{figureId}/status")
    public ApiResponse<FigureResponseVO> changeStatus(
            @Parameter(description = "전이할 상품 id", example = "12") @PathVariable Long figureId,
            @Valid @RequestBody FigureStatusRequestVO request) {
        return ApiResponse.ok("상태 변경이 완료되었습니다.", figureStatusService.change(currentUser.getId(), figureId, request));
    }

    @Operation(
            summary = "상품 일괄 상태 전이, 전부 성공 또는 전부 실패 (예: 선택한 상품 모두 전액 결제)",
            description = "`figureIds`로 고른 상품 전부를 같은 상태로 바꾼다. 상태가 바뀐 상품 id 목록을 돌려준다.\n\n"
                    + "- 하나라도 전이할 수 없으면(허용되지 않는 전이, 없는 id, 다른 사용자의 상품) **아무것도 바꾸지 않고** 400이며, 메시지에 문제가 된 id를 담는다. 한 트랜잭션이다.\n"
                    + "- 예약금 결제(RESERVED)는 상품마다 값이 달라서 일괄 대상이 아니다(개별 전이를 쓴다).\n"
                    + "- 전이 규칙은 단건 상태 전이와 같다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "일괄 전액 결제",
                    value = "{\"figureIds\":[1,2,3],\"status\":\"PURCHASED\",\"paymentType\":\"FULL\",\"paidAt\":\"2026-10-08\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상태가 바뀐 상품 id 목록"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "하나라도 전이할 수 없음(없는 id, 다른 사용자의 상품 포함), 예약금 결제(RESERVED)는 일괄 불가, 입력값 오류")
    })
    @PatchMapping("/status")
    public ApiResponse<List<Long>> changeStatusAll(@Valid @RequestBody FigureStatusRequestVO request) {
        return ApiResponse.ok("상태 변경이 완료되었습니다.", figureStatusService.changeAll(currentUser.getId(), request));
    }

    @Operation(
            summary = "상품 삭제 (예: 넨도로이드 하츠네 미쿠)",
            description = "성공하면 `data` 없이 `code: OK`만 돌려준다. 상품의 썸네일 행과 파일도 함께 정리한다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 시 data 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    @DeleteMapping("/{figureId}")
    public ApiResponse<Void> delete(
            @Parameter(description = "삭제할 상품 id", example = "12") @PathVariable Long figureId) {
        figureService.delete(currentUser.getId(), figureId);
        return ApiResponse.ok("삭제가 완료되었습니다.");
    }

    @Operation(
            summary = "상품 일괄 삭제, 전부 성공 또는 전부 실패 (예: 선택한 상품 모두 삭제)",
            description = "하나라도 없는 상품이면 아무것도 삭제하지 않고 404다. 성공하면 `data` 없이 `code: OK`만 돌려준다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 시 data 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "figureIds 누락"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "하나라도 없는 상품(아무것도 삭제하지 않음)")
    })
    @DeleteMapping
    public ApiResponse<Void> deleteAll(
            @Parameter(description = "삭제할 상품 id 목록 (쉼표로 구분)", example = "1,2,3", required = true)
            @RequestParam List<Long> figureIds) {
        figureService.deleteAll(currentUser.getId(), figureIds);
        return ApiResponse.ok("삭제가 완료되었습니다.");
    }
}
