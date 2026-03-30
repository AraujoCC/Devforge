package com.devforge.chat.repository;

import com.devforge.chat.model.ChatRoom;
import com.devforge.chat.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByRoomKey(String roomKey);

    @Query("SELECT m FROM Message m WHERE m.chatRoom.id = :roomId ORDER BY m.sentAt ASC")
    Page<Message> findMessagesByRoomId(Long roomId, Pageable pageable);

    @Query("SELECT cr FROM ChatRoom cr JOIN cr.members m WHERE m.id = :userId")
    List<ChatRoom> findRoomsByUserId(Long userId);
}
