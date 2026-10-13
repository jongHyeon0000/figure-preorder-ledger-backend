package com.jong.figurepreorderledgerbackend.service.auth;

import com.jong.figurepreorderledgerbackend.vo.user.LoginRequestVO;
import com.jong.figurepreorderledgerbackend.vo.user.LoginResponse;
import com.jong.figurepreorderledgerbackend.vo.user.UserVO;

public interface AuthService {

    /* 가입 시 분류 9개의 기본 '기타'도 만든다 */
    UserVO signup(UserVO request);

    LoginResponse login(LoginRequestVO request);

    UserVO getMe(Long userId);

    /* 업로드 파일은 DB 삭제가 커밋된 뒤에 지운다 */
    void withdraw(Long userId, String password);
}
