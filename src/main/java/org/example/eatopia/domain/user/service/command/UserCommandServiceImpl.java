package org.example.eatopia.domain.user.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserValidator userValidator;
    private final UserQueryService userQueryService;
    private final MailService mailService;
    private final CacheManager cacheManager;

    private void evictUserCache(Long userId, String email) {

        User user = userQueryService.getUserEntityById(userId);

        Cache usersCache = cacheManager.getCache("users");
        if (usersCache != null) {
            usersCache.evictIfPresent(userId);
            usersCache.evictIfPresent(user.getEmail());
        }
    }

    @Override
    public void changePassword(Long userId, UserPasswordChangeRequest request) {

        User user = userQueryService.getActiveUserById(userId);

        userValidator.validateCurrentPassword(request.oldPassword(), user.getPassword());

        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new GlobalException(UserErrorCode.PASSWORD_IS_SAME);
        }

        String encodedNewPassword = passwordEncoder.encode(request.newPassword());
        user.updatePassword(encodedNewPassword);

        evictUserCache(userId, user.getEmail());
    }

    @Override
    public void newPasswordForEmail(UserMailRequest request) {

        User user = userQueryService.getActiveUserByEmail(request.email());

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

        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(Const.RESET_TOKEN_EXPIRATION_SECONDS);
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .userId(user.getId())
                .token(token)
                .expiryDate(expiryDate)
                .build();
        passwordResetTokenRepository.save(resetToken);

        mailService.sendPasswordResetMail(user.getEmail(), token);
    }

    @Override
    public void resetPassword(UserPasswordResetRequest request) {

        User user = userQueryService.getActiveUserByEmail(request.email());

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.resetToken())
                .orElseThrow(() -> new GlobalException(AuthErrorCode.INVALID_RESET_TOKEN));

        if (!resetToken.getUserId().equals(user.getId())) {
            throw new GlobalException(AuthErrorCode.INVALID_RESET_TOKEN);
        }

        if (resetToken.isExpired()) {
            throw new GlobalException(AuthErrorCode.EXPIRED_RESET_TOKEN);
        }

        String encodedNewPassword = passwordEncoder.encode(request.newPassword());
        user.updatePassword(encodedNewPassword);

        resetToken.markAsUsed();
        passwordResetTokenRepository.save(resetToken);

        evictUserCache(user.getId(), user.getEmail());
    }

    @Override
    public void updateProfile(Long id, UserUpdateProfileRequest request) {

        User user = userQueryService.getActiveUserById(id);

        if (user.getUserRole().name().equals("BUYER")) {
            if (request.company() != null) {
                throw new GlobalException(UserErrorCode.USER_DONT_INPUT_COMPANY_NAME);
            }
        }

        if (user.isSeller() || user.isAdmin()) {
            if (request.company() == null || request.company().trim().isEmpty()) {
                throw new GlobalException(UserErrorCode.INVALID_INPUT, "판매자(SELLER)는 회사명을 반드시 입력해야 합니다.");
            }
        }

        user.updateProfile(request.address(), request.company());

        evictUserCache(id, user.getEmail());
    }
}