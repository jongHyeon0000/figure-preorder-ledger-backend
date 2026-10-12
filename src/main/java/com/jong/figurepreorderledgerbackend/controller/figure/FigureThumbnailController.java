package com.jong.figurepreorderledgerbackend.controller.figure;

import com.jong.figurepreorderledgerbackend.common.response.ApiResponse;
import com.jong.figurepreorderledgerbackend.common.security.CurrentUser;
import com.jong.figurepreorderledgerbackend.service.figure.FigureThumbnailService;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload/thumbnail-image")
@RequiredArgsConstructor
public class FigureThumbnailController {

    private final FigureThumbnailService figureThumbnailService;
    private final CurrentUser currentUser;

    /**
     * 상품 썸네일 업로드와 교체 (예: 상품 대표 사진)
     * [PUT] 단일 | /api/upload/thumbnail-image/{figureId}
     *
     * @param figureId 썸네일을 올릴 상품 id (경로)
     * @param file     업로드할 이미지 파일 (본문 multipart, 필드명 file, 필수, jpg/jpeg/png/gif/webp, 최대 5MB)
     *
     * @return ApiResponse<FigureResponseVO> 썸네일이 반영된 상품 한 건 (목록 항목 모양, thumbnailUrl에 /uploads/ 주소)
     * @throws 404 상품 없음 | 400 파일 없음, 허용하지 않는 확장자, 파일 내용이 확장자와 다름, 크기 초과
     */
    @PutMapping(value = "/{figureId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FigureResponseVO> upload(@PathVariable Long figureId, @RequestParam("file") MultipartFile file) {
        return ApiResponse.ok(figureThumbnailService.upload(currentUser.getId(), figureId, file));
    }

    /**
     * 상품 썸네일 제거 (예: 대표 사진 빼기)
     * [DELETE] 단일 | /api/upload/thumbnail-image/{figureId}
     *
     * @param figureId 썸네일을 지울 상품 id (경로)
     *
     * @return ApiResponse<FigureResponseVO> 썸네일이 빠진 상품 한 건 (목록 항목 모양, 원래 썸네일이 없어도 성공)
     * @throws 404 상품 없음
     */
    @DeleteMapping("/{figureId}")
    public ApiResponse<FigureResponseVO> delete(@PathVariable Long figureId) {
        return ApiResponse.ok(figureThumbnailService.delete(currentUser.getId(), figureId));
    }
}
