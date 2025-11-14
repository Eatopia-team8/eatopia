package org.example.eatopia.common.infra.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.eatopia.domain.chat.entity.ChatRoom;
import org.example.eatopia.domain.chat.service.chatRoom.ChatRoomQueryService;
import org.example.eatopia.domain.chat.validator.ChatRoomValidator;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.example.eatopia.domain.user.entity.User;
import org.example.eatopia.domain.user.service.query.UserQueryService;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component // 2. @Component로 Bean 등록
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;
    private final ChatRoomValidator chatRoomValidator;
    private final UserQueryService userQueryService;
    private final ChatRoomQueryService chatRoomQueryService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }

        // --- [1. 인증] CONNECT: 현관문 신분증 검사 ---
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authorization = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                throw new AuthenticationCredentialsNotFoundException("Authorization header is missing or invalid");
            }

            String token = authorization.substring(7);
            if (!jwtProvider.validateToken(token)) {
                throw new AuthenticationCredentialsNotFoundException("JWT token validation failed");
            }

            Authentication authentication = jwtProvider.getAuthentication(token);
            if (authentication == null) {
                throw new AuthenticationCredentialsNotFoundException("Unable to authenticate WebSocket client");
            }
            // (중요) 인증된 유저를 세션에 등록
            accessor.setUser(authentication);
            log.info("STOMP CONNECT successful. User: {}", authentication.getName());
        }

        // --- [2. 인가] SUBSCRIBE: 방(Room) 열쇠 검사 ---
        else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            Authentication authentication = (Authentication) accessor.getUser();

            // (중요) 인증되지 않은 사용자의 구독 시도 차단
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new AccessDeniedException("STOMP: Unauthenticated SUBSCRIBE attempt");
            }

            String destination = accessor.getDestination();
            if (destination == null) {
                throw new IllegalArgumentException("STOMP: Destination is null");
            }

            // 4. (핵심 보안 로직) 구독하려는 목적지가 채팅방인지 확인
            //    예: "/queue/chatRoom/123"
            if (destination.startsWith("/queue/chatRoom/")) {
                try {
                    // 5. 목적지에서 roomId 파싱
                    Long roomId = parseRoomIdFrom(destination);

                    // 6. UserPrincipal (ID) 파싱
                    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                    Long userId = userPrincipal.getId();

                    // 7. [검증] 이 유저(userId)가 이 방(roomId)의 참여자가 맞는지 검사
                    User user = userQueryService.getUserEntityById(userId);
                    ChatRoom chatRoom = chatRoomQueryService.getChatRoomById(roomId);

                    chatRoomValidator.validateParticipant(chatRoom, user);
                    log.info("STOMP SUBSCRIBE validation success. User {} to room {}", userId, roomId);

                } catch (Exception e) {
                    // 8. 검증 실패 시 예외를 던져 구독을 강제 중단
                    log.warn("STOMP SUBSCRIBE validation failed: {}", e.getMessage());
                    throw new AccessDeniedException("STOMP: No permission to subscribe " + destination);
                }
            }
        }

        return message;
    }

    // 예시: 목적지에서 roomId 파싱
    private Long parseRoomIdFrom(String destination) {
        try {
            return Long.parseLong(destination.substring(destination.lastIndexOf('/') + 1));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid chat room destination: " + destination);
        }
    }
}