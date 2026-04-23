package com.agoracorp.projectx.dto;

public record ProfileResponse(Long id, Long userId, String email, String fullName, String bio) {
}
