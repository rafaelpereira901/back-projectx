package com.agoracorp.projectx.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.agoracorp.projectx.dto.ProfileResponse;
import com.agoracorp.projectx.dto.ProfileUpdateRequest;
import com.agoracorp.projectx.model.UserAccount;
import com.agoracorp.projectx.security.UserPrincipal;
import com.agoracorp.projectx.service.UserProfileService;

@ExtendWith(MockitoExtension.class)
class UserProfileControllerTest {

	@Mock
	private UserProfileService userProfileService;

	private UserProfileController userProfileController;

	@BeforeEach
	void setUp() {
		userProfileController = new UserProfileController(userProfileService);
	}

	@Test
	void getProfileById_shouldReturnOk() {
		ProfileResponse profileResponse = new ProfileResponse(1L, "John", "Bio");
		when(userProfileService.getProfileById(2L)).thenReturn(profileResponse);

		ResponseEntity<ProfileResponse> response = userProfileController.getProfileById(2L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("John", response.getBody().fullName());
	}

	@Test
	void updateProfileById_shouldReturnOk() {
		UserPrincipal principal = buildPrincipal();
		ProfileUpdateRequest request = new ProfileUpdateRequest("Jane", "New bio");
		ProfileResponse profileResponse = new ProfileResponse(1L, "Jane", "New bio");
		when(userProfileService.updateProfileById(2L, principal, request)).thenReturn(profileResponse);

		ResponseEntity<ProfileResponse> response = userProfileController.updateProfileById(2L, principal, request);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Jane", response.getBody().fullName());
	}

	@Test
	void deleteProfileById_shouldReturnNoContent() {
		UserPrincipal principal = buildPrincipal();

		ResponseEntity<Void> response = userProfileController.deleteProfileById(2L, principal);

		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		verify(userProfileService).deleteProfileById(2L, principal);
	}

	private UserPrincipal buildPrincipal() {
		UserAccount user = new UserAccount();
		user.setId(2L);
		user.setEmail("user@mail.com");
		user.setPassword("encoded");
		return new UserPrincipal(user);
	}
}
