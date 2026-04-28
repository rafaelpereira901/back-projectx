package com.agoracorp.projectx.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.agoracorp.projectx.model.Book;
import com.agoracorp.projectx.model.UserProfile;
import com.agoracorp.projectx.repository.BookRepository;
import com.agoracorp.projectx.repository.UserProfileRepository;
import com.agoracorp.projectx.security.UserPrincipal;

@Service
public class ProfileAccessService {

	private final UserProfileRepository userProfileRepository;
	private final BookRepository bookRepository;

	public ProfileAccessService(UserProfileRepository userProfileRepository, BookRepository bookRepository) {
		this.userProfileRepository = userProfileRepository;
		this.bookRepository = bookRepository;
	}

	public UserProfile getProfileOrThrow(Long profileId) {
		return userProfileRepository.findById(profileId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
	}

	public Book getBookOrThrow(Long bookId) {
		return bookRepository.findById(bookId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book with id " + bookId + " not found"));
	}

	public UserProfile assertOwnerProfile(Long profileId, UserPrincipal principal) {
		if (principal == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required");
		}

		UserProfile authenticatedProfile = userProfileRepository.findByUserId(principal.getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Authenticated user profile not found"));

		if (!profileId.equals(authenticatedProfile.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only modify your own content");
		}

		return authenticatedProfile;
	}
}
