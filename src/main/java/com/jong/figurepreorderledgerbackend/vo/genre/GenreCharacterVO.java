package com.jong.figurepreorderledgerbackend.vo.genre;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 캐릭터(장르 하위) (테이블 genre_character)
 * 요청, 응답, 매퍼 조회 결과에 같이 쓴다. [테이블명 joined](조인한 테이블), [request](요청 전용), [response](응답 전용) 표시가 붙은 필드는 컬럼이 아니다.
 */
@Schema(description = "캐릭터(하위). 요청, 응답에 같이 쓴다")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenreCharacterVO {

    /** PK */
    @Schema(description = "캐릭터 id", example = "1")
    private Long genreCharacterId;

    /** 소유 계정 */
    @Schema(description = "소유 계정 id. 요청 본문에 보내도 읽지 않는다", example = "1")
    private Long userId;

    /** 소속 장르 */
    @Schema(description = "소속 장르", example = "1")
    private Long genreCharacterGroupId;

    /** 캐릭터명 (하츠네 미쿠 등) */
    @Schema(description = "캐릭터명 (하츠네 미쿠 등)", example = "하츠네 미쿠", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Size(max = 100)
    private String name;

    /** 설명 (일반 텍스트) */
    @Schema(description = "설명 (일반 텍스트)", example = "자유롭게 적는 설명")
    @Size(max = 1000)
    private String description;

    /** 생성일 */
    @Schema(description = "생성일시", example = "2026-10-12T21:30:00")
    private LocalDateTime createdAt;

    /** 수정일 */
    @Schema(description = "수정일시", example = "2026-10-12T21:30:00")
    private LocalDateTime updatedAt;

    /** [genre_character_group joined] 소속 장르명 */
    @Schema(description = "소속 장르명 (조회 응답에서만 나온다)")
    private String genreCharacterGroupName;

    /** [figure_character_map joined] 이 캐릭터가 연결된 상품 (figure_id). 상품별로 묶을 때만 쓰고 응답에는 내보내지 않는다 */
    @JsonIgnore
    private Long figureId;

    /** [thumbnail_image joined] 썸네일 이미지 주소 (/uploads/ + file_path, 없으면 null) */
    @Schema(description = "썸네일 이미지 주소 (/uploads/ + file_path, 없으면 null) (조회 응답에서만 나온다)")
    private String thumbnailUrl;
}
