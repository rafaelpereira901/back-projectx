package com.agoracorp.projectx.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agoracorp.projectx.dto.ReadingHistoryCreateRequest;
import com.agoracorp.projectx.dto.ReadingHistoryResponse;
import com.agoracorp.projectx.security.UserPrincipal;
import com.agoracorp.projectx.service.ReadingHistoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/profiles/{profileId}")
public class ProfileReadingHistoryController {

	private final ReadingHistoryService readingHistoryService;

	public ProfileReadingHistoryController(ReadingHistoryService readingHistoryService) {
		this.readingHistoryService = readingHistoryService;
	}

	@PostMapping("/books/{bookId}/reading-histories")
	public ResponseEntity<ReadingHistoryResponse> create(
			@PathVariable Long profileId,
			@PathVariable Long bookId,
			@AuthenticationPrincipal UserPrincipal principal,
			@Valid @RequestBody ReadingHistoryCreateRequest request) {
		return new ResponseEntity<>(readingHistoryService.create(profileId, bookId, principal, request), HttpStatus.CREATED);
	}

	@GetMapping("/reading-histories")
	public ResponseEntity<List<ReadingHistoryResponse>> getByProfile(@PathVariable Long profileId) {
		return new ResponseEntity<>(readingHistoryService.getByProfile(profileId), HttpStatus.OK);
	}

	@GetMapping("/books/{bookId}/reading-histories")
	public ResponseEntity<List<ReadingHistoryResponse>> getByProfileAndBook(
			@PathVariable Long profileId,
			@PathVariable Long bookId) {
		return new ResponseEntity<>(readingHistoryService.getByProfileAndBook(profileId, bookId), HttpStatus.OK);
	}
}
