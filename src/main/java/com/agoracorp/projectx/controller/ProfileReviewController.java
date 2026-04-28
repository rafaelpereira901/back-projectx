package com.agoracorp.projectx.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.agoracorp.projectx.dto.ReviewResponse;
import com.agoracorp.projectx.dto.ReviewUpsertRequest;
import com.agoracorp.projectx.security.UserPrincipal;
import com.agoracorp.projectx.service.ReviewService;

import jakarta.validation.Valid;

@RestController
public class ProfileReviewController {

	private final ReviewService reviewService;

	public ProfileReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	@PutMapping("/profiles/{profileId}/books/{bookId}/review")
	public ResponseEntity<ReviewResponse> upsert(
			@PathVariable Long profileId,
			@PathVariable Long bookId,
			@AuthenticationPrincipal UserPrincipal principal,
			@Valid @RequestBody ReviewUpsertRequest request) {
		return new ResponseEntity<>(reviewService.upsert(profileId, bookId, principal, request), HttpStatus.OK);
	}

	@DeleteMapping("/profiles/{profileId}/books/{bookId}/review")
	public ResponseEntity<Void> delete(
			@PathVariable Long profileId,
			@PathVariable Long bookId,
			@AuthenticationPrincipal UserPrincipal principal) {
		reviewService.delete(profileId, bookId, principal);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/profiles/{profileId}/reviews")
	public ResponseEntity<List<ReviewResponse>> getByProfile(@PathVariable Long profileId) {
		return new ResponseEntity<>(reviewService.getByProfile(profileId), HttpStatus.OK);
	}

	@GetMapping("/profiles/{profileId}/books/{bookId}/review")
	public ResponseEntity<ReviewResponse> getByProfileAndBook(
			@PathVariable Long profileId,
			@PathVariable Long bookId) {
		return new ResponseEntity<>(reviewService.getByProfileAndBook(profileId, bookId), HttpStatus.OK);
	}

	@GetMapping("/books/{bookId}/reviews")
	public ResponseEntity<List<ReviewResponse>> getByBook(@PathVariable Long bookId) {
		return new ResponseEntity<>(reviewService.getByBook(bookId), HttpStatus.OK);
	}
}
