package com.jong.figurepreorderledgerbackend.controller.maker;

import com.jong.figurepreorderledgerbackend.common.response.ApiResponse;
import com.jong.figurepreorderledgerbackend.common.security.CurrentUser;
import com.jong.figurepreorderledgerbackend.service.maker.MakerService;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import com.jong.figurepreorderledgerbackend.vo.maker.MakerVO;
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

@Tag(name = "제조사", description = "제조사(예: 세가). 상하위 없이 평면이다.")
@RestController
@RequestMapping("/api/maker")
@RequiredArgsConstructor
public class MakerController {

    private final MakerService makerService;
    private final CurrentUser currentUser;

    @Operation(
            summary = "제조사 목록 조회 (예: 세가, 후류)",
            description = "응답: makerId, name, description, thumbnailUrl")
    @GetMapping
    public ApiResponse<List<MakerVO>> getAll() {
        return ApiResponse.ok(makerService.getAll(currentUser.getId()));
    }

    @Operation(
            summary = "제조사 추가 (예: 세가, 후류)",
            description = "요청 본문: name: 제조사 이름 (본문, 필수, 100자 이하), description: 설명 (본문, 선택, 1000자 이하)\n\n"
                    + "응답: 생성된 makerId, name, description, thumbnailUrl")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "추가 예",
                    value = "{\"name\":\"세가\",\"description\":\"설명(선택)\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "생성된 makerId, name, description, thumbnailUrl"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이름 중복, 입력값 오류")
    })
    @PostMapping
    public ApiResponse<MakerVO> create(@Valid @RequestBody MakerVO request) {
        return ApiResponse.ok(makerService.create(currentUser.getId(), request));
    }

    @Operation(
            summary = "제조사 이름과 설명 수정 (예: 세가, 후류)",
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
    @PutMapping("/{makerId}")
    public ApiResponse<Void> update(
            @Parameter(description = "수정할 제조사 id", example = "1") @PathVariable Long makerId,
            @Valid @RequestBody MakerVO request) {
        makerService.update(currentUser.getId(), makerId, request);
        return ApiResponse.ok();
    }

    @Operation(
            summary = "제조사 삭제, 쓰던 상품은 '기타'로 이동 (예: 세가, 후류)",
            description = "응답: 성공 시 data 없음")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 시 data 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "기타 삭제 불가"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    @DeleteMapping("/{makerId}")
    public ApiResponse<Void> delete(
            @Parameter(description = "삭제할 제조사 id", example = "1") @PathVariable Long makerId) {
        makerService.delete(currentUser.getId(), makerId);
        return ApiResponse.ok();
    }

    @Operation(
            summary = "제조사 삭제 전 영향 상품 수 조회 (예: 세가, 후류)",
            description = "응답: count: 이 제조사를 쓰는 상품 수")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "count: 이 제조사를 쓰는 상품 수"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    @GetMapping("/{makerId}/usage")
    public ApiResponse<UsageResponse> getUsage(
            @Parameter(description = "조회할 제조사 id", example = "1") @PathVariable Long makerId) {
        return ApiResponse.ok(makerService.getUsage(currentUser.getId(), makerId));
    }
}
