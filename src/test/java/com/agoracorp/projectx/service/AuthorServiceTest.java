package com.agoracorp.projectx.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.agoracorp.projectx.model.Author;
import com.agoracorp.projectx.repository.AuthorRepository;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

	@Mock
	private AuthorRepository authorRepository;

	private AuthorService authorService;

	@BeforeEach
	void setUp() {
		authorService = new AuthorService(authorRepository);
	}

	@Test
	void getAllAuthors_shouldReturnRepositoryData() {
		List<Author> authors = List.of(buildAuthor(1L, "Robert", "Author desc"));
		when(authorRepository.findAll()).thenReturn(authors);

		List<Author> result = authorService.getAllAuthors();

		assertEquals(1, result.size());
		assertEquals("Robert", result.get(0).getName());
	}

	@Test
	void getAuthorById_shouldReturnAuthor_whenFound() {
		Author author = buildAuthor(1L, "Robert", "Author desc");
		when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

		Author result = authorService.getAuthorById(1L);

		assertEquals(1L, result.getId());
	}

	@Test
	void getAuthorById_shouldThrowNotFound_whenMissing() {
		when(authorRepository.findById(99L)).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> authorService.getAuthorById(99L));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertTrue(exception.getReason().contains("99"));
	}

	@Test
	void createAuthor_shouldNullifyIdBeforeSave() {
		Author input = buildAuthor(10L, "Robert", "Author desc");
		when(authorRepository.save(any(Author.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Author result = authorService.createAuthor(input);

		ArgumentCaptor<Author> captor = ArgumentCaptor.forClass(Author.class);
		verify(authorRepository).save(captor.capture());
		assertNull(captor.getValue().getId());
		assertNull(result.getId());
	}

	@Test
	void createAuthor_shouldThrowBadRequest_whenNameIsBlank() {
		Author input = buildAuthor(null, "   ", "Author desc");

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> authorService.createAuthor(input));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		verify(authorRepository, never()).save(any(Author.class));
	}

	@Test
	void updateAuthor_shouldUpdateNameAndDescription() {
		Author existing = buildAuthor(1L, "Old Name", "Old desc");
		Author update = buildAuthor(null, "New Name", "New desc");
		when(authorRepository.findById(1L)).thenReturn(Optional.of(existing));
		when(authorRepository.save(any(Author.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Author result = authorService.updateAuthor(1L, update);

		assertEquals("New Name", result.getName());
		assertEquals("New desc", result.getDescription());
	}

	@Test
	void deleteAuthor_shouldDelete_whenFound() {
		Author existing = buildAuthor(1L, "Robert", "Author desc");
		when(authorRepository.findById(1L)).thenReturn(Optional.of(existing));

		authorService.deleteAuthor(1L);

		verify(authorRepository).delete(existing);
	}

	private Author buildAuthor(Long id, String name, String description) {
		Author author = new Author();
		author.setId(id);
		author.setName(name);
		author.setDescription(description);
		return author;
	}
}
