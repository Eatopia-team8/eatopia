package org.example.eatopia.domain.address.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressUpdateRequest(

        @NotBlank(message = "상세 주소는 필수입니다.")
        @Size(max = 100)
        String address,

        @NotBlank(message = "우편번호는 필수입니다.")
        @Size(max = 50)
        String zipcode

) {
}
