package com.jong.figurepreorderledgerbackend.controller.figure;

import com.jong.figurepreorderledgerbackend.common.response.ApiResponse;
import com.jong.figurepreorderledgerbackend.common.security.CurrentUser;
import com.jong.figurepreorderledgerbackend.service.figure.FigureFormService;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureFormOptionsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "상품 폼 선택지", description = "상품 등록, 수정 화면에서 쓰는 분류 선택지 묶음")
@RestController
@RequestMapping("/api/figure/form-options")
@RequiredArgsConstructor
public class FigureFormController {

    private final FigureFormService figureFormService;
    private final CurrentUser currentUser;

    @Operation(
            summary = "상품 등록, 수정 폼의 선택지 5개 축을 한 번에 조회 (예: 판매처, 형태, 시리즈, 장르·캐릭터, 제조사)",
            description = "폼 하나를 열 때 5번 요청하지 않도록 한 번에 돌려준다. 등록 폼은 이 1요청, 수정 폼은 상세 조회와 이 요청 2요청이다.\n\n"
                    + "| 키 | 내용 |\n"
                    + "|---|---|\n"
                    + "| `shops` | 판매처 채널 > 스토어 |\n"
                    + "| `types` | 형태 > 하위 형태 |\n"
                    + "| `series` | 시리즈 그룹 > 시리즈 |\n"
                    + "| `genres` | 장르 > 캐릭터 |\n"
                    + "| `makers` | 제조사 (상하위 없이 평면) |")
    @GetMapping
    public ApiResponse<FigureFormOptionsResponse> getOptions() {
        return ApiResponse.ok(figureFormService.getOptions(currentUser.getId()));
    }
}
