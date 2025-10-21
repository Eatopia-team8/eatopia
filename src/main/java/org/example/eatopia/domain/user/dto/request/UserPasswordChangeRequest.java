package org.example.eatopia.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserPasswordChangeRequest(

        @NotBlank(message = "현재 비밀번호는 필수입니다.")
        String oldPassword,

        @NotBlank(message = "새 비밀번호는 필수입니다.")
        @Size(min = 8, message = "새 비밀번호는 8자 이상이어야 합니다.")
        String newPassword
) {
}

