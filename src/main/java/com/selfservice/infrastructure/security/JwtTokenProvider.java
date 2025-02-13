package com.selfservice.infrastructure.security;

import java.security.Key;
import java.util.Date;
import java.util.Base64;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.selfservice.domain.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.expiration}")
    private int jwtExpirationInMs;

    private SecretKey key;

    @PostConstruct
    public void init() {
        // Gera uma chave de 512 bits (64 bytes) para HS512
        byte[] keyBytes = new byte[64];
        // Preenche com valores fixos para manter a mesma chave
        for (int i = 0; i < keyBytes.length; i++) {
            keyBytes[i] = (byte) i;
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("name", user.getName())
                .claim("roles", user.getRoles())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            log.error("Assinatura JWT inválida");
        } catch (MalformedJwtException ex) {
            log.error("Token JWT inválido");
        } catch (ExpiredJwtException ex) {
            log.error("Token JWT expirado");
        } catch (UnsupportedJwtException ex) {
            log.error("Token JWT não suportado");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string está vazia");
        }
        return false;
    }
} 