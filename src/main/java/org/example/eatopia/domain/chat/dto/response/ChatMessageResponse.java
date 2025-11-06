package org.example.eatopia.domain.chat.dto.response;

import org.example.eatopia.domain.chat.entity.ChatMessage;

import java.time.LocalDateTime;

public record ChatMessageResponse(Long messageId,
                                  Long senderId,
                                  String senderName,
                                  String message,
                                  LocalDateTime sentAt
) {

    public static ChatMessageResponse of(Long messageId,
                                         Long senderId,
                                         String senderName,
                                         String message,
                                         LocalDateTime sentAt) {
        return new ChatMessageResponse(messageId, senderId, senderName, message, sentAt);
    }

    public static ChatMessageResponse from(ChatMessage chatMessage) {
        return new ChatMessageResponse(
                chatMessage.getId(),
                chatMessage.getSender().getId(),
                chatMessage.getSender().getName(),
                chatMessage.getMessage(),
                chatMessage.getSentAt()
        );
    }
}
