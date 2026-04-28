package com.agoracorp.projectx.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agoracorp.projectx.dto.ProfileResponse;
import com.agoracorp.projectx.dto.ProfileUpdateRequest;
import com.agoracorp.projectx.model.UserProfile;
import com.agoracorp.projectx.repository.UserProfileRepository;
import com.agoracorp.projectx.security.UserPrincipal;

@Service
public class UserProfileService {

	private final UserProfileRepository userProfileRepository;
	private final ProfileAccessService profileAccessService;

	public UserProfileService(UserProfileRepository userProfileRepository, ProfileAccessService profileAccessService) {
		this.userProfileRepository = userProfileRepository;
		this.profileAccessService = profileAccessService;
	}

	@Transactional(readOnly = true)
	public ProfileResponse getProfileById(Long profileId) {
		UserProfile profile = profileAccessService.getProfileOrThrow(profileId);
		return toResponse(profile);
	}

	@Transactional
	public ProfileResponse updateProfileById(Long profileId, UserPrincipal principal, ProfileUpdateRequest request) {
		UserProfile profile = profileAccessService.assertOwnerProfile(profileId, principal);
		profile.setFullName(request.fullName());
		profile.setBio(request.bio());
		return toResponse(userProfileRepository.save(profile));
	}

	@Transactional
	public void deleteProfileById(Long profileId, UserPrincipal principal) {
		UserProfile profile = profileAccessService.assertOwnerProfile(profileId, principal);
		userProfileRepository.delete(profile);
	}

	private ProfileResponse toResponse(UserProfile profile) {
		return new ProfileResponse(profile.getId(), profile.getFullName(), profile.getBio());
	}
}
