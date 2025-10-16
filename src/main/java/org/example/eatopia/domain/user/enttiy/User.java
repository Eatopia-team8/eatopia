package org.example.eatopia.domain.user.enttiy;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.eatopia.common.core.entity.SoftDeleteEntity;
import org.example.eatopia.domain.user.config.UserRole;

/**
 * 시스템의 사용자 정보를 나타내는 엔티티
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
    private String password;

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

    /**
     * 회원가입 비즈니스 로직을 처리하는 정적 팩토리 메서드. 사용자를 생성하며 기본 역할(BUYER)을 부여합니다.
     *
     * @param email    사용자 이메일
     * @param password 암호화된 비밀번호
     * @param name     사용자 이름
     * @return 생성된 User 엔티티
     */
    public static User signUp(String email, String password, String name) {
        return User.builder()
                .email(email)
                .password(password)
                .name(name)
                .userRole(UserRole.BUYER)
                .build();
    }

    /**
     * 사용자의 비밀번호를 변경하는 비즈니스 메서드.
     *
     * @param password 변경할 새 비밀번호 (암호화된 상태)
     */
    public void updatePassword(String password) {
        this.password = password;
    }


}
