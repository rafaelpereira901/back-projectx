package com.agoracorp.projectx.dto;

import com.agoracorp.projectx.model.ShelfStatus;

import jakarta.validation.constraints.NotNull;

public record ShelfUpsertRequest(
		@NotNull(message = "Shelf status is required")
		ShelfStatus status) {
}
