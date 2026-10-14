package com.jong.figurepreorderledgerbackend.controller.shop;

import com.jong.figurepreorderledgerbackend.common.response.ApiResponse;
import com.jong.figurepreorderledgerbackend.common.security.CurrentUser;
import com.jong.figurepreorderledgerbackend.service.shop.ShopGroupService;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import com.jong.figurepreorderledgerbackend.vo.shop.ShopGroupVO;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "판매처 채널", description = "판매처 상위 분류(예: 네이버 스토어). 추가하면 기본 하위 '기타'가 함께 만들어진다.")
@RestController
@RequestMapping("/api/shop-group")
@RequiredArgsConstructor
public class ShopGroupController {

    private final ShopGroupService shopGroupService;
    private final CurrentUser currentUser;

    @Operation(
            summary = "판매처 채널 목록 조회, 하위 판매처 스토어 중첩 (예: 네이버 스토어)",
            description = "응답: id, name, description, thumbnailUrl, children(판매처 스토어 목록: id, groupId, name, description, thumbnailUrl)")
    @GetMapping
    public ApiResponse<List<ShopGroupVO>> getTree() {
        return ApiResponse.ok("조회가 완료되었습니다.", shopGroupService.getTree(currentUser.getId()));
    }

    @Operation(
            summary = "판매처 채널 추가, 기본 하위 '기타' 자동 생성 (예: 네이버 스토어)",
            description = "요청 본문: name: 판매처 채널 이름 (본문, 필수, 100자 이하), description: 설명 (본문, 선택, 1000자 이하)\n\n"
                    + "응답: 생성된 id, name, description, thumbnailUrl, children(기본 '기타' 하나)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "추가 예",
                    value = "{\"name\":\"네이버 스토어\",\"description\":\"설명(선택)\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "생성된 id, name, description, thumbnailUrl, children(기본 '기타' 하나)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이름 중복, 입력값 오류")
    })
    @PostMapping
    public ApiResponse<ShopGroupVO> create(@Valid @RequestBody ShopGroupVO request) {
        return ApiResponse.ok("저장이 완료되었습니다.", shopGroupService.create(currentUser.getId(), request));
    }

    @Operation(
            summary = "판매처 채널 이름과 설명 수정 (예: 네이버 스토어)",
            description = "요청 본문: name: 새 이름 (본문, 필수, 100자 이하), description: 새 설명 (본문, 선택, 1000자 이하, 생략하면 설명 삭제)\n\n"
                    + "응답: 성공 시 data 없음")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "수정 예",
                    value = "{\"name\":\"새 이름\",\"description\":\"새 설명(생략하면 설명 삭제)\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 시 data 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "기타 이름 변경 불가, 이름 중복, 입력값 오류"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    @PutMapping("/{shopGroupId}")
    public ApiResponse<Void> update(
            @Parameter(description = "수정할 판매처 채널 id", example = "1") @PathVariable Long shopGroupId,
            @Valid @RequestBody ShopGroupVO request) {
        shopGroupService.update(currentUser.getId(), shopGroupId, request);
        return ApiResponse.ok("수정이 완료되었습니다.");
    }

    @Operation(
            summary = "판매처 채널 삭제, 하위와 쓰던 상품은 '기타'로 이동 (예: 네이버 스토어)",
            description = "응답: 성공 시 data 없음")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 시 data 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "기타 삭제 불가"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    @DeleteMapping("/{shopGroupId}")
    public ApiResponse<Void> delete(
            @Parameter(description = "삭제할 판매처 채널 id", example = "1") @PathVariable Long shopGroupId) {
        shopGroupService.delete(currentUser.getId(), shopGroupId);
        return ApiResponse.ok("삭제가 완료되었습니다.");
    }

    @Operation(
            summary = "판매처 채널 삭제 전 영향 상품 수 조회 (예: 네이버 스토어)",
            description = "응답: count: 이 판매처 채널의 하위를 쓰는 상품 수")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "count: 이 판매처 채널의 하위를 쓰는 상품 수"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    @GetMapping("/{shopGroupId}/usage")
    public ApiResponse<UsageResponse> getUsage(
            @Parameter(description = "조회할 판매처 채널 id", example = "1") @PathVariable Long shopGroupId) {
        return ApiResponse.ok("조회가 완료되었습니다.", shopGroupService.getUsage(currentUser.getId(), shopGroupId));
    }
}
