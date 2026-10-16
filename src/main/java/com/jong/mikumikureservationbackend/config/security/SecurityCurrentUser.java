package com.jong.mikumikureservationbackend.config.security;

import com.jong.mikumikureservationbackend.common.constant.GlobalExceptionCode;
import com.jong.mikumikureservationbackend.common.exception.BusinessException;
import com.jong.mikumikureservationbackend.common.security.AuthUser;
import com.jong.mikumikureservationbackend.common.security.CurrentUser;
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
