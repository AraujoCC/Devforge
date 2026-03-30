package com.devforge.notification.service;

import com.devforge.notification.model.Notification;
import com.devforge.notification.repository.NotificationRepository;
import com.devforge.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public Notification createNotification(User recipient, User actor,
                                            Notification.NotificationType type,
                                            String message, Long referenceId) {
        if (recipient.getId().equals(actor.getId())) return null; // don't self-notify

        var notification = Notification.builder()
                .recipient(recipient)
                .actor(actor)
                .type(type)
                .message(message)
                .referenceId(referenceId)
                .build();

        var saved = notificationRepository.save(notification);

        // Push via WebSocket
        messagingTemplate.convertAndSendToUser(
                recipient.getUsername(),
                "/queue/notifications",
                saved
        );

        return saved;
    }

    @Transactional(readOnly = true)
    public Page<Notification> getNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public long countUnread(Long userId) {
        return notificationRepository.countByRecipientIdAndReadFalse(userId);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }

    // ----- Event helpers -----

    public void notifyPostLike(User postOwner, User liker, Long postId) {
        createNotification(
                postOwner, liker,
                Notification.NotificationType.POST_LIKE,
                liker.getUsername() + " liked your post.",
                postId
        );
    }

    public void notifyNewFollower(User followed, User follower) {
        createNotification(
                followed, follower,
                Notification.NotificationType.NEW_FOLLOWER,
                follower.getUsername() + " started following you.",
                null
        );
    }

    public void notifyDirectMessage(User recipient, User sender, Long roomId) {
        createNotification(
                recipient, sender,
                Notification.NotificationType.DIRECT_MESSAGE,
                sender.getUsername() + " sent you a message.",
                roomId
        );
    }
}
