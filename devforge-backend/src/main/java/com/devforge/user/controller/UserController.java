package com.devforge.user.controller;

import com.devforge.auth.service.AuthService;
import com.devforge.user.model.User;
import com.devforge.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.findByUsername(username));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> listAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String bio,
            @RequestParam(required = false) String avatarUrl
    ) {
        return ResponseEntity.ok(userService.updateProfile(user.getId(), bio, avatarUrl));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}