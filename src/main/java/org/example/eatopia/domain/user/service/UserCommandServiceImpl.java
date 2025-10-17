package org.example.eatopia.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.common.infra.security.AuthService;
import org.example.eatopia.domain.user.dto.UserSignUpRequest;
import org.example.eatopia.domain.user.dto.UserSignUpResponse;
import org.example.eatopia.domain.user.enttiy.User;
import org.example.eatopia.domain.user.exception.UserErrorCode;
import org.example.eatopia.domain.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @Override
    public UserSignUpResponse signUp(UserSignUpRequest request) {
        // 1. 이메일 중복 확인 (비즈니스 로직)
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new GlobalException(UserErrorCode.DUPLICATE_EMAIL);
        }

        // 2. 비밀번호 암호화 및 User 엔티티 생성 (비즈니스 로직)
        String encodedPassword = passwordEncoder.encode(request.password());
        User user = UserSignUpRequest.toEntity(request, encodedPassword, request.role());

        // 3. 사용자 정보 저장 (비즈니스 로직)
        User savedUser = userRepository.save(user);

        // 4. JWT 토큰 생성 로직 (회원가입 후 바로 발급)
        // 보안/인증 로직을 AuthService에 위임
        String jwt = authService.issueToken(savedUser);

        // 5. 저장된 엔티티와 토큰을 포함하여 응답 DTO로 변환 후 반환 (비즈니스 로직)
        return UserSignUpResponse.of(savedUser, jwt);
    }
}