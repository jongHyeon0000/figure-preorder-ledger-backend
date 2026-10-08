package com.jong.figurepreorderledgerbackend.controller.figure;

import com.jong.figurepreorderledgerbackend.common.response.ApiResponse;
import com.jong.figurepreorderledgerbackend.common.security.CurrentUser;
import com.jong.figurepreorderledgerbackend.service.figure.FigureFormService;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureFormOptionsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/figure/form-options")
@RequiredArgsConstructor
public class FigureFormController {

    private final FigureFormService figureFormService;
    private final CurrentUser currentUser;

    /**
     * 상품 등록, 수정 폼의 선택지 5개 축을 한 번에 조회 (예: 판매처, 형태, 시리즈, 장르·캐릭터, 제조사)
     * [GET] 단일 | /api/figure/form-options
     *
     * @return ApiResponse<FigureFormOptionsResponse> shops(판매처 채널 > 스토어), types(형태 > 하위), series(시리즈 그룹 > 시리즈), genres(장르 > 캐릭터), makers(제조사, 평면)
     */
    @GetMapping
    public ApiResponse<FigureFormOptionsResponse> getOptions() {
        return ApiResponse.ok(figureFormService.getOptions(currentUser.getId()));
    }
}
