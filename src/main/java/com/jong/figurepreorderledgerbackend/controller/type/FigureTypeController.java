package com.jong.figurepreorderledgerbackend.controller.type;

import com.jong.figurepreorderledgerbackend.common.response.ApiResponse;
import com.jong.figurepreorderledgerbackend.common.security.CurrentUser;
import com.jong.figurepreorderledgerbackend.service.type.FigureTypeService;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import com.jong.figurepreorderledgerbackend.vo.type.FigureTypeVO;
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

@Tag(name = "형태 하위", description = "형태 하위 분류(예: 푸치). 상품은 이 단위로 고른다.")
@RestController
@RequestMapping("/api/figure-type")
@RequiredArgsConstructor
public class FigureTypeController {

    private final FigureTypeService figureTypeService;
    private final CurrentUser currentUser;

    @Operation(
            summary = "형태 하위 목록 조회 (예: 푸치)",
            description = "응답: id, groupId(상위 형태 id), name, description, thumbnailUrl")
    @GetMapping
    public ApiResponse<List<FigureTypeVO>> getAll() {
        return ApiResponse.ok("조회가 완료되었습니다.", figureTypeService.getAll(currentUser.getId()));
    }

    @Operation(
            summary = "형태 하위 추가 (예: 푸치)",
            description = "요청 본문: groupId: 상위 형태 id, name: 형태 하위 이름 (본문, 둘 다 필수, 이름은 100자 이하), description: 설명 (본문, 선택, 1000자 이하)\n\n"
                    + "응답: 생성된 id, groupId, name, description, thumbnailUrl")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "추가 예",
                    value = "{\"groupId\":1,\"name\":\"푸치\",\"description\":\"설명(선택)\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "생성된 id, groupId, name, description, thumbnailUrl"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이름 중복, 입력값 오류"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "상위 형태 없음")
    })
    @PostMapping
    public ApiResponse<FigureTypeVO> create(@Valid @RequestBody FigureTypeVO request) {
        return ApiResponse.ok("저장이 완료되었습니다.", figureTypeService.create(currentUser.getId(), request));
    }

    @Operation(
            summary = "형태 하위 이름과 설명 수정 (예: 푸치)",
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
    @PutMapping("/{figureTypeId}")
    public ApiResponse<Void> update(
            @Parameter(description = "수정할 형태 하위 id", example = "1") @PathVariable Long figureTypeId,
            @Valid @RequestBody FigureTypeVO request) {
        figureTypeService.update(currentUser.getId(), figureTypeId, request);
        return ApiResponse.ok("수정이 완료되었습니다.");
    }

    @Operation(
            summary = "형태 하위 삭제, 쓰던 상품은 '기타'로 이동 (예: 푸치)",
            description = "응답: 성공 시 data 없음")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 시 data 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "기타 삭제 불가, 상위의 마지막 하위 삭제 불가"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    @DeleteMapping("/{figureTypeId}")
    public ApiResponse<Void> delete(
            @Parameter(description = "삭제할 형태 하위 id", example = "1") @PathVariable Long figureTypeId) {
        figureTypeService.delete(currentUser.getId(), figureTypeId);
        return ApiResponse.ok("삭제가 완료되었습니다.");
    }

    @Operation(
            summary = "형태 하위 삭제 전 영향 상품 수 조회 (예: 푸치)",
            description = "응답: count: 이 형태 하위를 쓰는 상품 수")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "count: 이 형태 하위를 쓰는 상품 수"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    @GetMapping("/{figureTypeId}/usage")
    public ApiResponse<UsageResponse> getUsage(
            @Parameter(description = "조회할 형태 하위 id", example = "1") @PathVariable Long figureTypeId) {
        return ApiResponse.ok("조회가 완료되었습니다.", figureTypeService.getUsage(currentUser.getId(), figureTypeId));
    }
}
