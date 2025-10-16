package org.example.eatopia.common.infra.security;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.user.enttiy.User;
import org.example.eatopia.domain.user.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        //이메일로 DB에서 사용자정보를 조회함
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 이메일: " + email + "을 찾을 수 없습니다."));

        // 조회된 사용자 정보(email, password, role)를 Spring Security의 UserDetails 객체로 변환
        // Spring Security는 'ROLE_' 접두사가 붙은 권한 문자열을 요구
        String roleName = user.getUserRole().name();
        String authority = "ROLE_" + roleName;

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(authority))
        );
    }
}
