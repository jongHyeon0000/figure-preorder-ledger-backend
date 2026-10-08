package com.jong.figurepreorderledgerbackend.config;

import com.jong.figurepreorderledgerbackend.common.security.CurrentUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/*
 * 인증을 만들기 전까지만 쓰는 개발용 구현. local 프로파일에서 고정된 사용자 id를 돌려준다.
 * 인증 단계에서 토큰에서 id를 꺼내는 구현으로 교체한다.
 */
@Component
@Profile("local")
public class DevCurrentUser implements CurrentUser {

    private final Long userId;

    public DevCurrentUser(@Value("${app.dev.user-id}") Long userId) {
        this.userId = userId;
    }

    @Override
    public Long getId() {
        return userId;
    }
}
