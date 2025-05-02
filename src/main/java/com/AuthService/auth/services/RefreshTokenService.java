package com.AuthService.auth.services;

import com.AuthService.auth.entities.TokenEntity;
import com.AuthService.auth.repo.TokenRepo;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class RefreshTokenService {

    private final TokenRepo tokenRepo;
    private static final long REFRESH_TOKEN_VALIDITY = 60 * 1000; // 1 min in milliseconds

    public RefreshTokenService(TokenRepo tokenRepo) {
        this.tokenRepo = tokenRepo;
    }

    public String createRefreshToken(String username) {
        String refreshToken = UUID.randomUUID().toString();
        TokenEntity token = new TokenEntity();
        token.setToken(refreshToken);
        token.setUsername(username);
        token.setExpiryDate(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY));
        tokenRepo.save(token);
        return refreshToken;
    }

    public boolean verifyRefreshToken(String refreshToken) {
        TokenEntity token = tokenRepo.findByToken(refreshToken);
        if (token == null) {
            return false; // Token doesn't exist
        }
        return !token.getExpiryDate().before(new Date()); // Check if token is not expired
    }

    public String getUsernameFromToken(String refreshToken) {
        TokenEntity token = tokenRepo.findByToken(refreshToken);
        return token != null ? token.getUsername() : null;
    }
}
