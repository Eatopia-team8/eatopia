package org.example.eatopia.common.infra.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        // 401 Unauthorized JSON 응답을 반환하는 람다식으로 변경
        return (request, response, authException) -> {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            // 간단한 JSON 오류 메시지를 직접 작성
            response.getWriter().write("{\"success\": false, \"error\": {\"code\": \"AUTH-001\", \"message\": \"인증이 필요합니다.\"}}");
        };
    }

    // --- CORS 설정 Bean 추가 ---
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList("*"));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 적용
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .anonymous(AbstractHttpConfigurer::disable)

                // 인증 실패 처리 핸들러 등록
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint()))

                .addFilterBefore(jwtAuthenticationFilter, AnonymousAuthenticationFilter.class)

                .authorizeHttpRequests(authorize -> authorize
                        // 1. 회원가입, 로그인, 비밀번호 재설정 관련 경로는 모두 허용
                        .requestMatchers(
                                "/v1/auth/signup",
                                "/v1/auth/login",
                                "/v1/users/newpassword-foremail",
                                "/v1/users/password-reset"
                        ).permitAll()

                        // 2. 상품(products)과 카테고리(categories)는 GET (조회) 요청만 허용
                        .requestMatchers(HttpMethod.GET, "/v*/products/**", "/v*/categories/**", "/v*/search/keywords/popular").permitAll()

                        // 3. Swagger 및 정적 리소스 경로 허용
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/v1/users/userInfo").authenticated()

                        // 5. 그 외 모든 요청(POST /v1/products 등)은 인증 필요
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}