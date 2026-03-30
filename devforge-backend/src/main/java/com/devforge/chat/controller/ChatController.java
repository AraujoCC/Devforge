package com.devforge.chat.controller;

import com.devforge.chat.model.ChatRoom;
import com.devforge.chat.model.Message;
import com.devforge.chat.service.ChatService;
import com.devforge.user.model.User;
import com.devforge.user.repository.UserRepository;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository; // ✅ CORRETO

    // ----- REST endpoints -----

    @PostMapping("/room/{targetUserId}")
    public ResponseEntity<ChatRoom> getOrCreateRoom(
            @PathVariable Long targetUserId,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(
                chatService.getOrCreateRoom(user.getId(), targetUserId)
        );
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoom>> getMyRooms(
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(
                chatService.getUserRooms(user.getId())
        );
    }

    @GetMapping("/room/{roomId}/messages")
    public ResponseEntity<Page<Message>> getMessages(
            @PathVariable Long roomId,
            @PageableDefault(size = 50) Pageable pageable) {

        return ResponseEntity.ok(
                chatService.getMessages(roomId, pageable)
        );
    }

    // ----- WebSocket -----

    @MessageMapping("/chat/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            @Payload ChatMessagePayload payload,
            Principal principal) {

        // 🔐 usuário autenticado via JWT
        String email = principal.getName();

        User sender = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 💾 salva mensagem
        Message savedMessage = chatService.sendMessage(
                roomId,
                sender,
                payload.getContent()
        );

        // 📡 envia para subscribers
        messagingTemplate.convertAndSend(
                "/topic/room/" + roomId,
                mapToResponse(savedMessage)
        );
    }

    // ✅ DTO de resposta (evita expor entity)
    private ChatMessageResponse mapToResponse(Message message) {
        return ChatMessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .senderId(message.getSender().getId())
                .senderUsername(message.getSender().getUsername())
                .sentAt(message.getSentAt())
                .build();
    }

    // 🔐 payload seguro (frontend NÃO define usuário)
    @Data
    public static class ChatMessagePayload {
        private String content;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatMessageResponse {
        private Long id;
        private String content;
        private Long senderId;
        private String senderUsername;
        private LocalDateTime sentAt;
    }
}