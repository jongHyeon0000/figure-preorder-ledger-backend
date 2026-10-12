package com.jong.figurepreorderledgerbackend.controller.thumbnail;

import com.jong.figurepreorderledgerbackend.common.constant.ThumbnailTarget;
import com.jong.figurepreorderledgerbackend.common.response.ApiResponse;
import com.jong.figurepreorderledgerbackend.common.security.CurrentUser;
import com.jong.figurepreorderledgerbackend.service.thumbnail.ThumbnailImageService;
import com.jong.figurepreorderledgerbackend.vo.thumbnail.ThumbnailUrlResponse;
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
public class ThumbnailImageController {

    private final ThumbnailImageService thumbnailImageService;
    private final CurrentUser currentUser;

    /**
     * 썸네일 업로드와 교체 (예: 상품 대표 사진, 판매처 채널 로고)
     * [PUT] 단일 | /api/upload/thumbnail-image/{targetType}/{targetId}
     *
     * @param targetType 썸네일을 붙일 대상 종류 (경로): figure, shop-group, shop, figure-type-group, figure-type,
     *                   series-group, series, genre-character-group, genre-character, maker
     * @param targetId   대상 id (경로)
     * @param file       업로드할 이미지 파일 (본문 multipart, 필드명 file, 필수, jpg/jpeg/png/gif/webp, 최대 5MB)
     *
     * @return ApiResponse<ThumbnailUrlResponse> thumbnailUrl: 이미지를 내려주는 /uploads/ 주소
     * @throws 404 대상 없음 | 400 알 수 없는 대상 종류, 파일 없음, 허용하지 않는 확장자, 파일 내용이 확장자와 다름, 크기 초과
     */
    @PutMapping(value = "/{targetType}/{targetId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ThumbnailUrlResponse> upload(@PathVariable String targetType, @PathVariable Long targetId,
                                                    @RequestParam("file") MultipartFile file) {
        ThumbnailTarget target = ThumbnailTarget.fromUrlValue(targetType);
        return ApiResponse.ok(thumbnailImageService.upload(currentUser.getId(), target, targetId, file));
    }

    /**
     * 썸네일 제거 (예: 대표 사진 빼기)
     * [DELETE] 없음 | /api/upload/thumbnail-image/{targetType}/{targetId}
     *
     * @param targetType 썸네일을 지울 대상 종류 (경로, 업로드와 같은 값)
     * @param targetId   대상 id (경로)
     *
     * @return ApiResponse<Void> 성공 시 data 없음 (썸네일이 없어도 성공)
     * @throws 404 대상 없음 | 400 알 수 없는 대상 종류
     */
    @DeleteMapping("/{targetType}/{targetId}")
    public ApiResponse<Void> delete(@PathVariable String targetType, @PathVariable Long targetId) {
        thumbnailImageService.delete(currentUser.getId(), ThumbnailTarget.fromUrlValue(targetType), targetId);
        return ApiResponse.ok();
    }
}
