package com.agoracorp.projectx.dto;

import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
		@Size(max = 120, message = "Full name must have at most 120 characters")
		String fullName,
		@Size(max = 1000, message = "Bio must have at most 1000 characters")
		String bio) {
}
