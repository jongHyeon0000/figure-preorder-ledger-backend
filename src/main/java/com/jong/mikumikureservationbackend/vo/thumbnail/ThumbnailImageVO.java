package com.jong.mikumikureservationbackend.vo.thumbnail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jong.mikumikureservationbackend.common.constant.ThumbnailTarget;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 썸네일 이미지 (테이블 thumbnail_image, 상품과 분류 공통)
 * 테이블과 1:1인 순수 VO. 매퍼의 단일 행 조회 결과에 쓴다.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThumbnailImageVO {

    /** PK */
    private Long thumbnailImageId;

    /** 소유 계정 */
    private Long userId;

    /** 대상 종류: FIGURE 상품, SHOP_GROUP 판매처 채널, SHOP 판매처 스토어, FIGURE_TYPE_GROUP 형태 상위, FIGURE_TYPE 형태 하위, SERIES_GROUP 시리즈 상위, SERIES 시리즈 하위, GENRE_CHARACTER_GROUP 장르, GENRE_CHARACTER 캐릭터, MAKER 제조사 */
    private ThumbnailTarget targetType;

    /** 대상 행의 id (target_type 테이블의 PK) */
    private Long targetId;

    /** 서버 저장 경로 */
    private String filePath;

    /** 원본 파일명 */
    private String originalName;

    /** 파일 크기(byte) */
    private Long fileSize;

    /** 업로드일 */
    private LocalDateTime createdAt;
}
