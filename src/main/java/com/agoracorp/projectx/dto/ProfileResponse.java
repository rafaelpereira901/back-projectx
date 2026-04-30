package com.agoracorp.projectx.dto;

public record ProfileResponse(Long userId, String fullName, String bio, String avatarUrl, String coverUrl) {
}
