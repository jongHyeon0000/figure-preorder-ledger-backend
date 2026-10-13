package com.jong.figurepreorderledgerbackend.controller.series;

import com.jong.figurepreorderledgerbackend.common.response.ApiResponse;
import com.jong.figurepreorderledgerbackend.common.security.CurrentUser;
import com.jong.figurepreorderledgerbackend.service.series.SeriesService;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import com.jong.figurepreorderledgerbackend.vo.series.SeriesVO;
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

@Tag(name = "시리즈", description = "시리즈 하위 분류(예: 팝업퍼레이드). 상품은 이 단위로 여러 개 고른다.")
@RestController
@RequestMapping("/api/series")
@RequiredArgsConstructor
public class SeriesController {

    private final SeriesService seriesService;
    private final CurrentUser currentUser;

    @Operation(
            summary = "시리즈 목록 조회 (예: 팝업퍼레이드)",
            description = "응답: seriesId, seriesGroupId(상위 시리즈 그룹 id), name, description, thumbnailUrl")
    @GetMapping
    public ApiResponse<List<SeriesVO>> getAll() {
        return ApiResponse.ok(seriesService.getAll(currentUser.getId()));
    }

    @Operation(
            summary = "시리즈 추가 (예: 팝업퍼레이드)",
            description = "요청 본문: seriesGroupId: 상위 시리즈 그룹 id, name: 시리즈 이름 (본문, 둘 다 필수, 이름은 100자 이하), description: 설명 (본문, 선택, 1000자 이하)\n\n"
                    + "응답: 생성된 seriesId, seriesGroupId, name, description, thumbnailUrl")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "추가 예",
                    value = "{\"seriesGroupId\":1,\"name\":\"팝업퍼레이드\",\"description\":\"설명(선택)\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "생성된 seriesId, seriesGroupId, name, description, thumbnailUrl"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이름 중복, 입력값 오류"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "상위 시리즈 그룹 없음")
    })
    @PostMapping
    public ApiResponse<SeriesVO> create(@Valid @RequestBody SeriesVO request) {
        return ApiResponse.ok(seriesService.create(currentUser.getId(), request));
    }

    @Operation(
            summary = "시리즈 이름과 설명 수정 (예: 팝업퍼레이드)",
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
    @PutMapping("/{seriesId}")
    public ApiResponse<Void> update(
            @Parameter(description = "수정할 시리즈 id", example = "1") @PathVariable Long seriesId,
            @Valid @RequestBody SeriesVO request) {
        seriesService.update(currentUser.getId(), seriesId, request);
        return ApiResponse.ok();
    }

    @Operation(
            summary = "시리즈 삭제, 쓰던 상품은 '기타'로 이동 (예: 팝업퍼레이드)",
            description = "응답: 성공 시 data 없음")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 시 data 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "기타 삭제 불가, 상위의 마지막 하위 삭제 불가"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    @DeleteMapping("/{seriesId}")
    public ApiResponse<Void> delete(
            @Parameter(description = "삭제할 시리즈 id", example = "1") @PathVariable Long seriesId) {
        seriesService.delete(currentUser.getId(), seriesId);
        return ApiResponse.ok();
    }

    @Operation(
            summary = "시리즈 삭제 전 영향 상품 수 조회 (예: 팝업퍼레이드)",
            description = "응답: count: 이 시리즈를 쓰는 상품 수")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "count: 이 시리즈를 쓰는 상품 수"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    @GetMapping("/{seriesId}/usage")
    public ApiResponse<UsageResponse> getUsage(
            @Parameter(description = "조회할 시리즈 id", example = "1") @PathVariable Long seriesId) {
        return ApiResponse.ok(seriesService.getUsage(currentUser.getId(), seriesId));
    }
}
