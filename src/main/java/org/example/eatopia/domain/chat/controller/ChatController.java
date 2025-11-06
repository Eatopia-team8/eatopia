package org.example.eatopia.domain.chat.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.chat.dto.request.ChatMessageSendRequest;
import org.example.eatopia.domain.chat.dto.response.ChatMessageResponse;
import org.example.eatopia.domain.chat.dto.response.ChatRoomResponse;
import org.example.eatopia.domain.chat.service.chatMessage.ChatMessageCommandService;
import org.example.eatopia.domain.chat.service.chatMessage.ChatMessageQueryService;
import org.example.eatopia.domain.chat.service.chatRoom.ChatRoomCommandService;
import org.example.eatopia.domain.chat.service.chatRoom.ChatRoomQueryService;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.springframework.data.domain.Slice;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ChatController {

    private final ChatRoomCommandService chatRoomCommandService;
    private final ChatRoomQueryService chatRoomQueryService;
    private final ChatMessageCommandService chatMessageCommandService;
    private final ChatMessageQueryService chatMessageQueryService;

    @PostMapping("/v1/chatrooms/{recipientId}")
    public Response<ChatRoomResponse> getOrCreateChatRoom(@AuthenticationPrincipal UserPrincipal authUser,
                                                          @PathVariable Long recipientId) {

        ChatRoomResponse response = chatRoomCommandService.getOrCreateChatRoom(authUser, recipientId);

        return Response.success(response);
    }

    @GetMapping("/v1/chatrooms")
    public Response<List<ChatRoomResponse>> getChatRooms(@AuthenticationPrincipal UserPrincipal authUser) {

        List<ChatRoomResponse> responses = chatRoomQueryService.getChatRooms(authUser);

        return Response.success(responses);
    }

    @GetMapping("/rooms/{roomId}/messages")
    public Response<Slice<ChatMessageResponse>> getChatHistory(@AuthenticationPrincipal UserPrincipal authUser,
                                                               @PathVariable Long roomId,
                                                               @RequestParam(required = false) Long cursorId,
                                                               @RequestParam(defaultValue = "20") int size) {

        Slice<ChatMessageResponse> responses = chatMessageQueryService.getChatMessages(authUser, roomId, cursorId, size);

        return Response.success(responses);
    }

    @MessageMapping("/chat/rooms/{roomId}")
    public void sendChatMessage(@AuthenticationPrincipal UserPrincipal authUser,
                                @PathVariable Long roomId,
                                @Valid @RequestBody ChatMessageSendRequest request) {

        chatMessageCommandService.saveAndSendChatMessage(authUser, roomId, request);
    }
}