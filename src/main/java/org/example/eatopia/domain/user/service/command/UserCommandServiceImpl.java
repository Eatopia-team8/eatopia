package org.example.eatopia.domain.user.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.auth.exception.AuthErrorCode;
import org.example.eatopia.domain.user.dto.UserEmailForPasswordReset;
import org.example.eatopia.domain.user.dto.UserPasswordChangeRequest;
import org.example.eatopia.domain.user.dto.UserPasswordResetRequest;
import org.example.eatopia.domain.user.entity.PasswordResetToken;
import org.example.eatopia.domain.user.entity.User;
import org.example.eatopia.domain.user.repository.PasswordResetTokenRepository;
import org.example.eatopia.domain.user.repository.UserRepository;
import org.example.eatopia.domain.user.service.query.UserQueryService;
import org.example.eatopia.domain.user.validator.UserValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandServiceImpl implements UserCommandService {

    //테스트를위한 30초짜리 토큰만료시간
    private static final int EXPIRATION_SECONDS = 60;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserValidator userValidator;
    private final UserQueryService userQueryService;

    @Override
    public void changePassword(Long userId, UserPasswordChangeRequest request) {

        // 1. 활성 사용자 조회 및 탈퇴자 검사
        User user = userQueryService.getActiveUserById(userId);

        // 2. 현재 비밀번호 검증
        userValidator.validateCurrentPassword(request.oldPassword(), user.getPassword());

        // 3. 업데이트
        String encodedNewPassword = passwordEncoder.encode(request.newPassword());
        user.updatePassword(encodedNewPassword);
    }


    //이메일로 비밀번호 재설정 토큰을 요청하고 생성
    @Override
    public String requestPasswordResetToken(UserEmailForPasswordReset request) {

        // 1. 활성 사용자 조회 및 탈퇴자 검사
        User user = userQueryService.getActiveUserByEmail(request.email());

        // 2. 기존 토큰 삭제 및 새 토큰 생성/저장
        passwordResetTokenRepository.findByUserId(user.getId())
                .ifPresent(passwordResetTokenRepository::delete);

        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(EXPIRATION_SECONDS);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .userId(user.getId())
                .token(token)
                .expiryDate(expiryDate)
                .build();

        passwordResetTokenRepository.save(resetToken);
        return token;
    }

    //이메일과 재설정 토큰을 사용하여 비밀번호를 재설정
    @Override
    public void resetPassword(UserPasswordResetRequest request) {

        // 1. 활성 사용자 조회 및 탈퇴자 검사
        User user = userQueryService.getActiveUserByEmail(request.email());

        // 2. 재설정 토큰 유효성 검증 (나머지 로직 유지)
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.resetToken())
                .orElseThrow(() -> new GlobalException(AuthErrorCode.INVALID_RESET_TOKEN));

        if (!resetToken.getUserId().equals(user.getId())) {
            throw new GlobalException(AuthErrorCode.INVALID_RESET_TOKEN);
        }

        if (resetToken.isExpired()) {
            throw new GlobalException(AuthErrorCode.EXPIRED_RESET_TOKEN);
        }

        // 3. 업데이트
        String encodedNewPassword = passwordEncoder.encode(request.newPassword());
        user.updatePassword(encodedNewPassword);

        // 4. 토큰 무효화
        resetToken.markAsUsed();
        passwordResetTokenRepository.save(resetToken);
    }
}