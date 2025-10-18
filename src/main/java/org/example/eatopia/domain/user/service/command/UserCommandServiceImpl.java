package org.example.eatopia.domain.user.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.auth.exception.AuthErrorCode;
import org.example.eatopia.domain.user.dto.UserPasswordChangeRequest;
import org.example.eatopia.domain.user.dto.UserPasswordResetRequest;
import org.example.eatopia.domain.user.enttiy.PasswordResetToken;
import org.example.eatopia.domain.user.enttiy.User;
import org.example.eatopia.domain.user.exception.UserErrorCode;
import org.example.eatopia.domain.user.repository.PasswordResetTokenRepository;
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
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    public void changePassword(Long userId, UserPasswordChangeRequest request) {
        //1. 사용자조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(UserErrorCode.USER_NOT_FOUND, userId));

        //2. 현재 비밀번호(OldPassword)검증
        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            //비밀번호 불일치 시 Error띄우기
            throw new GlobalException(AuthErrorCode.UNAUTHORIZED_CREDENTIALS);
        }

        //3. 새 비밀번호(NewPassword) 인코딩
        String encodedNewPassword = passwordEncoder.encode(request.newPassword());

        //4. 비밀번호 업데이트 및 저장
        user.updatePassword(encodedNewPassword);
    }

    /**
     * [로그인 불필요] 이메일과 재설정 토큰을 사용하여 비밀번호를 재설정
     */
    @Override
    public void resetPassword(UserPasswordResetRequest request) {
        // 1. 사용자 조회 (Email 사용)
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new GlobalException(UserErrorCode.USER_NOT_FOUND, request.email()));

        // 2. 재설정 토큰 유효성 검증
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.resetToken())
                .orElseThrow(() -> new GlobalException(AuthErrorCode.UNAUTHORIZED_CREDENTIALS)); // 토큰 값 자체가 DB에 없음

        // a. 토큰의 사용자 ID가 일치하는지 확인 (보안 체크)
        if (!resetToken.getUserId().equals(user.getId())) {
            // 이메일은 맞지만 토큰이 다른 사용자 소유인 경우
            throw new GlobalException(AuthErrorCode.UNAUTHORIZED_CREDENTIALS);
        }

        // b. 토큰 만료 확인
        if (resetToken.isExpired()) {
            // 만료된 토큰 처리
            throw new GlobalException(AuthErrorCode.UNAUTHORIZED_CREDENTIALS);
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