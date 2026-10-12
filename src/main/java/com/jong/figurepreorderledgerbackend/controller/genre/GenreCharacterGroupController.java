package com.jong.figurepreorderledgerbackend.controller.genre;

import com.jong.figurepreorderledgerbackend.common.response.ApiResponse;
import com.jong.figurepreorderledgerbackend.common.security.CurrentUser;
import com.jong.figurepreorderledgerbackend.service.genre.GenreCharacterGroupService;
import com.jong.figurepreorderledgerbackend.vo.genre.GenreCharacterGroupRequest;
import com.jong.figurepreorderledgerbackend.vo.genre.GenreCharacterGroupResponse;
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
@RequestMapping("/api/genre-character-group")
@RequiredArgsConstructor
public class GenreCharacterGroupController {

    private final GenreCharacterGroupService genreCharacterGroupService;
    private final CurrentUser currentUser;

    /**
     * 장르 목록 조회, 하위 캐릭터 중첩 (예: 보컬로이드)
     * [GET] 목록 | /api/genre-character-group
     *
     * @return ApiResponse<List<GenreCharacterGroupResponse>> genreCharacterGroupId, name, children(캐릭터 목록: genreCharacterId, genreCharacterGroupId, name)
     */
    @GetMapping
    public ApiResponse<List<GenreCharacterGroupResponse>> getTree() {
        return ApiResponse.ok(genreCharacterGroupService.getTree(currentUser.getId()));
    }

    /**
     * 장르 추가, 기본 하위 '기타' 자동 생성 (예: 보컬로이드)
     * [POST] 단일 | /api/genre-character-group
     *
     * @param request name: 장르 이름 (본문, 필수, 100자 이하)
     *
     * @return ApiResponse<GenreCharacterGroupResponse> 생성된 genreCharacterGroupId, name, children(기본 '기타' 하나)
     * @throws 400 이름 중복, 입력값 오류
     */
    @PostMapping
    public ApiResponse<GenreCharacterGroupResponse> create(@Valid @RequestBody GenreCharacterGroupRequest request) {
        return ApiResponse.ok(genreCharacterGroupService.create(currentUser.getId(), request.getName()));
    }

    /**
     * 장르 이름 수정 (예: 보컬로이드)
     * [PUT] 단일 | /api/genre-character-group/{genreCharacterGroupId}
     *
     * @param genreCharacterGroupId 수정할 장르 id (경로)
     * @param request               name: 새 이름 (본문, 필수, 100자 이하)
     *
     * @return ApiResponse<Void> 성공 시 data 없음
     * @throws 404 대상 없음 | 400 기타 수정 불가, 이름 중복, 입력값 오류
     */
    @PutMapping("/{genreCharacterGroupId}")
    public ApiResponse<Void> update(@PathVariable Long genreCharacterGroupId, @Valid @RequestBody GenreCharacterGroupRequest request) {
        genreCharacterGroupService.update(currentUser.getId(), genreCharacterGroupId, request.getName());
        return ApiResponse.ok();
    }

    /**
     * 장르 삭제, 하위와 쓰던 상품은 '기타'로 이동 (예: 보컬로이드)
     * [DELETE] 없음 | /api/genre-character-group/{genreCharacterGroupId}
     *
     * @param genreCharacterGroupId 삭제할 장르 id (경로)
     *
     * @return ApiResponse<Void> 성공 시 data 없음
     * @throws 404 대상 없음 | 400 기타 삭제 불가
     */
    @DeleteMapping("/{genreCharacterGroupId}")
    public ApiResponse<Void> delete(@PathVariable Long genreCharacterGroupId) {
        genreCharacterGroupService.delete(currentUser.getId(), genreCharacterGroupId);
        return ApiResponse.ok();
    }

    /**
     * 장르 삭제 전 영향 상품 수 조회 (예: 보컬로이드)
     * [GET] 단일 | /api/genre-character-group/{genreCharacterGroupId}/usage
     *
     * @param genreCharacterGroupId 조회할 장르 id (경로)
     *
     * @return ApiResponse<UsageResponse> count: 이 장르의 하위를 쓰는 상품 수
     * @throws 404 대상 없음
     */
    @GetMapping("/{genreCharacterGroupId}/usage")
    public ApiResponse<UsageResponse> getUsage(@PathVariable Long genreCharacterGroupId) {
        return ApiResponse.ok(genreCharacterGroupService.getUsage(currentUser.getId(), genreCharacterGroupId));
    }
}
