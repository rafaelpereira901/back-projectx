package com.agoracorp.projectx.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agoracorp.projectx.dto.ProfileResponse;
import com.agoracorp.projectx.dto.ProfileUpdateRequest;
import com.agoracorp.projectx.security.UserPrincipal;
import com.agoracorp.projectx.service.UserProfileService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/profile")
public class UserProfileController {

	private final UserProfileService userProfileService;

	public UserProfileController(UserProfileService userProfileService) {
		this.userProfileService = userProfileService;
	}

	@GetMapping("/{userId}")
	public ResponseEntity<ProfileResponse> getProfileByUserId(@PathVariable Long userId) {
		return new ResponseEntity<>(userProfileService.getProfileByUserId(userId), HttpStatus.OK);
	}

	@PutMapping("/{userId}")
	public ResponseEntity<ProfileResponse> updateProfileByUserId(@PathVariable Long userId,
			@AuthenticationPrincipal UserPrincipal principal,
			@Valid @RequestBody ProfileUpdateRequest request) {
		return new ResponseEntity<>(userProfileService.updateProfileByUserId(userId, principal, request), HttpStatus.OK);
	}

	@DeleteMapping("/{userId}")
	public ResponseEntity<Void> deleteProfileByUserId(@PathVariable Long userId,
			@AuthenticationPrincipal UserPrincipal principal) {
		userProfileService.deleteProfileByUserId(userId, principal);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
