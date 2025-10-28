package org.example.eatopia.domain.address.dto;

import org.example.eatopia.domain.address.entity.Address;

import java.time.LocalDateTime;

public record AddressResponse(

        Long id,
        String address,
        String zipcode,
        boolean isDefault,
        LocalDateTime createdAt

) {

    /**
     * Address 엔티티를 AddressResponse DTO로 변환
     */
    public static AddressResponse from(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getAddress(),
                address.getZipcode(),
                address.isDefault(),
                address.getCreatedAt()
        );
    }
}
