package com.jong.figurepreorderledgerbackend.common.security;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AuthUser implements UserDetails, CredentialsContainer {

    private static final long serialVersionUID = 1L;

    private static final List<GrantedAuthority> AUTHORITIES =
            Collections.<GrantedAuthority>singletonList(new SimpleGrantedAuthority("ROLE_USER"));

    private final Long userId;
    private final String loginId;
    private String passwordHash;

    public AuthUser(Long userId, String loginId, String passwordHash) {
        this.userId = userId;
        this.loginId = loginId;
        this.passwordHash = passwordHash;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AUTHORITIES;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return loginId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void eraseCredentials() {
        this.passwordHash = null;
    }
}
