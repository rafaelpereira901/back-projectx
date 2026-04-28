package com.agoracorp.projectx.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agoracorp.projectx.dto.ShelfBookResponse;
import com.agoracorp.projectx.dto.ShelfResponse;
import com.agoracorp.projectx.dto.ShelfUpsertRequest;
import com.agoracorp.projectx.model.Book;
import com.agoracorp.projectx.model.ShelfStatus;
import com.agoracorp.projectx.model.UserAccount;
import com.agoracorp.projectx.model.UserBookShelf;
import com.agoracorp.projectx.model.UserProfile;
import com.agoracorp.projectx.repository.UserBookShelfRepository;
import com.agoracorp.projectx.security.UserPrincipal;

@ExtendWith(MockitoExtension.class)
class ShelfServiceTest {

	@Mock
	private UserBookShelfRepository userBookShelfRepository;

	@Mock
	private ProfileAccessService profileAccessService;

	private ShelfService shelfService;

	@BeforeEach
	void setUp() {
		shelfService = new ShelfService(userBookShelfRepository, profileAccessService);
	}

	@Test
	void upsert_shouldCreateWhenMissing() {
		Long profileId = 7L;
		Long bookId = 9L;
		UserProfile profile = profile(profileId, 100L);
		Book book = book(bookId, "Book A", "isbn-a");
		UserPrincipal principal = principal(100L);

		when(profileAccessService.assertOwnerProfile(profileId, principal)).thenReturn(profile);
		when(profileAccessService.getBookOrThrow(bookId)).thenReturn(book);
		when(userBookShelfRepository.findByUserProfileIdAndBookId(profileId, bookId)).thenReturn(Optional.empty());
		when(userBookShelfRepository.save(any(UserBookShelf.class))).thenAnswer(invocation -> {
			UserBookShelf saved = invocation.getArgument(0);
			saved.setId(1L);
			saved.setUpdatedAt(Instant.parse("2026-04-27T00:00:00Z"));
			return saved;
		});

		ShelfResponse response = shelfService.upsert(profileId, bookId, principal,
				new ShelfUpsertRequest(ShelfStatus.READING));

		assertEquals(1L, response.id());
		assertEquals(ShelfStatus.READING, response.status());
	}

	@Test
	void getBooksByProfileAndShelf_shouldReturnBooksOnShelf() {
		Long profileId = 7L;
		UserProfile profile = profile(profileId, 100L);
		Book book = book(9L, "Book A", "isbn-a");
		UserBookShelf shelf = new UserBookShelf();
		shelf.setId(10L);
		shelf.setUserProfile(profile);
		shelf.setBook(book);
		shelf.setStatus(ShelfStatus.READING);
		shelf.setUpdatedAt(Instant.parse("2026-04-27T00:00:00Z"));

		when(profileAccessService.getProfileOrThrow(profileId)).thenReturn(profile);
		when(userBookShelfRepository.findByUserProfileIdAndStatus(profileId, ShelfStatus.READING)).thenReturn(List.of(shelf));

		List<ShelfBookResponse> result = shelfService.getBooksByProfileAndShelf(profileId, ShelfStatus.READING);

		assertEquals(1, result.size());
		assertEquals(9L, result.get(0).bookId());
		assertEquals("Book A", result.get(0).title());
	}

	private UserProfile profile(Long profileId, Long userId) {
		UserProfile profile = new UserProfile();
		profile.setId(profileId);
		UserAccount user = new UserAccount();
		user.setId(userId);
		user.setEmail("p@example.com");
		user.setPassword("hash");
		profile.setUser(user);
		return profile;
	}

	private Book book(Long bookId, String title, String isbn) {
		Book book = new Book();
		book.setId(bookId);
		book.setTitle(title);
		book.setIsbn(isbn);
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
