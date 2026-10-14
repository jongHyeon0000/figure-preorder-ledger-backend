package com.jong.figurepreorderledgerbackend.service.auth.impl;

import com.jong.figurepreorderledgerbackend.common.constant.CategoryConstant;
import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.common.security.AuthUser;
import com.jong.figurepreorderledgerbackend.common.security.JwtProvider;
import com.jong.figurepreorderledgerbackend.mapper.figure.FigureMapper;
import com.jong.figurepreorderledgerbackend.mapper.genre.GenreCharacterGroupMapper;
import com.jong.figurepreorderledgerbackend.mapper.genre.GenreCharacterMapper;
import com.jong.figurepreorderledgerbackend.mapper.maker.MakerMapper;
import com.jong.figurepreorderledgerbackend.mapper.series.SeriesGroupMapper;
import com.jong.figurepreorderledgerbackend.mapper.series.SeriesMapper;
import com.jong.figurepreorderledgerbackend.mapper.shop.ShopGroupMapper;
import com.jong.figurepreorderledgerbackend.mapper.shop.ShopMapper;
import com.jong.figurepreorderledgerbackend.mapper.type.FigureTypeGroupMapper;
import com.jong.figurepreorderledgerbackend.mapper.type.FigureTypeMapper;
import com.jong.figurepreorderledgerbackend.mapper.user.UserMapper;
import com.jong.figurepreorderledgerbackend.service.auth.AuthService;
import com.jong.figurepreorderledgerbackend.service.common.ImageFileStore;
import com.jong.figurepreorderledgerbackend.vo.user.LoginRequestVO;
import com.jong.figurepreorderledgerbackend.vo.user.LoginResponse;
import com.jong.figurepreorderledgerbackend.vo.user.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final FigureMapper figureMapper;
    private final ShopGroupMapper shopGroupMapper;
    private final ShopMapper shopMapper;
    private final FigureTypeGroupMapper figureTypeGroupMapper;
    private final FigureTypeMapper figureTypeMapper;
    private final SeriesGroupMapper seriesGroupMapper;
    private final SeriesMapper seriesMapper;
    private final GenreCharacterGroupMapper genreCharacterGroupMapper;
    private final GenreCharacterMapper genreCharacterMapper;
    private final MakerMapper makerMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final ImageFileStore imageFileStore;

    @Override
    @Transactional
    public UserVO signup(UserVO request) {
        String loginId = request.getLoginId();
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        String name = request.getName().trim();
        if (name.isEmpty() || name.length() > 20) {
            throw new BusinessException(GlobalExceptionCode.VALIDATION_FAILED, "name: 앞뒤 공백을 뺀 1~20자여야 합니다");
        }

        if (userMapper.selectByLoginId(loginId) != null) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_ACCOUNT, "이미 사용 중인 아이디입니다");
        }
        if (userMapper.selectByEmail(email) != null) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_ACCOUNT, "이미 사용 중인 이메일입니다");
        }

        try {
            userMapper.insert(loginId, email, name, passwordEncoder.encode(request.getPassword()));
        } catch (DuplicateKeyException e) {
            // 중복 확인 직후 동시에 가입한 경우는 UNIQUE 제약이 막는다
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_ACCOUNT, "이미 사용 중인 아이디 또는 이메일입니다");
        }
        Long userId = userMapper.selectLastInsertId();
        createDefaultCategories(userId);
        return userMapper.selectById(userId);
    }

    @Override
    public LoginResponse login(LoginRequestVO request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(request.getLoginId(), request.getPassword()));
        } catch (AuthenticationException e) {
            throw new BusinessException(GlobalExceptionCode.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다");
        }
        AuthUser principal = (AuthUser) authentication.getPrincipal();
        return new LoginResponse(jwtProvider.createToken(principal.getUserId()));
    }

    @Override
    public UserVO getMe(Long userId) {
        UserVO user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        return user;
    }

    @Override
    @Transactional
    public void withdraw(Long userId, String password) {
        UserVO user = getMe(userId);
        try {
            authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(user.getLoginId(), password));
        } catch (AuthenticationException e) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "비밀번호가 일치하지 않습니다");
        }

        // 상품이 분류를 RESTRICT로 참조하므로 상품을 먼저 지운다
        figureMapper.deleteAllByUser(userId);
        userMapper.delete(userId);

        // 롤백 시 파일만 사라지지 않도록 커밋 뒤에 지운다
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                imageFileStore.deleteDirectory(String.valueOf(userId));
            }
        });
    }

    private void createDefaultCategories(Long userId) {
        String name = CategoryConstant.DEFAULT_NAME;

        shopGroupMapper.insert(userId, name, null);
        shopMapper.insert(userId, shopGroupMapper.selectByName(userId, name).getId(), name, null);

        figureTypeGroupMapper.insert(userId, name, null);
        figureTypeMapper.insert(userId, figureTypeGroupMapper.selectByName(userId, name).getId(), name, null);

        seriesGroupMapper.insert(userId, name, null);
        seriesMapper.insert(userId, seriesGroupMapper.selectByName(userId, name).getId(), name, null);

        genreCharacterGroupMapper.insert(userId, name, null);
        genreCharacterMapper.insert(userId,
                genreCharacterGroupMapper.selectByName(userId, name).getId(), name, null);

        makerMapper.insert(userId, name, null);
    }
}
