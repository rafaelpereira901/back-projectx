package com.agoracorp.projectx.dto;

import java.time.Instant;

import com.agoracorp.projectx.model.ShelfStatus;

public record ShelfBookResponse(
		Long shelfId,
		Long profileId,
		Long bookId,
		String title,
		String isbn,
		ShelfStatus status,
		Instant updatedAt) {
}
