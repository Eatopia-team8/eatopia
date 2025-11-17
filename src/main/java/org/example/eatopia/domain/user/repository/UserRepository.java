package org.example.eatopia.domain.user.repository;

import org.example.eatopia.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE (u.email LIKE %:keyword% OR u.name LIKE %:keyword%) AND u.deletedAt IS NULL")
    Page<User> searchByEmailOrNameAndNotDeleted(@Param("keyword") String keyword, Pageable pageable);
}
