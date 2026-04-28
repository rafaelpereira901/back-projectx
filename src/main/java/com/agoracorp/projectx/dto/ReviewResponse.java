package com.agoracorp.projectx.dto;

import java.time.Instant;

public record ReviewResponse(
		Long id,
		Long profileId,
		Long bookId,
		Integer rating,
		String comment,
		Instant createdAt,
		Instant updatedAt) {
}
