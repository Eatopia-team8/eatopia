package org.example.eatopia.domain.auth.dto.signup;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.eatopia.domain.user.config.UserRole;
import org.example.eatopia.domain.user.enttiy.User;

/**
 * 사용자 회원가입 요청 정보를 담는 DTO
 *
 * @param email    사용자 이메일 (계정 ID로 사용)
 * @param password 사용자 비밀번호
 * @param name     사용자 이름
 */
public record AuthSignUpRequest(
        @NotBlank(message = "이메일은 필수 입력 항목입니다.")
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        @Size(max = 50)
        String email,

        @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
        @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
        String password,

        @NotBlank(message = "이름은 필수 입력 항목입니다.")
        @Size(max = 30)
        String name,

        @NotNull(message = "사용자 역할은 필수 항목입니다.")
        UserRole role

) {
    /**
     * 요청 DTO(UserSignUpRequest)를 User 엔티티로 변환
     * <p>
     * 암호화된 비밀번호를 주입받아 User 엔티티의 정적 팩토리 메소드(`signUp`)를 호출
     *
     * @param request         회원가입 요청 DTO
     * @param encodedPassword 암호화된 비밀번호
     * @return User 엔티티
     */
    // toEntity 정적 팩토리 메소드 사용
    public static User toEntity(AuthSignUpRequest request, String encodedPassword, UserRole role) {
        return User.signUp(
                request.email(),
                encodedPassword,
                request.name(),
                role
        );
    }
}