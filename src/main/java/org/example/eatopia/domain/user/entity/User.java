package org.example.eatopia.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.eatopia.common.core.entity.SoftDeleteEntity;
import org.example.eatopia.domain.user.config.UserRole;

/**
 * 시스템의 사용자 정보를 나타내는 엔티티
 * (사용자 속성 및 관리 책임)
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends SoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(length = 255)
    private String address;

    @Column(length = 10)
    private String zipcode;

    @Column(length = 100)
    private String company;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String password; // 암호화된 비밀번호 저장

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private UserRole userRole;

    @Builder(access = AccessLevel.PRIVATE)
    private User(String email, String password, UserRole userRole, String name) {
        this.email = email;
        this.password = password;
        this.userRole = userRole;
        this.name = name;
    }

    //회원가입 비즈니스 로직을 처리하는 정적 팩토리메서드
    public static User signUp(String email, String password, String name, UserRole userRole) {
        return User.builder()
                .email(email)
                .password(password)
                .name(name)
                .userRole(userRole)
                .build();
    }

    //사용자의 비밀번호를 변경하는 비즈니스 메서드
    public void updatePassword(String password) {
        this.password = password;
    }

    //관리자인지 확인하는 비즈니스 메서드
    public boolean isAdmin() {
        return this.userRole == UserRole.ADMIN;
    }

    //판매자인지 확인하는 비즈니스 메서드
    public boolean isSeller() {
        return this.userRole == UserRole.SELLER;
    }

    //사용자의 프로필 정보(주소, 회사명)를 업데이트하는 비즈니스 메서드
    public void updateProfile(String address, String company) {
        if (address != null) {
            this.address = address;
        }
        if (this.isAdmin() || this.isSeller()) {
            if (company != null) {
                this.company = company;
            }
        }
    }
}