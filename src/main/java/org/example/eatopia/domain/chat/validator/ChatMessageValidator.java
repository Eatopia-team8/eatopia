package org.example.eatopia.domain.chat.validator;

import org.example.eatopia.domain.chat.exception.ChatMessageErrorCode;
import org.example.eatopia.domain.chat.exception.ChatMessageException;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageValidator {

    private static final int MAX_MESSAGE_LENGTH = 500;

    public void validateMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new ChatMessageException(ChatMessageErrorCode.CHAT_MESSAGE_EMPTY);
        }

        String trimmedMessage = message.trim();

        if (trimmedMessage.length() > MAX_MESSAGE_LENGTH) {
            throw new ChatMessageException(ChatMessageErrorCode.CHAT_MESSAGE_TOO_LONG);
        }
    }
}
