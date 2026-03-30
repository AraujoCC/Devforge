package com.devforge.auth.controller;

import com.devforge.auth.service.AuthService;
import com.devforge.user.dto.AuthResponse;
import com.devforge.user.dto.LoginRequest;
import com.devforge.user.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestHeader("Authorization") String authHeader) {
        String refreshToken = authHeader.substring(7);
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @GetMapping("/oauth2/success")
    public ResponseEntity<String> oauth2Success() {
        return ResponseEntity.ok("OAuth2 login successful!");
    }
}
