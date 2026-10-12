package com.jong.figurepreorderledgerbackend.vo.genre;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenreCharacterVO {

    /** PK */
    private Long genreCharacterId;

    /** 소유 계정 */
    private Long userId;

    /** 소속 장르 */
    private Long genreCharacterGroupId;

    /** 캐릭터명 (하츠네 미쿠 등) */
    @NotBlank
    @Size(max = 100)
    private String name;

    /** 생성일 */
    private LocalDateTime createdAt;

    /** 수정일 */
    private LocalDateTime updatedAt;

    /** [genre_character_group joined] 소속 장르명 */
    private String genreCharacterGroupName;

    /** [figure_character_map joined] 이 캐릭터가 연결된 상품 (figure_id). 상품별로 묶을 때만 쓰고 응답에는 내보내지 않는다 */
    @JsonIgnore
    private Long figureId;
}
