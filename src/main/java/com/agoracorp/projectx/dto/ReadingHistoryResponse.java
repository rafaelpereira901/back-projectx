package com.agoracorp.projectx.dto;

import java.time.Instant;

public record ReadingHistoryResponse(
		Long id,
		Long profileId,
		Long bookId,
		String comment,
		Instant createdAt) {
}
