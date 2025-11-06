package org.example.eatopia.common.infra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // WebSocket 기반 STOMP 메시징 기능 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // "/queue"로 시작하는 목적지를 가진 메시지를 브로커로 라우팅, "/queue"는 1:1(P2P) 메시징에 사용
        registry.enableSimpleBroker("/queue");

        // @MessageMapping 어노테이션이 붙은 컨트롤러의 메서드로 라우팅 됨
        registry.setApplicationDestinationPrefixes("/app");
    }

    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // STOMP 앤드포인트 설정
        // QUESTION: 클라이언트가 WebSocket 연결을 맺을 때 사용할 엔드포인트를 등록합니다.
        // "/ws" 경로로 STOMP 연결을 시도합니다.
        // .withSockJS()는 WebSocket을 지원하지 않는 브라우저에서도
        // 유사한 경험(HTTP 롱폴링 등)을 제공하기 위한 폴백(fallback) 옵션입니다.
        registry.addEndpoint("/chat")
                .setAllowedOriginPatterns("*") // TODO: 프론트엔드 배포 주소로 제한해야 함
                .withSockJS();
    }
}