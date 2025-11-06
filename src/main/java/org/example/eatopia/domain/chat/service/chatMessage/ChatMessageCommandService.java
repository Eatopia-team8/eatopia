package org.example.eatopia.domain.chat.service.chatMessage;

import org.example.eatopia.domain.chat.dto.request.ChatMessageSendRequest;
import org.example.eatopia.domain.user.dto.UserPrincipal;

public interface ChatMessageCommandService {

    void saveAndSendChatMessage(UserPrincipal authUser, Long chatRoomId, ChatMessageSendRequest request);
}
