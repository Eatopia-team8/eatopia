package org.example.eatopia.common.infra.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        // 1. API 기본 정보 설정
        Info info = new Info().title("Eatopia API")
                .description("Eatopia의 API 명세서입니다.")
                .version("1.0");

        // 2. JWT (Bearer Token) 인증 스키마 정의
        String jwtSchemeName = "bearerAuth"; // SecurityScheme의 이름
        SecurityScheme securityScheme = new SecurityScheme()
                .name(jwtSchemeName)            // 스키마 이름
                .type(SecurityScheme.Type.HTTP) // 인증 타입
                .scheme("bearer")               // 스키마: Bearer
                .bearerFormat("JWT");           // 토큰 형식: JWT

        // 3. API 문서 전체에 보안 요구사항(JWT)을 적용
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(jwtSchemeName); // "bearerAuth" 스키마를 사용하도록 추가

        return new OpenAPI()
                .info(info) // 1. 기본 정보 적용
                .addSecurityItem(securityRequirement) // 3. 모든 엔드포트에 보안 요구사항 적용
                .components(new Components()
                        // 2. "Authorize" 버튼을 생성하는 스키마 정의
                        .addSecuritySchemes(jwtSchemeName, securityScheme)
                );
    }
}