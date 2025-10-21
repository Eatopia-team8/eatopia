package org.example.eatopia.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * [USER 도메인] 로그인 없이 비밀번호 재설정 요청 DTO
 */
public record UserPasswordResetRequest(

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "재설정 토큰은 필수입니다.")
        String resetToken, // 이메일로 발송된 임시 토큰 또는 OTP

        @NotBlank(message = "새 비밀번호는 필수입니다.")
        @Size(min = 8, message = "새 비밀번호는 8자 이상이어야 합니다.")
        String newPassword
) {
}