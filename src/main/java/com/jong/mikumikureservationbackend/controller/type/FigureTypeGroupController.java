package com.jong.mikumikureservationbackend.controller.type;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.common.security.CurrentUser;
import com.jong.mikumikureservationbackend.service.type.FigureTypeGroupService;
import com.jong.mikumikureservationbackend.vo.common.UsageResponse;
import com.jong.mikumikureservationbackend.vo.type.FigureTypeGroupVO;
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

@Tag(name = "형태", description = "형태 상위 분류(예: 스케일, 넨도로이드). 추가하면 기본 하위 '기타'가 함께 만들어진다.")
@RestController
@RequestMapping("/api/figure-type-group")
@RequiredArgsConstructor
public class FigureTypeGroupController {

    private final FigureTypeGroupService figureTypeGroupService;
    private final CurrentUser currentUser;

    @Operation(
            summary = "형태 목록 조회, 하위 형태 하위 중첩 (예: 스케일, 넨도로이드)",
            description = "응답: id, name, description, thumbnailUrl, children(형태 하위 목록: id, groupId, name, description, thumbnailUrl)")
    @GetMapping
    public ApiResponse<List<FigureTypeGroupVO>> getTree() {
        return ApiResponse.ok("조회가 완료되었습니다.", figureTypeGroupService.getTree(currentUser.getId()));
    }

    @Operation(
            summary = "형태 추가, 기본 하위 '기타' 자동 생성 (예: 스케일, 넨도로이드)",
            description = "요청 본문: name: 형태 이름 (본문, 필수, 100자 이하), description: 설명 (본문, 선택, 1000자 이하)\n\n"
                    + "응답: 생성된 id, name, description, thumbnailUrl, children(기본 '기타' 하나)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "추가 예",
                    value = "{\"name\":\"스케일\",\"description\":\"설명(선택)\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "생성된 id, name, description, thumbnailUrl, children(기본 '기타' 하나)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이름 중복, 입력값 오류")
    })
    @PostMapping
    public ApiResponse<FigureTypeGroupVO> create(@Valid @RequestBody FigureTypeGroupVO request) {
        return ApiResponse.ok("저장이 완료되었습니다.", figureTypeGroupService.create(currentUser.getId(), request));
    }

    @Operation(
            summary = "형태 이름과 설명 수정 (예: 스케일, 넨도로이드)",
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
    @PutMapping("/{figureTypeGroupId}")
    public ApiResponse<Void> update(
            @Parameter(description = "수정할 형태 id", example = "1") @PathVariable Long figureTypeGroupId,
            @Valid @RequestBody FigureTypeGroupVO request) {
        figureTypeGroupService.update(currentUser.getId(), figureTypeGroupId, request);
        return ApiResponse.ok("수정이 완료되었습니다.");
    }

    @Operation(
            summary = "형태 삭제, 하위와 쓰던 상품은 '기타'로 이동 (예: 스케일, 넨도로이드)",
            description = "응답: 성공 시 data 없음")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 시 data 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "기타 삭제 불가"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    @DeleteMapping("/{figureTypeGroupId}")
    public ApiResponse<Void> delete(
            @Parameter(description = "삭제할 형태 id", example = "1") @PathVariable Long figureTypeGroupId) {
        figureTypeGroupService.delete(currentUser.getId(), figureTypeGroupId);
        return ApiResponse.ok("삭제가 완료되었습니다.");
    }

    @Operation(
            summary = "형태 삭제 전 영향 상품 수 조회 (예: 스케일, 넨도로이드)",
            description = "응답: count: 이 형태의 하위를 쓰는 상품 수")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "count: 이 형태의 하위를 쓰는 상품 수"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    @GetMapping("/{figureTypeGroupId}/usage")
    public ApiResponse<UsageResponse> getUsage(
            @Parameter(description = "조회할 형태 id", example = "1") @PathVariable Long figureTypeGroupId) {
        return ApiResponse.ok("조회가 완료되었습니다.", figureTypeGroupService.getUsage(currentUser.getId(), figureTypeGroupId));
    }
}
