package org.example.eatopia.common.infra.config;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.infra.security.JwtProvider;
import org.example.eatopia.common.infra.security.StompAuthChannelInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker // WebSocket 기반 STOMP 메시징 기능 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtProvider jwtProvider;
    private final StompAuthChannelInterceptor stompAuthChannelInterceptor;

    @Value("${websocket.allowed-origins:https://app.eatopia.com}")
    private String allowedOrigins;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // "/queue"로 시작하는 목적지를 가진 메시지를 브로커로 라우팅, "/queue"는 1:1(P2P) 메시징에 사용
        registry.enableSimpleBroker("/queue");

        // @MessageMapping 어노테이션이 붙은 컨트롤러의 메서드로 라우팅 됨
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // STOMP 앤드포인트 설정
        registry.addEndpoint("/chat")
                .setAllowedOrigins(resolveAllowedOrigins())
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompAuthChannelInterceptor);
    }

    private String[] resolveAllowedOrigins() {
        return Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toArray(String[]::new);
    }
}