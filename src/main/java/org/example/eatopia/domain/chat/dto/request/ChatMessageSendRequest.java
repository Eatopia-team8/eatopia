package org.example.eatopia.domain.chat.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChatMessageSendRequest(@NotBlank(message = "메시지를 입력해주세요.") String message) {

    public static ChatMessageSendRequest of(String message) {
        return new ChatMessageSendRequest(message);
    }
}
