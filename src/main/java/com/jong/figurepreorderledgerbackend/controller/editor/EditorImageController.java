package com.jong.figurepreorderledgerbackend.controller.editor;

import com.jong.figurepreorderledgerbackend.common.response.ApiResponse;
import com.jong.figurepreorderledgerbackend.common.security.CurrentUser;
import com.jong.figurepreorderledgerbackend.service.editor.EditorImageService;
import com.jong.figurepreorderledgerbackend.vo.editor.EditorImageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "에디터 이미지", description = "상품 설명(HTML)에 넣는 이미지 업로드. 설명에 쓰이지 않은 이미지는 1일 뒤 매일 00시에 자동 정리된다.")
@RestController
@RequestMapping("/api/upload/editor-image")
@RequiredArgsConstructor
public class EditorImageController {

    private final EditorImageService editorImageService;
    private final CurrentUser currentUser;

    @Operation(
            summary = "에디터 이미지 업로드 (예: 상품 설명에 넣을 사진)",
            description = "응답: editorImageId, url(에디터가 이미지 src로 쓸 /uploads/ 주소)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "editorImageId, url(에디터가 이미지 src로 쓸 /uploads/ 주소)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "파일 없음, 허용하지 않는 확장자, 파일 내용이 확장자와 다름, 크기 초과")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<EditorImageVO> upload(
            @Parameter(description = "업로드할 이미지 파일 (본문 multipart, 필드명 file, 필수, jpg/jpeg/png/gif/webp, 최대 5MB)") @RequestParam("file") MultipartFile file) {
        return ApiResponse.ok("이미지 업로드가 완료되었습니다.", editorImageService.upload(currentUser.getId(), file));
    }
}
