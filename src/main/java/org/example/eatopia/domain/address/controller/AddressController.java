package org.example.eatopia.domain.address.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.address.dto.AddressCreateRequest;
import org.example.eatopia.domain.address.dto.AddressResponse;
import org.example.eatopia.domain.address.dto.AddressUpdateRequest;
import org.example.eatopia.domain.address.service.command.AddressCommandService;
import org.example.eatopia.domain.address.service.query.AddressQueryService;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/addresses")
public class AddressController {

    private final AddressCommandService addressCommandService;
    private final AddressQueryService addressQueryService;

    //현재 로그인한 사용자의 모든 배송지목록 조회
    @GetMapping("/check-address")
    public ResponseEntity<Response<List<AddressResponse>>> getMyAddresses(
            @AuthenticationPrincipal UserPrincipal authUser
    ) {
        List<AddressResponse> response = addressQueryService.getMyAddresses(authUser.getId());
        return ResponseEntity.ok(Response.success(response));
    }

    //특정 배송지 1건의 상세정보 조회
    @GetMapping("/check-detailAddress/{addressId}")
    public ResponseEntity<Response<AddressResponse>> getAddressById(
            @AuthenticationPrincipal UserPrincipal authUser,
            @PathVariable Long addressId
    ) {
        AddressResponse response = addressQueryService.getAddressById(authUser.getId(), addressId);
        return ResponseEntity.ok(Response.success(response));
    }

    //현재 로그인한 사용자의 새배송지를 생성
    @PostMapping("/create-address")
    public ResponseEntity<Response<AddressResponse>> createAddress(
            @AuthenticationPrincipal UserPrincipal authUser,
            @Valid @RequestBody AddressCreateRequest request
    ) {
        AddressResponse response = addressCommandService.createAddress(authUser.getId(), request);
        return ResponseEntity.ok(Response.success(response));
    }

    //기존 배송지정보 수정
    @PatchMapping("/update-address/{addressId}")
    public ResponseEntity<Response<AddressResponse>> updateAddress(
            @AuthenticationPrincipal UserPrincipal authUser,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressUpdateRequest request
    ) {
        AddressResponse response = addressCommandService.updateAddress(authUser.getId(), addressId, request);
        return ResponseEntity.ok(Response.success(response));
    }

    //배송지 삭제
    @DeleteMapping("/delete-address/{addressId}")
    public ResponseEntity<Response<Void>> deleteAddress(
            @AuthenticationPrincipal UserPrincipal authUser,
            @PathVariable Long addressId
    ) {
        addressCommandService.deleteAddress(authUser.getId(), addressId);
        return ResponseEntity.ok(Response.success());
    }

    //특정 배송지를 기본배송지로 설정
    @PatchMapping("/set-address/{addressId}/default")
    public ResponseEntity<Response<Void>> setDefaultAddress(
            @AuthenticationPrincipal UserPrincipal authUser,
            @PathVariable Long addressId
    ) {
        addressCommandService.setDefaultAddress(authUser.getId(), addressId);
        return ResponseEntity.ok(Response.success());
    }
}
