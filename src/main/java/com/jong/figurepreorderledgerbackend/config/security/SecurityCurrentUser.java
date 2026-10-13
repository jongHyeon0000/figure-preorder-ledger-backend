package com.jong.figurepreorderledgerbackend.config.security;

import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.common.security.AuthUser;
import com.jong.figurepreorderledgerbackend.common.security.CurrentUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityCurrentUser implements CurrentUser {

    @Override
    public Long getId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthUser)) {
            throw new BusinessException(GlobalExceptionCode.UNAUTHORIZED);
        }
        return ((AuthUser) authentication.getPrincipal()).getUserId();
    }
}
