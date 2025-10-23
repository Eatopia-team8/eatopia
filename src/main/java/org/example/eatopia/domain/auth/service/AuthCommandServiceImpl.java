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

    // 회원가입 시 허용할 역할 목록
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

    //JWT 토큰 발행 로직을 분리하여 중복을 제거
    private String issueToken(User user) {
        JwtPayload payload = createJwtPayloadFromUser(user);
        return jwtProvider.createToken(payload);
    }


    //회원가입 및 토큰 발행에 필요한 JwtPayload를 User 엔티티로부터 생성
    private JwtPayload createJwtPayloadFromUser(User user) {

        // 사용자 권한 정보를 GrantedAuthority 컬렉션으로 변환
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

        // 1. 역할(Role) 유효성 검증
        if (!ALLOWED_SIGNUP_ROLES.contains(request.role())) {
            throw new GlobalException(AuthErrorCode.ACCESS_DENIED, "허용되지 않은 사용자 역할입니다.");
        }

        // 2. 이메일 중복 확인
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new GlobalException(UserErrorCode.DUPLICATE_EMAIL);
        }

        // 3. 비밀번호 인코딩 및 User 엔티티 생성
        String encodedPassword = passwordEncoder.encode(request.password());
        User user = User.signUp(
                request.email(),
                encodedPassword,
                request.name(),
                request.role()
        );

        // 4. 사용자 정보 저장
        User savedUser = userRepository.save(user);

        // 5. JWT 토큰 생성 (issueToken 헬퍼 메서드 사용)
        String jwt = issueToken(savedUser);

        // 6. 응답 DTO로 변환 후 반환
        return AuthSignUpResponse.of(savedUser, jwt);
    }

    @Override
    public AuthLoginResponse login(AuthLoginRequest request) {

        // 1. 탈퇴자를 포함하여 이메일로 사용자 조회
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new GlobalException(AuthErrorCode.UNAUTHORIZED_CREDENTIALS));

        // 2. 찾은 사용자가 탈퇴자인지 확인
        if (user.getDeletedAt() != null) {
            throw new GlobalException(AuthErrorCode.USER_IS_DELETED);
        }

        // 3. 비밀번호 검증 (Validator 사용)
        userValidator.validateCurrentPassword(request.password(), user.getPassword());

        // 4. JWT (Access Token) 발급
        String jwt = issueToken(user);

        // 5. Refresh Token 생성 및 저장 로직 추가
        authRepository.findByUserId(user.getId())
                .ifPresent(authRepository::delete);
        authRepository.flush();

        String refreshTokenValue = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.create(user.getId(), refreshTokenValue);
        authRepository.save(refreshToken);

        // 6. 응답 DTO로 변환
        return AuthLoginResponse.of(user, jwt);
    }

    @Override
    public void logout(Long userId) {

        authRepository.findByUserId(userId)
                .ifPresentOrElse(
                        //Refresh Token이 존재할 경우 삭제 및 로그 기록
                        token -> {
                            authRepository.delete(token);
                            log.info("사용자 ID [{}]의 Refresh Token이 삭제되어 로그아웃 처리되었습니다.", userId);
                        },
                        //Refresh Token이 이미 없는 경우, 아무것도 하지 않고 조용히 종료
                        () -> {
                            throw new GlobalException(AuthErrorCode.USER_ALREADY_LOGGED_OUT);
                        }
                );
    }

    @Override
    public void withdrawUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(UserErrorCode.USER_NOT_FOUND, userId));

        //이미 탈퇴된 사용자인지 확인
        if (user.getDeletedAt() != null) {
            throw new GlobalException(AuthErrorCode.USER_ALREADY_WITHDRAWN);
        }

        //1. Soft Delete처리
        user.softDelete();
        userRepository.save(user);

        //2. Refresh Token 무효화
        authRepository.findByUserId(userId)
                .ifPresent(authRepository::delete);

        //3. 비밀번호 재설정 토큰 무효화
        passwordResetTokenRepository.findByUserId(userId)
                .ifPresent(passwordResetTokenRepository::delete);

        log.info("사용자 ID [{}]의 회원 탈퇴(Soft Delete)가 완료되었으며, 모든 토큰이 무효화되었습니다.", userId);
    }
}
