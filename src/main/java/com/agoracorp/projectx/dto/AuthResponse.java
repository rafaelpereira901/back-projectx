package com.agoracorp.projectx.dto;

public record AuthResponse(String token, String tokenType, Long userId, String email) {
}
