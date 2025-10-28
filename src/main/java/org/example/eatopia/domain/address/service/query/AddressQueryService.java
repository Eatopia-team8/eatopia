package org.example.eatopia.domain.address.service.query;

import org.example.eatopia.domain.address.dto.AddressResponse;

import java.util.List;

public interface AddressQueryService {

    //현재 로그인한 사용자의 모든 배송지목록을 조회
    List<AddressResponse> getMyAddresses(Long userId);

    //특정 배송지 1건의 상세정보를 조회
    AddressResponse getAddressById(Long userId, Long addressId);

}
