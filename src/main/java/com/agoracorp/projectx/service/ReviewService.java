package com.agoracorp.projectx.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.agoracorp.projectx.dto.ReviewResponse;
import com.agoracorp.projectx.dto.ReviewUpsertRequest;
import com.agoracorp.projectx.model.Book;
import com.agoracorp.projectx.model.Review;
import com.agoracorp.projectx.model.UserProfile;
import com.agoracorp.projectx.repository.ReviewRepository;
import com.agoracorp.projectx.security.UserPrincipal;

@Service
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final ProfileAccessService profileAccessService;

	public ReviewService(ReviewRepository reviewRepository, ProfileAccessService profileAccessService) {
		this.reviewRepository = reviewRepository;
		this.profileAccessService = profileAccessService;
	}

	@Transactional
	public ReviewResponse upsert(Long profileId, Long bookId, UserPrincipal principal, ReviewUpsertRequest request) {
		UserProfile profile = profileAccessService.assertOwnerProfile(profileId, principal);
		Book book = profileAccessService.getBookOrThrow(bookId);

		Review review = reviewRepository.findByUserProfileIdAndBookId(profileId, bookId).orElseGet(() -> {
			Review created = new Review();
			created.setUserProfile(profile);
			created.setBook(book);
			return created;
		});

		review.setComment(request.comment());
		review.setRating(request.rating());
		return toResponse(reviewRepository.save(review));
	}

	@Transactional
	public void delete(Long profileId, Long bookId, UserPrincipal principal) {
		profileAccessService.assertOwnerProfile(profileId, principal);
		Review review = reviewRepository.findByUserProfileIdAndBookId(profileId, bookId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
		reviewRepository.delete(review);
	}

	@Transactional(readOnly = true)
	public List<ReviewResponse> getByProfile(Long profileId) {
		profileAccessService.getProfileOrThrow(profileId);
		return reviewRepository.findByUserProfileId(profileId)
				.stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public ReviewResponse getByProfileAndBook(Long profileId, Long bookId) {
		profileAccessService.getProfileOrThrow(profileId);
		profileAccessService.getBookOrThrow(bookId);
		Review review = reviewRepository.findByUserProfileIdAndBookId(profileId, bookId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
		return toResponse(review);
	}

	@Transactional(readOnly = true)
	public List<ReviewResponse> getByBook(Long bookId) {
		profileAccessService.getBookOrThrow(bookId);
		return reviewRepository.findByBookId(bookId)
				.stream()
				.map(this::toResponse)
				.toList();
	}

	private ReviewResponse toResponse(Review review) {
		return new ReviewResponse(
				review.getId(),
				review.getUserProfile().getId(),
				review.getBook().getId(),
				review.getRating(),
				review.getComment(),
				review.getCreatedAt(),
				review.getUpdatedAt());
	}
}
