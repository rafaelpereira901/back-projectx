package com.agoracorp.projectx.dto;

import java.time.Instant;

public record ReviewResponse(
		Long id,
		Long profileId,
		String username,
		Long bookId,
		String bookName,
		Integer rating,
		String comment,
		Instant createdAt,
		Instant updatedAt) {
}
