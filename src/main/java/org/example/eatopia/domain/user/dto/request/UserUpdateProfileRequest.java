package org.example.eatopia.domain.user.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 사용자 프로필 업데이트 요청 DTO
 * (SELLER는 회사명이 필수로 검증됨, 주소는 선택 사항)
 */
public record UserUpdateProfileRequest(

        @Size(max = 255, message = "주소는 255자를 초과할 수 없습니다.")
        String address,

        @NotNull(message = "회사명은 필수 입력값입니다.") // 회사명 필수 입력
        @Size(max = 100, message = "회사명은 100자를 초과할 수 없습니다.")
        String company
) {
}
