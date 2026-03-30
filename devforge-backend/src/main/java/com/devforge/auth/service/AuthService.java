package com.devforge.auth.service;

import com.devforge.user.dto.AuthResponse;
import com.devforge.user.dto.LoginRequest;
import com.devforge.user.dto.RegisterRequest;
import com.devforge.user.model.Role;
import com.devforge.user.model.User;
import com.devforge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use.");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken.");
        }

        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .bio(request.getBio())
                .roles(Set.of(Role.ROLE_USER))
                .build();

        userRepository.save(user);

        var accessToken  = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public AuthResponse login(LoginRequest request) {

        // 🔥 autentica via Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials."));

        var accessToken  = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    // 🔥 REFRESH TOKEN FLOW (ESSENCIAL)
    public AuthResponse refreshToken(String refreshToken) {

        String email = jwtService.extractUsername(refreshToken);

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new RuntimeException("Invalid refresh token");
        }

        var newAccessToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}