package org.example.eatopia.domain.address.repository;

import org.example.eatopia.domain.address.entity.Address;
import org.example.eatopia.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    //특정사용자의 모든주소목록 조회
    List<Address> findAllByUserOrderByCreatedAtDesc(User user);

    //특정사용자의 기본배송지 조회
    Optional<Address> findByUserAndIsDefaultTrue(User user);

    //주소ID와 사용자ID로 주소를 조회
    Optional<Address> findByIdAndUser(Long id, User user);

    //사용자가 동일한주소를 이미 등록했는지 확인
    boolean existsByUserAndAddressAndZipcode(User user, String address, String zipcode);

}
