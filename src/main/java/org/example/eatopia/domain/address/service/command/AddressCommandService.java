package org.example.eatopia.domain.address.service.command;

import org.example.eatopia.domain.address.dto.AddressCreateRequest;
import org.example.eatopia.domain.address.dto.AddressResponse;
import org.example.eatopia.domain.address.dto.AddressUpdateRequest;

public interface AddressCommandService {

    //현재 로그인한 사용자의 새 배송지를 생성
    AddressResponse createAddress(Long userId, AddressCreateRequest request);

    //기존 배송지정보를 수정
    AddressResponse updateAddress(Long userId, Long addressId, AddressUpdateRequest request);

    //배송지를 삭제
    void deleteAddress(Long userId, Long addressId);

    //특정 배송지를 기본배송지로 설정
    void setDefaultAddress(Long userId, Long addressId);

}
