package org.example.eatopia.domain.chat.service.chatMessage;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.chat.dto.response.ChatMessageResponse;
import org.example.eatopia.domain.chat.entity.ChatMessage;
import org.example.eatopia.domain.chat.entity.ChatRoom;
import org.example.eatopia.domain.chat.exception.ChatRoomErrorCode;
import org.example.eatopia.domain.chat.exception.ChatRoomException;
import org.example.eatopia.domain.chat.repository.ChatMessageRepository;
import org.example.eatopia.domain.chat.repository.ChatRoomRepository;
import org.example.eatopia.domain.chat.validator.ChatMessageValidator;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.example.eatopia.domain.user.entity.User;
import org.example.eatopia.domain.user.service.query.UserQueryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageQueryServiceImpl implements ChatMessageQueryService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserQueryService userQueryService;
    private final ChatMessageValidator chatMessageValidator;

    @Override
    public Slice<ChatMessageResponse> getChatMessages(UserPrincipal authUser, Long roomId, Long cursorId, int size) {

        User user = userQueryService.getUserEntityById(authUser.getId());
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatRoomException(ChatRoomErrorCode.CHAT_ROOM_NOT_FOUND));

        chatMessageValidator.validateParticipant(chatRoom, user);

        int pageSize = size > 0 ? size : 20;

        Pageable pageable = PageRequest.of(0, pageSize);

        Slice<ChatMessage> chatMessages = chatMessageRepository
                .findByChatRoomAndIdLessThanOrderByIdDesc(chatRoom, cursorId, pageable);

        List<ChatMessageResponse> responses = chatMessages.getContent().stream()
                .map(ChatMessageResponse::from)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        Collections.reverse(responses);

        return new SliceImpl<>(responses, chatMessages.getPageable(), chatMessages.hasNext());
    }


}
