package com.agoracorp.projectx.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.agoracorp.projectx.dto.ProfileResponse;
import com.agoracorp.projectx.dto.ProfileUpdateRequest;
import com.agoracorp.projectx.model.UserAccount;
import com.agoracorp.projectx.model.UserProfile;
import com.agoracorp.projectx.repository.UserAccountRepository;
import com.agoracorp.projectx.repository.UserProfileRepository;
import com.agoracorp.projectx.security.UserPrincipal;

@Service
public class UserProfileService {

	private final UserAccountRepository userAccountRepository;
	private final UserProfileRepository userProfileRepository;

	public UserProfileService(UserAccountRepository userAccountRepository, UserProfileRepository userProfileRepository) {
		this.userAccountRepository = userAccountRepository;
		this.userProfileRepository = userProfileRepository;
	}

	@Transactional(readOnly = true)
	public ProfileResponse getProfileByUserId(Long userId) {
		UserProfile profile = getProfileByUserIdOrThrow(userId);
		return toResponse(profile);
	}

	@Transactional
	public ProfileResponse updateProfileByUserId(Long userId, UserPrincipal principal, ProfileUpdateRequest request) {
		assertOwner(userId, principal);
		UserProfile profile = getProfileByUserIdOrThrow(userId);
		profile.setFullName(request.fullName());
		profile.setBio(request.bio());
		return toResponse(userProfileRepository.save(profile));
	}

	@Transactional
	public void deleteProfileByUserId(Long userId, UserPrincipal principal) {
		assertOwner(userId, principal);
		UserProfile profile = getProfileByUserIdOrThrow(userId);
		userProfileRepository.delete(profile);
	}

	private UserProfile getProfileByUserIdOrThrow(Long userId) {
		UserAccount user = userAccountRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		return userProfileRepository.findByUserId(user.getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
	}

	private void assertOwner(Long userId, UserPrincipal principal) {
		if (principal == null || !userId.equals(principal.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only modify your own profile");
		}
	}

	private ProfileResponse toResponse(UserProfile profile) {
		return new ProfileResponse(profile.getId(), profile.getUser().getId(), profile.getUser().getEmail(),
				profile.getFullName(), profile.getBio());
	}
}
