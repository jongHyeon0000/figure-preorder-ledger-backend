package com.jong.figurepreorderledgerbackend.controller.genre;

import com.jong.figurepreorderledgerbackend.common.response.ApiResponse;
import com.jong.figurepreorderledgerbackend.common.security.CurrentUser;
import com.jong.figurepreorderledgerbackend.service.genre.GenreCharacterService;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import com.jong.figurepreorderledgerbackend.vo.genre.GenreCharacterVO;
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

@RestController
@RequestMapping("/api/genre-character")
@RequiredArgsConstructor
public class GenreCharacterController {

    private final GenreCharacterService genreCharacterService;
    private final CurrentUser currentUser;

    /**
     * 캐릭터 목록 조회 (예: 하츠네 미쿠)
     * [GET] 목록 | /api/genre-character
     *
     * @return ApiResponse<List<GenreCharacterVO>> genreCharacterId, genreCharacterGroupId(상위 장르 id), name, description, thumbnailUrl
     */
    @GetMapping
    public ApiResponse<List<GenreCharacterVO>> getAll() {
        return ApiResponse.ok(genreCharacterService.getAll(currentUser.getId()));
    }

    /**
     * 캐릭터 추가 (예: 하츠네 미쿠)
     * [POST] 단일 | /api/genre-character
     *
     * @param request genreCharacterGroupId: 상위 장르 id, name: 캐릭터 이름 (본문, 둘 다 필수, 이름은 100자 이하), description: 설명 (본문, 선택, 1000자 이하)
     *
     * @return ApiResponse<GenreCharacterVO> 생성된 genreCharacterId, genreCharacterGroupId, name, description, thumbnailUrl
     * @throws 404 상위 장르 없음 | 400 이름 중복, 입력값 오류
     */
    @PostMapping
    public ApiResponse<GenreCharacterVO> create(@Valid @RequestBody GenreCharacterVO request) {
        return ApiResponse.ok(genreCharacterService.create(currentUser.getId(), request));
    }

    /**
     * 캐릭터 이름과 설명 수정 (예: 하츠네 미쿠)
     * [PUT] 단일 | /api/genre-character/{genreCharacterId}
     *
     * @param genreCharacterId 수정할 캐릭터 id (경로)
     * @param request          name: 새 이름 (본문, 필수, 100자 이하), description: 새 설명 (본문, 선택, 1000자 이하, 생략하면 설명 삭제)
     *
     * @return ApiResponse<Void> 성공 시 data 없음
     * @throws 404 대상 없음 | 400 기타 이름 변경 불가, 이름 중복, 입력값 오류
     */
    @PutMapping("/{genreCharacterId}")
    public ApiResponse<Void> update(@PathVariable Long genreCharacterId, @Valid @RequestBody GenreCharacterVO request) {
        genreCharacterService.update(currentUser.getId(), genreCharacterId, request);
        return ApiResponse.ok();
    }

    /**
     * 캐릭터 삭제, 쓰던 상품은 '기타'로 이동 (예: 하츠네 미쿠)
     * [DELETE] 없음 | /api/genre-character/{genreCharacterId}
     *
     * @param genreCharacterId 삭제할 캐릭터 id (경로)
     *
     * @return ApiResponse<Void> 성공 시 data 없음
     * @throws 404 대상 없음 | 400 기타 삭제 불가, 상위의 마지막 하위 삭제 불가
     */
    @DeleteMapping("/{genreCharacterId}")
    public ApiResponse<Void> delete(@PathVariable Long genreCharacterId) {
        genreCharacterService.delete(currentUser.getId(), genreCharacterId);
        return ApiResponse.ok();
    }

    /**
     * 캐릭터 삭제 전 영향 상품 수 조회 (예: 하츠네 미쿠)
     * [GET] 단일 | /api/genre-character/{genreCharacterId}/usage
     *
     * @param genreCharacterId 조회할 캐릭터 id (경로)
     *
     * @return ApiResponse<UsageResponse> count: 이 캐릭터를 쓰는 상품 수
     * @throws 404 대상 없음
     */
    @GetMapping("/{genreCharacterId}/usage")
    public ApiResponse<UsageResponse> getUsage(@PathVariable Long genreCharacterId) {
        return ApiResponse.ok(genreCharacterService.getUsage(currentUser.getId(), genreCharacterId));
    }
}
