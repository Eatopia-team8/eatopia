package org.example.eatopia.domain.chat.service.chatMessage;

import org.example.eatopia.domain.chat.dto.response.ChatMessageResponse;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.springframework.data.domain.Slice;

public interface ChatMessageQueryService {

    Slice<ChatMessageResponse> getChatMessages(UserPrincipal authUser, Long roomId, Long cursorId, int size);
}
