package org.example.eatopia.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
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

    @Override
    @Transactional
    public UserSignUpResponse signUp(UserSignUpRequest request) {
        // 이메일 중복 확인
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new GlobalException(UserErrorCode.DUPLICATE_EMAIL);
        }

        // 비밀번호 암호화 및 User 엔티티 생성 (DTO 정적 팩토리 메소드 사용)
        String encodedPassword = passwordEncoder.encode(request.password());
        User user = UserSignUpRequest.toEntity(request, encodedPassword);

        // 사용자 정보 저장
        User savedUser = userRepository.save(user);

        // 중간 변수를 활용하여 응답 DTO 생성 및 반환 (DTO 정적 팩토리 메소드 사용)
        UserSignUpResponse response = UserSignUpResponse.from(savedUser);
        return response;
    }

}
