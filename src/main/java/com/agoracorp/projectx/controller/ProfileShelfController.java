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

import com.agoracorp.projectx.dto.ShelfBookResponse;
import com.agoracorp.projectx.dto.ShelfResponse;
import com.agoracorp.projectx.dto.ShelfUpsertRequest;
import com.agoracorp.projectx.model.ShelfStatus;
import com.agoracorp.projectx.security.UserPrincipal;
import com.agoracorp.projectx.service.ShelfService;

import jakarta.validation.Valid;

@RestController
public class ProfileShelfController {

	private final ShelfService shelfService;

	public ProfileShelfController(ShelfService shelfService) {
		this.shelfService = shelfService;
	}

	@PutMapping("/profiles/{profileId}/books/{bookId}/shelf")
	public ResponseEntity<ShelfResponse> upsert(
			@PathVariable Long profileId,
			@PathVariable Long bookId,
			@AuthenticationPrincipal UserPrincipal principal,
			@Valid @RequestBody ShelfUpsertRequest request) {
		return new ResponseEntity<>(shelfService.upsert(profileId, bookId, principal, request), HttpStatus.OK);
	}

	@DeleteMapping("/profiles/{profileId}/books/{bookId}/shelf")
	public ResponseEntity<Void> delete(
			@PathVariable Long profileId,
			@PathVariable Long bookId,
			@AuthenticationPrincipal UserPrincipal principal) {
		shelfService.delete(profileId, bookId, principal);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/profiles/{profileId}/shelves/{status}/books")
	public ResponseEntity<List<ShelfBookResponse>> getBooksByShelf(
			@PathVariable Long profileId,
			@PathVariable ShelfStatus status) {
		return new ResponseEntity<>(shelfService.getBooksByProfileAndShelf(profileId, status), HttpStatus.OK);
	}

	@GetMapping("/profiles/{profileId}/books/{bookId}/shelf")
	public ResponseEntity<ShelfResponse> getByProfileAndBook(
			@PathVariable Long profileId,
			@PathVariable Long bookId) {
		return new ResponseEntity<>(shelfService.getByProfileAndBook(profileId, bookId), HttpStatus.OK);
	}
}
