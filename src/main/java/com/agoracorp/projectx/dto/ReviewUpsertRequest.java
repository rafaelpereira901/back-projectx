package com.agoracorp.projectx.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewUpsertRequest(
		@NotBlank(message = "Comment is required")
		@Size(max = 2000, message = "Comment must have at most 2000 characters")
		String comment,
		@NotNull(message = "Rating is required")
		@Min(value = 1, message = "Rating must be between 1 and 5")
		@Max(value = 5, message = "Rating must be between 1 and 5")
		Integer rating) {
}
