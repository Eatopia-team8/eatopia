package org.example.eatopia.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.common.infra.security.AuthUtil;
import org.example.eatopia.common.infra.security.JwtProvider;
import org.example.eatopia.domain.user.dto.UserSignUpRequest;
import org.example.eatopia.domain.user.dto.UserSignUpResponse;
import org.example.eatopia.domain.user.enttiy.User;
import org.example.eatopia.domain.user.exception.UserErrorCode;
import org.example.eatopia.domain.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public UserSignUpResponse signUp(UserSignUpRequest request) {
        // 1. 이메일 중복 확인
        if (userRepository.findByEmail(request.email()).isPresent()) {
            // 이미 이메일이 존재하면, 도메인 에러코드를 사용하여 예외 발생
            throw new GlobalException(UserErrorCode.DUPLICATE_EMAIL);
        }

        // 2. 비밀번호 암호화 및 User 엔티티 생성
        String encodedPassword = passwordEncoder.encode(request.password());
        // DTO의 정적 팩토리 메소드를 사용하여 엔티티 생성
        User user = UserSignUpRequest.toEntity(request, encodedPassword, request.role());

        // 3. 사용자 정보 저장
        User savedUser = userRepository.save(user);

        // 4. JWT 토큰 생성 로직 (회원가입 후 바로 발급)

        // 4-1. JWT 생성을 위한 Spring Security 인증 객체(Authentication) 생성
        // Authentication 객체는 토큰에 담길 사용자 정보와 권한을 포함.
        Authentication authentication = AuthUtil.createAuthentication(savedUser);

        Long userId = savedUser.getId();
        String name = savedUser.getName();
        String userEmail = savedUser.getEmail();

        // 4-2. 토큰 생성
        String jwt = jwtProvider.generateToken(authentication, userId, name, userEmail);

        // 5. 저장된 엔티티와 토큰을 포함하여 응답 DTO로 변환 후 반환
        return UserSignUpResponse.of(savedUser, jwt);
    }
}
