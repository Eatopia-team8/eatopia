package org.example.eatopia.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.eatopia.common.core.dto.JwtPayload;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.common.infra.security.JwtProvider;
import org.example.eatopia.domain.auth.dto.request.AuthLoginRequest;
import org.example.eatopia.domain.auth.dto.request.AuthSignUpRequest;
import org.example.eatopia.domain.auth.dto.response.AuthLoginResponse;
import org.example.eatopia.domain.auth.dto.response.AuthSignUpResponse;
import org.example.eatopia.domain.auth.entity.RefreshToken;
import org.example.eatopia.domain.auth.exception.AuthErrorCode;
import org.example.eatopia.domain.auth.repository.AuthRepository;
import org.example.eatopia.domain.user.config.UserRole;
import org.example.eatopia.domain.user.entity.User;
import org.example.eatopia.domain.user.exception.UserErrorCode;
import org.example.eatopia.domain.user.repository.PasswordResetTokenRepository;
import org.example.eatopia.domain.user.repository.UserRepository;
import org.example.eatopia.domain.user.validator.UserValidator;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthCommandServiceImpl implements AuthCommandService {

    private static final List<UserRole> ALLOWED_SIGNUP_ROLES = Arrays.asList(
            UserRole.BUYER,
            UserRole.SELLER,
            UserRole.ADMIN
    );

    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final AuthRepository authRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserValidator userValidator;

    private String issueToken(User user) {

        JwtPayload payload = createJwtPayloadFromUser(user);
        return jwtProvider.createToken(payload);
    }

    private JwtPayload createJwtPayloadFromUser(User user) {

        Collection<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name())
        );

        log.info("User name for JWT: {}", user.getName());

        return JwtPayload.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .authorities(authorities)
                .build();
    }

    @Override
    public AuthSignUpResponse signUp(AuthSignUpRequest request) {

        if (!ALLOWED_SIGNUP_ROLES.contains(request.role())) {
            throw new GlobalException(AuthErrorCode.ACCESS_DENIED, "허용되지 않은 사용자 역할입니다.");
        }

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new GlobalException(UserErrorCode.DUPLICATE_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        User user = User.signUp(
                request.email(),
                encodedPassword,
                request.name(),
                request.role()
        );

        User savedUser = userRepository.save(user);

        String jwt = issueToken(savedUser);

        return AuthSignUpResponse.of(savedUser, jwt);
    }

    @Override
    public AuthLoginResponse login(AuthLoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new GlobalException(AuthErrorCode.UNAUTHORIZED_CREDENTIALS));

        if (user.getDeletedAt() != null) {
            throw new GlobalException(AuthErrorCode.USER_IS_DELETED);
        }

        userValidator.validateCurrentPassword(request.password(), user.getPassword());

        String jwt = issueToken(user);

        // Refresh Token 생성 및 저장 로직
        authRepository.findByUserId(user.getId())
                .ifPresent(token -> {
                    authRepository.delete(token);
                    authRepository.flush();
                });

        String refreshTokenValue = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.create(user.getId(), refreshTokenValue);
        authRepository.save(refreshToken);

        return AuthLoginResponse.of(user, jwt);
    }

    @Override
    public void logout(Long userId) {

        authRepository.findByUserId(userId)
                .ifPresent(
                        token -> {
                            authRepository.delete(token);
                            log.info("사용자 ID [{}]의 Refresh Token이 삭제되어 로그아웃 처리되었습니다.", userId);
                        }
                );
    }

    @Override
    public void withdrawUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(UserErrorCode.USER_NOT_FOUND, userId));

        if (user.getDeletedAt() != null) {
            throw new GlobalException(AuthErrorCode.USER_ALREADY_WITHDRAWN);
        }

        user.softDelete();
        userRepository.save(user);

        authRepository.findByUserId(userId)
                .ifPresent(authRepository::delete);

        passwordResetTokenRepository.findByUserId(userId)
                .ifPresent(passwordResetTokenRepository::delete);

        log.info("사용자 ID [{}]의 회원 탈퇴(Soft Delete)가 완료되었으며, 모든 토큰이 무효화되었습니다.", userId);
    }
}