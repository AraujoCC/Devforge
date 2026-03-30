package com.devforge.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "SUA_CHAVE_SECRETA_SUPER_GRANDE_AQUI_12345678901234567890";

    // 🔥 gerar access token
    public String generateToken(UserDetails userDetails) {
        return buildToken(userDetails, 1000 * 60 * 60); // 1h
    }

    // 🔥 gerar refresh token
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(userDetails, 1000 * 60 * 60 * 24 * 7); // 7 dias
    }

    private String buildToken(UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // email
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 🔥 extrair username (email)
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 🔥 validar token
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // 🔥 verificar expiração
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 🔥 extrair claims
    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = SECRET_KEY.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}