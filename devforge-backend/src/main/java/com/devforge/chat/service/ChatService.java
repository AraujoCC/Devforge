package com.devforge.chat.service;

import com.devforge.chat.model.ChatRoom;
import com.devforge.chat.model.Message;
import com.devforge.chat.repository.ChatRepository;
import com.devforge.user.model.User;
import com.devforge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatRoom getOrCreateRoom(Long userId1, Long userId2) {
        String roomKey = ChatRoom.buildRoomKey(userId1, userId2);
        return chatRepository.findByRoomKey(roomKey).orElseGet(() -> {
            var user1 = userRepository.findById(userId1)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId1));
            var user2 = userRepository.findById(userId2)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId2));

            var room = ChatRoom.builder().roomKey(roomKey).build();
            room.getMembers().add(user1);
            room.getMembers().add(user2);
            return chatRepository.save(room);
        });
    }

    @Transactional
    public Message sendMessage(Long roomId, User sender, String content) {
        var room = chatRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));

        boolean isMember = room.getMembers().stream()
                .anyMatch(m -> m.getId().equals(sender.getId()));
        if (!isMember) throw new SecurityException("You are not a member of this room.");

        var message = Message.builder()
                .content(content)
                .sender(sender)
                .chatRoom(room)
                .build();

        room.getMessages().add(message);
        chatRepository.save(room);
        return message;
    }

    @Transactional(readOnly = true)
    public Page<Message> getMessages(Long roomId, Pageable pageable) {
        return chatRepository.findMessagesByRoomId(roomId, pageable);
    }

    @Transactional(readOnly = true)
    public List<ChatRoom> getUserRooms(Long userId) {
        return chatRepository.findRoomsByUserId(userId);
    }
}
