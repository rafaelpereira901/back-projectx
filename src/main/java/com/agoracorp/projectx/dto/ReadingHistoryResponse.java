package com.agoracorp.projectx.dto;

import java.time.Instant;

public record ReadingHistoryResponse(
		Long id,
		Long profileId,
		String username,
		Long bookId,
		String bookName,
		String comment,
		Instant createdAt) {
}
