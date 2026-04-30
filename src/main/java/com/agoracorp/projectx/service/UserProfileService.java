package com.agoracorp.projectx.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
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

	private static final Logger log = LoggerFactory.getLogger(UserProfileService.class);

	private final UserAccountRepository userAccountRepository;
	private final UserProfileRepository userProfileRepository;
	private final CloudinaryService cloudinaryService;

	public UserProfileService(UserAccountRepository userAccountRepository,
			UserProfileRepository userProfileRepository,
			CloudinaryService cloudinaryService) {
		this.userAccountRepository = userAccountRepository;
		this.userProfileRepository = userProfileRepository;
		this.cloudinaryService = cloudinaryService;
	}

	@Transactional(readOnly = true)
	public ProfileResponse getProfileByUserId(Long userId) {
		UserProfile profile = getProfileByUserIdOrThrow(userId);
		return toResponse(profile, userId);
	}

	@Transactional
	public ProfileResponse updateProfileByUserId(Long userId, UserPrincipal principal, ProfileUpdateRequest request) {
		assertOwner(userId, principal);
		UserProfile profile = getProfileByUserIdOrThrow(userId);
		profile.setFullName(request.fullName());
		profile.setBio(request.bio());
		return toResponse(userProfileRepository.save(profile), userId);
	}

	@Transactional
	public ProfileResponse uploadAvatarByUserId(Long userId, UserPrincipal principal, MultipartFile file) {
		assertOwner(userId, principal);
		validateImageFile(file);
		UserProfile profile = getProfileByUserIdOrThrow(userId);
		try {
			profile.setAvatarUrl(cloudinaryService.uploadAvatar(file, userId));
			return toResponse(userProfileRepository.save(profile), userId);
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload image");
		}
	}

	@Transactional
	public ProfileResponse uploadCoverByUserId(Long userId, UserPrincipal principal, MultipartFile file) {
		assertOwner(userId, principal);
		validateImageFile(file);
		UserProfile profile = getProfileByUserIdOrThrow(userId);
		try {
			profile.setCoverUrl(cloudinaryService.uploadCover(file, userId));
			return toResponse(userProfileRepository.save(profile), userId);
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload image");
		}
	}

	@Transactional
	public void deleteProfileByUserId(Long userId, UserPrincipal principal, String reason) {
		assertOwner(userId, principal);
		if (reason != null && !reason.isBlank()) {
			log.info("Profile deleted — userId={} reason=\"{}\"", userId, reason.strip());
		} else {
			log.info("Profile deleted — userId={} reason=(none)", userId);
		}
		UserProfile profile = getProfileByUserIdOrThrow(userId);
		userProfileRepository.delete(profile);
	}

	private void validateImageFile(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is required");
		}
		String contentType = file.getContentType();
		if (contentType == null || !contentType.startsWith("image/")) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File must be an image");
		}
		if (file.getSize() > 5 * 1024 * 1024) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image must be smaller than 5 MB");
		}
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

	private ProfileResponse toResponse(UserProfile profile, Long userId) {
		return new ProfileResponse(userId, profile.getFullName(), profile.getBio(), profile.getAvatarUrl(), profile.getCoverUrl());
	}
}
