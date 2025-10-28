package org.example.eatopia.domain.address.service.query;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.address.dto.AddressResponse;
import org.example.eatopia.domain.address.entity.Address;
import org.example.eatopia.domain.address.repository.AddressRepository;
import org.example.eatopia.domain.user.entity.User;
import org.example.eatopia.domain.user.exception.UserErrorCode;
import org.example.eatopia.domain.user.service.query.UserQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressQueryServiceImpl implements AddressQueryService {


    private final AddressRepository addressRepository;
    private final UserQueryService userQueryService;

    //현재 로그인한 사용자의 모든 배송지목록을 조회
    @Override
    public List<AddressResponse> getMyAddresses(Long userId) {
        // 1. User 엔티티 조회
        User user = userQueryService.getActiveUserById(userId);

        // 2. 해당 사용자의 주소 목록 전체 조회
        List<Address> addresses = addressRepository.findAllByUserOrderByCreatedAtDesc(user);

        // 3. DTO 리스트로 변환하여 반환
        return addresses.stream()
                .map(AddressResponse::from)
                .collect(Collectors.toList());
    }

    //특정 배송지 1건의 상세정보 조회
    @Override
    public AddressResponse getAddressById(Long userId, Long addressId) {
        // 1. User 엔티티 조회
        User user = userQueryService.getActiveUserById(userId);

        // 2. 주소 ID와 사용자 ID로 주소 조회
        Address address = addressRepository.findByIdAndUser(addressId, user)
                .orElseThrow(() -> new GlobalException(UserErrorCode.ADDRESS_NOT_FOUND));

        // 3. DTO로 변환하여 반환
        return AddressResponse.from(address);
    }

}
