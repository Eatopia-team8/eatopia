package org.example.eatopia.domain.address.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.address.dto.AddressCreateRequest;
import org.example.eatopia.domain.address.dto.AddressResponse;
import org.example.eatopia.domain.address.dto.AddressUpdateRequest;
import org.example.eatopia.domain.address.entity.Address;
import org.example.eatopia.domain.address.repository.AddressRepository;
import org.example.eatopia.domain.user.entity.User;
import org.example.eatopia.domain.user.exception.UserErrorCode;
import org.example.eatopia.domain.user.service.query.UserQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressCommandServiceImpl implements AddressCommandService {

    private final AddressRepository addressRepository;
    private final UserQueryService userQueryService;

    //현재 로그인한 사용자의 새배송지를 생성
    @Override
    public AddressResponse createAddress(Long userId, AddressCreateRequest request) {

        //1. User엔티티 조회
        User user = userQueryService.getActiveUserById(userId);

        //2. 해당 사용자가 동일한주소(주소, 우편번호)를 이미 등록했는지 확인
        if (addressRepository.existsByUserAndAddressAndZipcode(user, request.address(), request.zipcode())) {
            throw new GlobalException(UserErrorCode.DUPLICATE_ADDRESS);
        }

        //3. 새주소엔티티 생성
        Address address = Address.create(
                user,
                request.address(),
                request.zipcode()
        );

        //4. 저장
        Address savedAddress = addressRepository.save(address);

        //5. DTO로 변환하여 반환
        return AddressResponse.from(savedAddress);
    }

    //기존 배송지정보 수정
    @Override
    public AddressResponse updateAddress(Long userId, Long addressId, AddressUpdateRequest request) {
        // 1. User 엔티티 조회
        User user = userQueryService.getActiveUserById(userId);

        // 2. 주소 조회 및 소유권 검증
        Address address = addressRepository.findByIdAndUser(addressId, user)
                .orElseThrow(() -> new GlobalException(UserErrorCode.ADDRESS_NOT_FOUND));

        boolean isAddressChanged = !address.getAddress().equals(request.address());
        boolean isZipcodeChanged = !address.getZipcode().equals(request.zipcode());

        if (!isAddressChanged && !isZipcodeChanged) {
            // 주소와 우편번호가 모두 동일하면 예외 발생
            throw new GlobalException(UserErrorCode.NO_CHANGES_DETECTED);
        }

        // 4. 주소 정보 업데이트
        address.update(
                request.address(),
                request.zipcode()
        );

        // 4. DTO로 변환하여 반환
        return AddressResponse.from(address);
    }

    //배송지 삭제
    @Override
    public void deleteAddress(Long userId, Long addressId) {

        //1. User 엔티티 조회
        User user = userQueryService.getActiveUserById(userId);

        // 2. 주소 조회 및 소유권 검증
        Address address = addressRepository.findByIdAndUser(addressId, user)
                .orElseThrow(() -> new GlobalException(UserErrorCode.ADDRESS_NOT_FOUND));

        //3. DB에서 완전히 삭제
        addressRepository.delete(address);
    }

    //특정배송지를 기본 배송지로 설정
    @Override
    public void setDefaultAddress(Long userId, Long addressId) {

        // 1. User 엔티티 조회
        User user = userQueryService.getActiveUserById(userId);

        // 2. 현재 사용자의 '기존' 기본 배송지를 찾아서 해제
        Optional<Address> currentDefaultOpt = addressRepository.findByUserAndIsDefaultTrue(user);
        currentDefaultOpt.ifPresent(Address::unSetDefault);

        // 3. '새로운' 기본 배송지 조회 및 소유권 검증
        Address newDefaultAddress = addressRepository.findByIdAndUser(addressId, user)
                .orElseThrow(() -> new GlobalException(UserErrorCode.ADDRESS_NOT_FOUND));

        // 4. 새 배송지를 기본값으로 설정
        newDefaultAddress.setDefault();
    }
}
