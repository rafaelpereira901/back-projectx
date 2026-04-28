package com.agoracorp.projectx.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agoracorp.projectx.dto.ReadingHistoryCreateRequest;
import com.agoracorp.projectx.dto.ReadingHistoryResponse;
import com.agoracorp.projectx.model.Book;
import com.agoracorp.projectx.model.ReadingHistory;
import com.agoracorp.projectx.model.UserProfile;
import com.agoracorp.projectx.repository.ReadingHistoryRepository;
import com.agoracorp.projectx.security.UserPrincipal;

@Service
public class ReadingHistoryService {

	private final ReadingHistoryRepository readingHistoryRepository;
	private final ProfileAccessService profileAccessService;

	public ReadingHistoryService(ReadingHistoryRepository readingHistoryRepository, ProfileAccessService profileAccessService) {
		this.readingHistoryRepository = readingHistoryRepository;
		this.profileAccessService = profileAccessService;
	}

	@Transactional
	public ReadingHistoryResponse create(Long profileId, Long bookId, UserPrincipal principal,
			ReadingHistoryCreateRequest request) {
		UserProfile profile = profileAccessService.assertOwnerProfile(profileId, principal);
		Book book = profileAccessService.getBookOrThrow(bookId);

		ReadingHistory history = new ReadingHistory();
		history.setUserProfile(profile);
		history.setBook(book);
		history.setComment(request.comment());

		return toResponse(readingHistoryRepository.save(history));
	}

	@Transactional(readOnly = true)
	public List<ReadingHistoryResponse> getByProfile(Long profileId) {
		profileAccessService.getProfileOrThrow(profileId);
		return readingHistoryRepository.findByUserProfileId(profileId)
				.stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<ReadingHistoryResponse> getByProfileAndBook(Long profileId, Long bookId) {
		profileAccessService.getProfileOrThrow(profileId);
		profileAccessService.getBookOrThrow(bookId);
		return readingHistoryRepository.findByUserProfileIdAndBookId(profileId, bookId)
				.stream()
				.map(this::toResponse)
				.toList();
	}

	private ReadingHistoryResponse toResponse(ReadingHistory history) {
		return new ReadingHistoryResponse(
				history.getId(),
				history.getUserProfile().getId(),
				history.getBook().getId(),
				history.getComment(),
				history.getCreatedAt());
	}
}
