package com.agoracorp.projectx.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agoracorp.projectx.dto.ReadingHistoryCreateRequest;
import com.agoracorp.projectx.dto.ReadingHistoryResponse;
import com.agoracorp.projectx.model.Book;
import com.agoracorp.projectx.model.ReadingHistory;
import com.agoracorp.projectx.model.UserAccount;
import com.agoracorp.projectx.model.UserProfile;
import com.agoracorp.projectx.repository.ReadingHistoryRepository;
import com.agoracorp.projectx.security.UserPrincipal;

@ExtendWith(MockitoExtension.class)
class ReadingHistoryServiceTest {

	@Mock
	private ReadingHistoryRepository readingHistoryRepository;

	@Mock
	private ProfileAccessService profileAccessService;

	private ReadingHistoryService readingHistoryService;

	@BeforeEach
	void setUp() {
		readingHistoryService = new ReadingHistoryService(readingHistoryRepository, profileAccessService);
	}

	@Test
	void create_shouldPersistNewHistoryEntry() {
		Long profileId = 2L;
		Long bookId = 4L;
		UserProfile profile = profile(profileId, 10L);
		Book book = book(bookId);
		UserPrincipal principal = principal(10L);

		when(profileAccessService.assertOwnerProfile(profileId, principal)).thenReturn(profile);
		when(profileAccessService.getBookOrThrow(bookId)).thenReturn(book);
		when(readingHistoryRepository.save(any(ReadingHistory.class))).thenAnswer(invocation -> {
			ReadingHistory saved = invocation.getArgument(0);
			saved.setId(33L);
			saved.setCreatedAt(Instant.parse("2026-04-27T00:00:00Z"));
			return saved;
		});

		ReadingHistoryResponse response = readingHistoryService.create(profileId, bookId, principal,
				new ReadingHistoryCreateRequest("chapter one notes"));

		ArgumentCaptor<ReadingHistory> captor = ArgumentCaptor.forClass(ReadingHistory.class);
		verify(readingHistoryRepository).save(captor.capture());
		assertEquals("chapter one notes", captor.getValue().getComment());
		assertEquals(33L, response.id());
		assertEquals(profileId, response.profileId());
	}

	@Test
	void getByProfileAndBook_shouldReturnAllEntriesForPair() {
		Long profileId = 2L;
		Long bookId = 4L;
		UserProfile profile = profile(profileId, 10L);
		Book book = book(bookId);

		ReadingHistory first = new ReadingHistory();
		first.setId(1L);
		first.setUserProfile(profile);
		first.setBook(book);
		first.setComment("start");
		first.setCreatedAt(Instant.parse("2026-04-20T00:00:00Z"));

		ReadingHistory second = new ReadingHistory();
		second.setId(2L);
		second.setUserProfile(profile);
		second.setBook(book);
		second.setComment("middle");
		second.setCreatedAt(Instant.parse("2026-04-21T00:00:00Z"));

		when(profileAccessService.getProfileOrThrow(profileId)).thenReturn(profile);
		when(profileAccessService.getBookOrThrow(bookId)).thenReturn(book);
		when(readingHistoryRepository.findByUserProfileIdAndBookId(profileId, bookId)).thenReturn(List.of(first, second));

		List<ReadingHistoryResponse> result = readingHistoryService.getByProfileAndBook(profileId, bookId);

		assertEquals(2, result.size());
		assertEquals("start", result.get(0).comment());
		assertEquals("middle", result.get(1).comment());
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
