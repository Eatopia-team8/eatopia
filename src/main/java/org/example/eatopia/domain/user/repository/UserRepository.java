package org.example.eatopia.domain.user.repository;

import org.example.eatopia.domain.user.enttiy.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    //이메일로 사용자를 조회하는 메소드
    Optional<User> findByEmail(String email);
}
