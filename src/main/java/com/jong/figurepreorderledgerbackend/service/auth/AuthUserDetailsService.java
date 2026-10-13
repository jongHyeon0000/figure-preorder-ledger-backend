package com.jong.figurepreorderledgerbackend.service.auth;

import com.jong.figurepreorderledgerbackend.common.security.AuthUser;
import com.jong.figurepreorderledgerbackend.mapper.user.UserMapper;
import com.jong.figurepreorderledgerbackend.vo.user.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public AuthUser loadUserByUsername(String loginId) throws UsernameNotFoundException {
        UserVO user = userMapper.selectByLoginId(loginId);
        if (user == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다");
        }
        return new AuthUser(user.getUserId(), user.getLoginId(), user.getPasswordHash());
    }

    public AuthUser loadUserById(Long userId) throws UsernameNotFoundException {
        UserVO user = userMapper.selectById(userId);
        if (user == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다");
        }
        return new AuthUser(user.getUserId(), user.getLoginId(), null);
    }
}