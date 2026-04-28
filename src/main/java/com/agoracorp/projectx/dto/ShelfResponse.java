package com.agoracorp.projectx.dto;

import java.time.Instant;

import com.agoracorp.projectx.model.ShelfStatus;

public record ShelfResponse(
		Long id,
		Long profileId,
		Long bookId,
		ShelfStatus status,
		Instant updatedAt) {
}
