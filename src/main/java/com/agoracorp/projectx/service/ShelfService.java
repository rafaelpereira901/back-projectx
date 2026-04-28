package com.agoracorp.projectx.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.agoracorp.projectx.dto.ShelfBookResponse;
import com.agoracorp.projectx.dto.ShelfResponse;
import com.agoracorp.projectx.dto.ShelfUpsertRequest;
import com.agoracorp.projectx.model.Book;
import com.agoracorp.projectx.model.ShelfStatus;
import com.agoracorp.projectx.model.UserBookShelf;
import com.agoracorp.projectx.model.UserProfile;
import com.agoracorp.projectx.repository.UserBookShelfRepository;
import com.agoracorp.projectx.security.UserPrincipal;

@Service
public class ShelfService {

	private final UserBookShelfRepository userBookShelfRepository;
	private final ProfileAccessService profileAccessService;

	public ShelfService(UserBookShelfRepository userBookShelfRepository, ProfileAccessService profileAccessService) {
		this.userBookShelfRepository = userBookShelfRepository;
		this.profileAccessService = profileAccessService;
	}

	@Transactional
	public ShelfResponse upsert(Long profileId, Long bookId, UserPrincipal principal, ShelfUpsertRequest request) {
		UserProfile profile = profileAccessService.assertOwnerProfile(profileId, principal);
		Book book = profileAccessService.getBookOrThrow(bookId);

		UserBookShelf shelf = userBookShelfRepository.findByUserProfileIdAndBookId(profileId, bookId).orElseGet(() -> {
			UserBookShelf created = new UserBookShelf();
			created.setUserProfile(profile);
			created.setBook(book);
			return created;
		});

		shelf.setStatus(request.status());
		return toResponse(userBookShelfRepository.save(shelf));
	}

	@Transactional
	public void delete(Long profileId, Long bookId, UserPrincipal principal) {
		profileAccessService.assertOwnerProfile(profileId, principal);
		UserBookShelf shelf = userBookShelfRepository.findByUserProfileIdAndBookId(profileId, bookId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shelf relation not found"));
		userBookShelfRepository.delete(shelf);
	}

	@Transactional(readOnly = true)
	public List<ShelfBookResponse> getBooksByProfileAndShelf(Long profileId, ShelfStatus status) {
		profileAccessService.getProfileOrThrow(profileId);
		return userBookShelfRepository.findByUserProfileIdAndStatus(profileId, status)
				.stream()
				.map(this::toBookResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public ShelfResponse getByProfileAndBook(Long profileId, Long bookId) {
		profileAccessService.getProfileOrThrow(profileId);
		profileAccessService.getBookOrThrow(bookId);
		UserBookShelf shelf = userBookShelfRepository.findByUserProfileIdAndBookId(profileId, bookId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shelf relation not found"));
		return toResponse(shelf);
	}

	private ShelfResponse toResponse(UserBookShelf shelf) {
		return new ShelfResponse(
				shelf.getId(),
				shelf.getUserProfile().getId(),
				shelf.getBook().getId(),
				shelf.getStatus(),
				shelf.getUpdatedAt());
	}

	private ShelfBookResponse toBookResponse(UserBookShelf shelf) {
		Book book = shelf.getBook();
		return new ShelfBookResponse(
				shelf.getId(),
				shelf.getUserProfile().getId(),
				book.getId(),
				book.getTitle(),
				book.getIsbn(),
				shelf.getStatus(),
				shelf.getUpdatedAt());
	}
}
