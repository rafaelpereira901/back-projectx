package com.agoracorp.projectx.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

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
import com.agoracorp.projectx.repository.UserAccountRepository;
import com.agoracorp.projectx.repository.UserProfileRepository;
import com.agoracorp.projectx.security.UserPrincipal;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

	@Mock
	private UserAccountRepository userAccountRepository;
	@Mock
	private UserProfileRepository userProfileRepository;

	private UserProfileService userProfileService;

	@BeforeEach
	void setUp() {
		userProfileService = new UserProfileService(userAccountRepository, userProfileRepository);
	}

	@Test
	void getProfileByUserId_shouldReturnProfile() {
		UserAccount user = buildUser();
		UserProfile profile = buildProfile(user);
		when(userAccountRepository.findById(1L)).thenReturn(Optional.of(user));
		when(userProfileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));

		ProfileResponse response = userProfileService.getProfileByUserId(1L);

		assertEquals(5L, response.id());
		assertEquals("Name", response.fullName());
	}

	@Test
	void updateProfileByUserId_shouldPersistChanges_whenOwner() {
		UserAccount user = buildUser();
		UserProfile profile = buildProfile(user);
		UserPrincipal principal = new UserPrincipal(user);
		when(userAccountRepository.findById(1L)).thenReturn(Optional.of(user));
		when(userProfileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));
		when(userProfileRepository.save(profile)).thenReturn(profile);

		ProfileResponse response = userProfileService.updateProfileByUserId(1L, principal,
				new ProfileUpdateRequest("New Name", "New bio"));

		assertEquals("New Name", response.fullName());
		assertEquals("New bio", response.bio());
	}

	@Test
	void deleteProfileByUserId_shouldDeleteProfile_whenOwner() {
		UserAccount user = buildUser();
		UserProfile profile = buildProfile(user);
		UserPrincipal principal = new UserPrincipal(user);
		when(userAccountRepository.findById(1L)).thenReturn(Optional.of(user));
		when(userProfileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));

		userProfileService.deleteProfileByUserId(1L, principal);

		verify(userProfileRepository).delete(profile);
	}

	@Test
	void getProfileByUserId_shouldThrowNotFound_whenProfileMissing() {
		UserAccount user = buildUser();
		when(userAccountRepository.findById(1L)).thenReturn(Optional.of(user));
		when(userProfileRepository.findByUserId(1L)).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> userProfileService.getProfileByUserId(1L));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
	}

	@Test
	void updateProfileByUserId_shouldThrowForbidden_whenNotOwner() {
		UserAccount other = new UserAccount();
		other.setId(2L);
		other.setEmail("other@mail.com");
		other.setPassword("encoded");
		UserPrincipal principal = new UserPrincipal(other);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> userProfileService.updateProfileByUserId(1L, principal,
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
