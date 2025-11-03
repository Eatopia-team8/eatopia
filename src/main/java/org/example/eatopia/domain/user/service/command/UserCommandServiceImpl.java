package org.example.eatopia.domain.user.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.consts.Const;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.common.infra.mail.MailService;
import org.example.eatopia.domain.auth.exception.AuthErrorCode;
import org.example.eatopia.domain.user.dto.request.UserMailRequest;
import org.example.eatopia.domain.user.dto.request.UserPasswordChangeRequest;
import org.example.eatopia.domain.user.dto.request.UserPasswordResetRequest;
import org.example.eatopia.domain.user.dto.request.UserUpdateProfileRequest;
import org.example.eatopia.domain.user.entity.PasswordResetToken;
import org.example.eatopia.domain.user.entity.User;
import org.example.eatopia.domain.user.exception.UserErrorCode;
import org.example.eatopia.domain.user.repository.PasswordResetTokenRepository;
import org.example.eatopia.domain.user.repository.UserRepository;
import org.example.eatopia.domain.user.service.query.UserQueryService;
import org.example.eatopia.domain.user.validator.UserValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserValidator userValidator;
    private final UserQueryService userQueryService;
    private final MailService mailService;

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

    @Override
    public void newPasswordForEmail(UserMailRequest request) {

        // 1. 활성 사용자 조회 및 탈퇴자 검사
        User user = userQueryService.getActiveUserByEmail(request.email());

        // 2. 기존 토큰 확인 및 쿨다운 검사
        Optional<PasswordResetToken> existingTokenOpt = passwordResetTokenRepository.findByUserId(user.getId());

        if (existingTokenOpt.isPresent()) {
            PasswordResetToken existingToken = existingTokenOpt.get();
            LocalDateTime coolDownTime = existingToken.getCreatedAt().plusMinutes(Const.RE_ISSUE_COOL_DOWN_MINUTES);

            if (LocalDateTime.now().isBefore(coolDownTime)) {
                throw new GlobalException(AuthErrorCode.TOKEN_ALREADY_ISSUED);
            } else {
                passwordResetTokenRepository.delete(existingToken);
                passwordResetTokenRepository.flush();
            }
        }

        // 3. 새 토큰 생성 및 저장
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(Const.RESET_TOKEN_EXPIRATION_SECONDS);
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .userId(user.getId())
                .token(token)
                .expiryDate(expiryDate)
                .build();
        passwordResetTokenRepository.save(resetToken);

        // 4. MailService 호출
        mailService.sendPasswordResetMail(user.getEmail(), token);
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

    @Override
    public void updateProfile(Long id, UserUpdateProfileRequest request) {

        //1. 활성 사용자 조회 및 탈퇴자 검사
        User user = userQueryService.getActiveUserById(id);

        // 2. BUYER가 company 필드를 보냈는지 검사
        if (user.getUserRole().name().equals("BUYER")) {
            // company 값이 null이 아닌 경우(즉, JSON에 포함되어 넘어온 경우)
            if (request.company() != null) { // company가 null이 아니면 예외 발생
                throw new GlobalException(UserErrorCode.USER_DONT_INPUT_COMPANY_NAME);
            }
        }

        // 3. SELLER 역할일 때 company 필수 검증 (ADMIN은 자동 통과)
        if (user.isSeller() || user.isAdmin()) {
            if (request.company() == null || request.company().trim().isEmpty()) { // 회사명이 null이거나 비어있으면 예외 발생
                throw new GlobalException(UserErrorCode.INVALID_INPUT, "판매자(SELLER)는 회사명을 반드시 입력해야 합니다.");
            }
        }

        //3. 업데이트 실행
        user.updateProfile(request.address(), request.company());
    }
}