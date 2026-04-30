package com.agoracorp.projectx.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.agoracorp.projectx.dto.ReviewResponse;
import com.agoracorp.projectx.dto.ReviewUpsertRequest;
import com.agoracorp.projectx.model.Book;
import com.agoracorp.projectx.model.Review;
import com.agoracorp.projectx.model.UserAccount;
import com.agoracorp.projectx.model.UserProfile;
import com.agoracorp.projectx.repository.ReviewRepository;
import com.agoracorp.projectx.security.UserPrincipal;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private ProfileAccessService profileAccessService;

	private ReviewService reviewService;

	@BeforeEach
	void setUp() {
		reviewService = new ReviewService(reviewRepository, profileAccessService);
	}

	@Test
	void upsert_shouldCreateReviewWhenMissing() {
		Long profileId = 7L;
		Long bookId = 11L;
		UserProfile profile = profile(profileId, 100L);
		Book book = book(bookId);
		UserPrincipal principal = principal(100L);

		when(profileAccessService.assertOwnerProfile(profileId, principal)).thenReturn(profile);
		when(profileAccessService.getBookOrThrow(bookId)).thenReturn(book);
		when(reviewRepository.findByUserProfileIdAndBookId(profileId, bookId)).thenReturn(Optional.empty());
		when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
			Review saved = invocation.getArgument(0);
			saved.setId(1L);
			saved.setCreatedAt(Instant.parse("2026-04-27T00:00:00Z"));
			saved.setUpdatedAt(Instant.parse("2026-04-27T00:00:00Z"));
			return saved;
		});

		ReviewResponse response = reviewService.upsert(profileId, bookId, principal,
				new ReviewUpsertRequest("great", 5));

		assertEquals(1L, response.id());
		assertEquals(profileId, response.profileId());
		assertEquals("Test User", response.username());
		assertEquals(bookId, response.bookId());
		assertEquals("book", response.bookName());
		assertEquals(5, response.rating());
		assertEquals("great", response.comment());
	}

	@Test
	void upsert_shouldUpdateExistingReview() {
		Long profileId = 7L;
		Long bookId = 11L;
		UserProfile profile = profile(profileId, 100L);
		Book book = book(bookId);
		UserPrincipal principal = principal(100L);
		Review existing = new Review();
		existing.setId(90L);
		existing.setUserProfile(profile);
		existing.setBook(book);
		existing.setComment("old");
		existing.setRating(2);
		existing.setCreatedAt(Instant.parse("2026-04-20T00:00:00Z"));
		existing.setUpdatedAt(Instant.parse("2026-04-20T00:00:00Z"));

		when(profileAccessService.assertOwnerProfile(profileId, principal)).thenReturn(profile);
		when(profileAccessService.getBookOrThrow(bookId)).thenReturn(book);
		when(reviewRepository.findByUserProfileIdAndBookId(profileId, bookId)).thenReturn(Optional.of(existing));
		when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

		ReviewResponse response = reviewService.upsert(profileId, bookId, principal,
				new ReviewUpsertRequest("new", 4));

		ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);
		verify(reviewRepository).save(captor.capture());
		assertEquals(90L, captor.getValue().getId());
		assertEquals("new", captor.getValue().getComment());
		assertEquals(4, captor.getValue().getRating());
		assertEquals(90L, response.id());
		assertEquals("Test User", response.username());
		assertEquals("book", response.bookName());
	}

	@Test
	void delete_shouldReturnForbiddenWhenNotOwner() {
		Long profileId = 7L;
		Long bookId = 11L;
		UserPrincipal principal = principal(999L);

		ResponseStatusException forbidden = new ResponseStatusException(HttpStatus.FORBIDDEN,
				"You can only modify your own content");
		when(profileAccessService.assertOwnerProfile(profileId, principal)).thenThrow(forbidden);

		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> reviewService.delete(profileId, bookId, principal));

		assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
	}

	private UserProfile profile(Long profileId, Long userId) {
		UserProfile profile = new UserProfile();
		profile.setId(profileId);
		profile.setFullName("Test User");

		UserAccount user = new UserAccount();
		user.setId(userId);
		user.setEmail("test@example.com");
		user.setPassword("hash");
		profile.setUser(user);
		return profile;
	}

	private Book book(Long bookId) {
		Book book = new Book();
		book.setId(bookId);
		book.setTitle("book");
		book.setIsbn("isbn");
		return book;
	}

	private UserPrincipal principal(Long userId) {
		UserAccount user = new UserAccount();
		user.setId(userId);
		user.setEmail("owner@example.com");
		user.setPassword("hash");
		return new UserPrincipal(user);
	}
}
