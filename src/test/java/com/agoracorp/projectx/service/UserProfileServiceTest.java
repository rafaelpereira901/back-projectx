package com.agoracorp.projectx.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.agoracorp.projectx.dto.ProfileResponse;
import com.agoracorp.projectx.dto.ProfileUpdateRequest;
import com.agoracorp.projectx.model.UserAccount;
import com.agoracorp.projectx.model.UserProfile;
import com.agoracorp.projectx.repository.UserProfileRepository;
import com.agoracorp.projectx.security.UserPrincipal;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

	@Mock
	private UserProfileRepository userProfileRepository;
	@Mock
	private ProfileAccessService profileAccessService;

	private UserProfileService userProfileService;

	@BeforeEach
	void setUp() {
		userProfileService = new UserProfileService(userProfileRepository, profileAccessService);
	}

	@Test
	void getProfileById_shouldReturnProfile() {
		UserAccount user = buildUser();
		UserProfile profile = buildProfile(user);
		when(profileAccessService.getProfileOrThrow(5L)).thenReturn(profile);

		ProfileResponse response = userProfileService.getProfileById(5L);

		assertEquals(5L, response.id());
		assertEquals("Name", response.fullName());
	}

	@Test
	void updateProfileById_shouldPersistChanges_whenOwner() {
		UserAccount user = buildUser();
		UserProfile profile = buildProfile(user);
		UserPrincipal principal = new UserPrincipal(user);
		when(profileAccessService.assertOwnerProfile(5L, principal)).thenReturn(profile);
		when(userProfileRepository.save(profile)).thenReturn(profile);

		ProfileResponse response = userProfileService.updateProfileById(5L, principal,
				new ProfileUpdateRequest("New Name", "New bio"));

		assertEquals("New Name", response.fullName());
		assertEquals("New bio", response.bio());
	}

	@Test
	void deleteProfileById_shouldDeleteProfile_whenOwner() {
		UserAccount user = buildUser();
		UserProfile profile = buildProfile(user);
		UserPrincipal principal = new UserPrincipal(user);
		when(profileAccessService.assertOwnerProfile(5L, principal)).thenReturn(profile);

		userProfileService.deleteProfileById(5L, principal);

		verify(userProfileRepository).delete(profile);
	}

	@Test
	void getProfileById_shouldThrowNotFound_whenProfileMissing() {
		ResponseStatusException notFound = new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found");
		when(profileAccessService.getProfileOrThrow(99L)).thenThrow(notFound);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> userProfileService.getProfileById(99L));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
	}

	@Test
	void updateProfileById_shouldThrowForbidden_whenNotOwner() {
		UserAccount other = new UserAccount();
		other.setId(2L);
		other.setEmail("other@mail.com");
		other.setPassword("encoded");
		UserPrincipal principal = new UserPrincipal(other);
		ResponseStatusException forbidden = new ResponseStatusException(HttpStatus.FORBIDDEN,
				"You can only modify your own content");
		when(profileAccessService.assertOwnerProfile(5L, principal)).thenThrow(forbidden);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> userProfileService.updateProfileById(5L, principal,
						new ProfileUpdateRequest("New Name", "New bio")));

		assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
	}

	private UserAccount buildUser() {
		UserAccount user = new UserAccount();
		user.setId(1L);
		user.setEmail("user@mail.com");
		user.setPassword("encoded");
		return user;
	}

	private UserProfile buildProfile(UserAccount user) {
		UserProfile profile = new UserProfile();
		profile.setId(5L);
		profile.setUser(user);
		profile.setFullName("Name");
		profile.setBio("Bio");
		return profile;
	}
}
