package com.jong.mikumikureservationbackend.service.thumbnail;

import com.jong.mikumikureservationbackend.common.constant.ThumbnailTarget;
import com.jong.mikumikureservationbackend.vo.thumbnail.ThumbnailUrlResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ThumbnailImageService {

    /*
     * 대상(상품, 분류)의 썸네일을 올린다. 이미 있으면 새 파일로 바꾸고 옛 파일을 지운다.
     * 대상이 내 것이어야 하고(없거나 남의 것이면 404), 확장자와 파일 내용(앞부분 바이트)이 모두 허용된 이미지여야 한다(400).
     */
    ThumbnailUrlResponse upload(Long userId, ThumbnailTarget target, Long targetId, MultipartFile file);

    /* 대상의 썸네일을 지운다(파일과 행). 썸네일이 없어도 오류 없이 끝난다. 대상이 없으면 404 */
    void delete(Long userId, ThumbnailTarget target, Long targetId);

    /*
     * 대상을 삭제한 뒤 그 대상들의 썸네일 행과 파일을 지운다. 다른 서비스(상품, 분류 삭제)가 호출한다.
     * 파일 삭제에 실패해도 로그만 남기고 넘어간다.
     */
    void deleteByTargets(Long userId, ThumbnailTarget target, List<Long> targetIds);
}
