package org.example.eatopia.domain.user.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.auth.exception.AuthErrorCode;
import org.example.eatopia.domain.user.dto.UserEmailForPasswordReset;
import org.example.eatopia.domain.user.dto.UserPasswordChangeRequest;
import org.example.eatopia.domain.user.dto.UserPasswordResetRequest;
import org.example.eatopia.domain.user.entity.PasswordResetToken;
import org.example.eatopia.domain.user.entity.User;
import org.example.eatopia.domain.user.exception.UserErrorCode;
import org.example.eatopia.domain.user.repository.PasswordResetTokenRepository;
import org.example.eatopia.domain.user.repository.UserRepository;
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

    private static final int EXPIRATION_HOURS = 24; // 토큰 만료 시간 (24시간)

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserValidator userValidator;

    @Override
    public void changePassword(Long userId, UserPasswordChangeRequest request) {
        //1. 사용자조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(UserErrorCode.USER_NOT_FOUND, userId));

        //2. 현재 비밀번호(OldPassword)검증
        userValidator.validateCurrentPassword(request.oldPassword(), user.getPassword());

        //3. 새 비밀번호(NewPassword) 인코딩
        String encodedNewPassword = passwordEncoder.encode(request.newPassword());

        //4. 비밀번호 업데이트 및 저장
        user.updatePassword(encodedNewPassword);
    }


    //이메일로 비밀번호 재설정 토큰을 요청하고 생성
    @Override
    public String requestPasswordResetToken(UserEmailForPasswordReset request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new GlobalException(UserErrorCode.USER_NOT_FOUND, request.email()));

        passwordResetTokenRepository.findByUserId(user.getId())
                .ifPresent(passwordResetTokenRepository::delete);

        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(EXPIRATION_HOURS);

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
        // 1. 사용자 조회 (Email 사용)
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new GlobalException(UserErrorCode.USER_NOT_FOUND, request.email()));

        // 2. 재설정 토큰 유효성 검증
        //토큰 값 자체가 DB에 없는 경우: INVALID_RESET_TOKEN 사용
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.resetToken())
                .orElseThrow(() -> new GlobalException(AuthErrorCode.INVALID_RESET_TOKEN));

        // a. 토큰의 사용자 ID가 일치하는지 확인 (보안 체크)
        if (!resetToken.getUserId().equals(user.getId())) {
            //토큰은 있지만 소유자가 다른 경우: INVALID_RESET_TOKEN 사용
            throw new GlobalException(AuthErrorCode.INVALID_RESET_TOKEN);
        }

        // b. 토큰 만료 확인
        if (resetToken.isExpired()) {
            //만료된 토큰 처리: EXPIRED_RESET_TOKEN 사용
            throw new GlobalException(AuthErrorCode.EXPIRED_RESET_TOKEN);
        }

        // 3. 새 비밀번호 인코딩
        String encodedNewPassword = passwordEncoder.encode(request.newPassword());

        // 4. 비밀번호 업데이트
        user.updatePassword(encodedNewPassword);

        // 5. 사용된 재설정 토큰을 무효화 (재사용 방지)
        resetToken.markAsUsed();
        passwordResetTokenRepository.save(resetToken);
    }

}