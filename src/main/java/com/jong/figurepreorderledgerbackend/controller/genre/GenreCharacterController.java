package com.jong.figurepreorderledgerbackend.controller.genre;

import com.jong.figurepreorderledgerbackend.common.response.ApiResponse;
import com.jong.figurepreorderledgerbackend.common.security.CurrentUser;
import com.jong.figurepreorderledgerbackend.service.genre.GenreCharacterService;
import com.jong.figurepreorderledgerbackend.vo.genre.GenreCharacterCreateRequest;
import com.jong.figurepreorderledgerbackend.vo.genre.GenreCharacterResponse;
import com.jong.figurepreorderledgerbackend.vo.genre.GenreCharacterUpdateRequest;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
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
     * @return ApiResponse<List<GenreCharacterResponse>> id, genreId(상위 장르 id), name
     */
    @GetMapping
    public ApiResponse<List<GenreCharacterResponse>> getAll() {
        return ApiResponse.ok(genreCharacterService.getAll(currentUser.getId()));
    }

    /**
     * 캐릭터 추가 (예: 하츠네 미쿠)
     * [POST] 단일 | /api/genre-character
     *
     * @param request genreId: 상위 장르 id, name: 캐릭터 이름 (본문, 둘 다 필수, 이름은 100자 이하)
     *
     * @return ApiResponse<GenreCharacterResponse> 생성된 id, genreId, name
     * @throws 404 상위 장르 없음 | 400 이름 중복, 입력값 오류
     */
    @PostMapping
    public ApiResponse<GenreCharacterResponse> create(@Valid @RequestBody GenreCharacterCreateRequest request) {
        return ApiResponse.ok(genreCharacterService.create(currentUser.getId(), request.getGenreId(), request.getName()));
    }

    /**
     * 캐릭터 이름 수정 (예: 하츠네 미쿠)
     * [PUT] 단일 | /api/genre-character/{id}
     *
     * @param id      수정할 캐릭터 id (경로)
     * @param request name: 새 이름 (본문, 필수, 100자 이하)
     *
     * @return ApiResponse<Void> 성공 시 data 없음
     * @throws 404 대상 없음 | 400 기타 수정 불가, 이름 중복, 입력값 오류
     */
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody GenreCharacterUpdateRequest request) {
        genreCharacterService.update(currentUser.getId(), id, request.getName());
        return ApiResponse.ok();
    }

    /**
     * 캐릭터 삭제, 쓰던 상품은 '기타'로 이동 (예: 하츠네 미쿠)
     * [DELETE] 없음 | /api/genre-character/{id}
     *
     * @param id 삭제할 캐릭터 id (경로)
     *
     * @return ApiResponse<Void> 성공 시 data 없음
     * @throws 404 대상 없음 | 400 기타 삭제 불가, 상위의 마지막 하위 삭제 불가
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        genreCharacterService.delete(currentUser.getId(), id);
        return ApiResponse.ok();
    }

    /**
     * 캐릭터 삭제 전 영향 상품 수 조회 (예: 하츠네 미쿠)
     * [GET] 단일 | /api/genre-character/{id}/usage
     *
     * @param id 조회할 캐릭터 id (경로)
     *
     * @return ApiResponse<UsageResponse> count: 이 캐릭터를 쓰는 상품 수
     * @throws 404 대상 없음
     */
    @GetMapping("/{id}/usage")
    public ApiResponse<UsageResponse> getUsage(@PathVariable Long id) {
        return ApiResponse.ok(genreCharacterService.getUsage(currentUser.getId(), id));
    }
}
