package org.example.eatopia.domain.chat.service.chatMessage;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.chat.dto.request.ChatMessageSendRequest;
import org.example.eatopia.domain.chat.dto.response.ChatMessageResponse;
import org.example.eatopia.domain.chat.entity.ChatMessage;
import org.example.eatopia.domain.chat.entity.ChatRoom;
import org.example.eatopia.domain.chat.exception.ChatRoomErrorCode;
import org.example.eatopia.domain.chat.exception.ChatRoomException;
import org.example.eatopia.domain.chat.repository.ChatMessageRepository;
import org.example.eatopia.domain.chat.repository.ChatRoomRepository;
import org.example.eatopia.domain.chat.validator.ChatMessageValidator;
import org.example.eatopia.domain.chat.validator.ChatRoomValidator;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.example.eatopia.domain.user.entity.User;
import org.example.eatopia.domain.user.service.query.UserQueryService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatMessageCommandServiceImpl implements ChatMessageCommandService {

    private final SimpMessagingTemplate messagingTemplate;

    private final UserQueryService userQueryService;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomValidator chatRoomValidator;
    private final ChatMessageValidator chatMessageValidator;

    public void saveAndSendChatMessage(UserPrincipal authUser,
                                       Long chatRoonId,
                                       ChatMessageSendRequest request) {

        User sender = userQueryService.getUserEntityById(authUser.getId());
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoonId)
                .orElseThrow(() -> new ChatRoomException(ChatRoomErrorCode.CHAT_ROOM_NOT_FOUND));

        chatRoomValidator.validateParticipant(chatRoom, sender);
        chatMessageValidator.validateMessage(request.message());

        ChatMessage chatMessage = new ChatMessage(
                chatRoom,
                sender,
                request.message()
        );
        chatMessageRepository.save(chatMessage);

        ChatMessageResponse response = ChatMessageResponse.from(chatMessage);

        // 4. (핵심) /queue/chatRoom/{roomId} 목적지를 구독(Subscribe) 중인
        //    클라이언트에게 메시지 DTO를 전송합니다.
        //    (상대방과 나 자신 모두에게 전송됩니다. 프론트엔드에서 처리)
        messagingTemplate.convertAndSend("/queue/chatRoom/" + chatRoom.getId(), response);
    }
}