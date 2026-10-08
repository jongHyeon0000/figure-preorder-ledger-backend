package com.jong.figurepreorderledgerbackend.controller.genre;

import com.jong.figurepreorderledgerbackend.common.response.ApiResponse;
import com.jong.figurepreorderledgerbackend.common.security.CurrentUser;
import com.jong.figurepreorderledgerbackend.service.genre.GenreService;
import com.jong.figurepreorderledgerbackend.vo.genre.GenreRequest;
import com.jong.figurepreorderledgerbackend.vo.genre.GenreResponse;
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
@RequestMapping("/api/genre")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;
    private final CurrentUser currentUser;

    /**
     * 장르 목록 조회, 하위 캐릭터 중첩 (예: 보컬로이드)
     * [GET] 목록 | /api/genre
     *
     * @return ApiResponse<List<GenreResponse>> id, name, children(캐릭터 목록: id, genreId, name)
     */
    @GetMapping
    public ApiResponse<List<GenreResponse>> getTree() {
        return ApiResponse.ok(genreService.getTree(currentUser.getId()));
    }

    /**
     * 장르 추가, 기본 하위 '기타' 자동 생성 (예: 보컬로이드)
     * [POST] 단일 | /api/genre
     *
     * @param request name: 장르 이름 (본문, 필수, 100자 이하)
     *
     * @return ApiResponse<GenreResponse> 생성된 id, name, children(기본 '기타' 하나)
     * @throws 400 이름 중복, 입력값 오류
     */
    @PostMapping
    public ApiResponse<GenreResponse> create(@Valid @RequestBody GenreRequest request) {
        return ApiResponse.ok(genreService.create(currentUser.getId(), request.getName()));
    }

    /**
     * 장르 이름 수정 (예: 보컬로이드)
     * [PUT] 단일 | /api/genre/{id}
     *
     * @param id      수정할 장르 id (경로)
     * @param request name: 새 이름 (본문, 필수, 100자 이하)
     *
     * @return ApiResponse<Void> 성공 시 data 없음
     * @throws 404 대상 없음 | 400 기타 수정 불가, 이름 중복, 입력값 오류
     */
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody GenreRequest request) {
        genreService.update(currentUser.getId(), id, request.getName());
        return ApiResponse.ok();
    }

    /**
     * 장르 삭제, 하위와 쓰던 상품은 '기타'로 이동 (예: 보컬로이드)
     * [DELETE] 없음 | /api/genre/{id}
     *
     * @param id 삭제할 장르 id (경로)
     *
     * @return ApiResponse<Void> 성공 시 data 없음
     * @throws 404 대상 없음 | 400 기타 삭제 불가
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        genreService.delete(currentUser.getId(), id);
        return ApiResponse.ok();
    }

    /**
     * 장르 삭제 전 영향 상품 수 조회 (예: 보컬로이드)
     * [GET] 단일 | /api/genre/{id}/usage
     *
     * @param id 조회할 장르 id (경로)
     *
     * @return ApiResponse<UsageResponse> count: 이 장르의 하위를 쓰는 상품 수
     * @throws 404 대상 없음
     */
    @GetMapping("/{id}/usage")
    public ApiResponse<UsageResponse> getUsage(@PathVariable Long id) {
        return ApiResponse.ok(genreService.getUsage(currentUser.getId(), id));
    }
}
