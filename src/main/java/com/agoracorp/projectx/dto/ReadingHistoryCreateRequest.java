package com.agoracorp.projectx.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReadingHistoryCreateRequest(
		@NotBlank(message = "Comment is required")
		@Size(max = 2000, message = "Comment must have at most 2000 characters")
		String comment) {
}
