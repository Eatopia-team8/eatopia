package org.example.eatopia.domain.address.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.eatopia.common.core.entity.BaseEntity;
import org.example.eatopia.domain.user.entity.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "addresses")
public class Address extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String address;

    @Column(nullable = false, length = 50)
    private String zipcode;

    @Column(nullable = false)
    private boolean isDefault;

    @Builder
    private Address(User user, String address, String zipcode, boolean isDefault) {
        this.user = user;
        this.address = address;
        this.zipcode = zipcode;
        this.isDefault = isDefault;
    }

    //정적 팩토리 메소드
    public static Address create(User user, String address, String zipcode) {
        return Address.builder()
                .user(user)
                .address(address)
                .zipcode(zipcode)
                .isDefault(false)
                .build();
    }

    //주소 정보를 수정하는 비즈니스 메소드
    public void update(String address, String zipcode) {
        this.address = address;
        this.zipcode = zipcode;
    }

    //이주소를 기본배송지로 설정
    public void setDefault() {
        this.isDefault = true;
    }

    //이주소를 기본배송지에서 해제함
    public void unSetDefault() {
        this.isDefault = false;
    }

}
