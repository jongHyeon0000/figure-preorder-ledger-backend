package com.jong.figurepreorderledgerbackend.controller.auth;

import com.jong.figurepreorderledgerbackend.common.response.ApiResponse;
import com.jong.figurepreorderledgerbackend.common.security.CurrentUser;
import com.jong.figurepreorderledgerbackend.service.auth.AuthService;
import com.jong.figurepreorderledgerbackend.vo.user.LoginRequestVO;
import com.jong.figurepreorderledgerbackend.vo.user.LoginResponse;
import com.jong.figurepreorderledgerbackend.vo.user.UserVO;
import com.jong.figurepreorderledgerbackend.vo.user.WithdrawRequestVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CurrentUser currentUser;

    /**
     * 회원가입 (예: 아이디 jong1234, 닉네임 종현)
     * [POST] 단일 | /api/auth/signup (인증 불필요)
     *
     * @param request loginId: 로그인 아이디 (본문, 필수, 영문과 숫자 4~30자), email: 이메일 (본문, 필수, 100자 이하),
     *                name: 닉네임 (본문, 필수, 20자 이하), password: 비밀번호 (본문, 필수, 8~20자)
     *
     * @return ApiResponse<UserVO> 가입한 userId, loginId, email, name, createdAt, updatedAt (비밀번호 관련 값 없음)
     * @throws 400 아이디 또는 이메일 중복, 입력값 오류
     */
    @PostMapping("/signup")
    public ApiResponse<UserVO> signup(@Valid @RequestBody UserVO request) {
        return ApiResponse.ok(authService.signup(request));
    }

    /**
     * 로그인, JWT 발급 (예: jong1234 로 로그인)
     * [POST] 단일 | /api/auth/login (인증 불필요)
     *
     * @param request loginId: 로그인 아이디 (본문, 필수), password: 비밀번호 (본문, 필수)
     *
     * @return ApiResponse<LoginResponse> accessToken: 이후 요청의 Authorization: Bearer 헤더에 쓸 토큰 (24시간 유효)
     * @throws 401 아이디 또는 비밀번호 불일치 (둘을 구분하지 않음) | 400 입력값 오류
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequestVO request) {
        return ApiResponse.ok(authService.login(request));
    }

    /**
     * 내 정보 조회 (예: 로그인한 계정의 닉네임)
     * [GET] 단일 | /api/auth/me
     *
     * @return ApiResponse<UserVO> userId, loginId, email, name, createdAt, updatedAt
     * @throws 401 토큰 없음, 만료, 변조, 탈퇴한 계정
     */
    @GetMapping("/me")
    public ApiResponse<UserVO> getMe() {
        return ApiResponse.ok(authService.getMe(currentUser.getId()));
    }

    /**
     * 회원 탈퇴 (예: 내 계정, 상품, 분류, 업로드 이미지 전부 삭제)
     * [DELETE] 없음 | /api/auth/me
     *
     * @param request password: 현재 비밀번호 (본문, 필수)
     *
     * @return ApiResponse<Void> 성공 시 data 없음 (되돌릴 수 없다)
     * @throws 401 토큰 없음, 만료, 변조 | 400 비밀번호 불일치, 입력값 오류
     */
    @DeleteMapping("/me")
    public ApiResponse<Void> withdraw(@Valid @RequestBody WithdrawRequestVO request) {
        authService.withdraw(currentUser.getId(), request.getPassword());
        return ApiResponse.ok();
    }
}
