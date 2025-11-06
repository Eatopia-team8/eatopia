package org.example.eatopia.domain.chat.dto.response;

import org.example.eatopia.domain.chat.entity.ChatRoom;

import java.time.LocalDateTime;

public record ChatRoomResponse(Long id,
                               LocalDateTime createdAt) {

    public static ChatRoomResponse from(ChatRoom chatRoom) {
        
        return new ChatRoomResponse(chatRoom.getId(), chatRoom.getCreatedAt());
    }
}
