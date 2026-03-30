package com.devforge.notification.controller;

import com.devforge.notification.model.Notification;
import com.devforge.notification.service.NotificationService;
import com.devforge.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Page<Notification>> getAll(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(notificationService.getNotifications(user.getId(), pageable));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> unreadCount(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(Map.of("count", notificationService.countUnread(user.getId())));
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllRead(@AuthenticationPrincipal User user) {
        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.noContent().build();
    }
}
