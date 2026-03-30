package com.devforge.chat.model;

import com.devforge.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_rooms")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_key", unique = true, nullable = false)
    private String roomKey;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "chat_room_members",
        joinColumns = @JoinColumn(name = "room_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private List<User> members = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Message> messages = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    // Factory: generates a deterministic room key for two users
    public static String buildRoomKey(Long userId1, Long userId2) {
        long a = Math.min(userId1, userId2);
        long b = Math.max(userId1, userId2);
        return "dm_" + a + "_" + b;
    }
}
