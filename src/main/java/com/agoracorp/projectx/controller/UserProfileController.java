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
@RequestMapping("/profiles")
public class UserProfileController {

	private final UserProfileService userProfileService;

	public UserProfileController(UserProfileService userProfileService) {
		this.userProfileService = userProfileService;
	}

	@GetMapping("/{profileId}")
	public ResponseEntity<ProfileResponse> getProfileById(@PathVariable Long profileId) {
		return new ResponseEntity<>(userProfileService.getProfileById(profileId), HttpStatus.OK);
	}

	@PutMapping("/{profileId}")
	public ResponseEntity<ProfileResponse> updateProfileById(@PathVariable Long profileId,
			@AuthenticationPrincipal UserPrincipal principal,
			@Valid @RequestBody ProfileUpdateRequest request) {
		return new ResponseEntity<>(userProfileService.updateProfileById(profileId, principal, request), HttpStatus.OK);
	}

	@DeleteMapping("/{profileId}")
	public ResponseEntity<Void> deleteProfileById(@PathVariable Long profileId,
			@AuthenticationPrincipal UserPrincipal principal) {
		userProfileService.deleteProfileById(profileId, principal);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
