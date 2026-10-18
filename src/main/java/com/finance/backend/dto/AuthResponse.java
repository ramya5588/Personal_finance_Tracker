package com.finance.backend.dto;

public record AuthResponse(
        String token,
        String tokenType,
        long expiresIn,
        String name,
        String email
) {
}
