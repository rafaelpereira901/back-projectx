package com.agoracorp.projectx.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
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
import com.agoracorp.projectx.model.Book;
import com.agoracorp.projectx.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

	@Mock
	private BookRepository bookRepository;

	private BookService bookService;

	@BeforeEach
	void setUp() {
		bookService = new BookService(bookRepository);
	}

	@Test
	void getAllBooks_shouldReturnRepositoryData() {
		List<Book> books = List.of(buildBook(1L, "Old title"));
		when(bookRepository.findAll()).thenReturn(books);

		List<Book> result = bookService.getAllBooks();

		assertEquals(1, result.size());
		assertEquals("Old title", result.get(0).getTitle());
	}

	@Test
	void getBookById_shouldReturnBook_whenFound() {
		Book book = buildBook(1L, "Old title");
		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

		Book result = bookService.getBookById(1L);

		assertEquals(1L, result.getId());
	}

	@Test
	void getBookById_shouldThrowNotFound_whenMissing() {
		when(bookRepository.findById(99L)).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> bookService.getBookById(99L));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertTrue(exception.getReason().contains("99"));
	}

	@Test
	void createBook_shouldNullifyIdBeforeSave() {
		Book input = buildBook(99L, "Created title");
		when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Book result = bookService.createBook(input);

		ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
		verify(bookRepository).save(captor.capture());
		assertNull(captor.getValue().getId());
		assertNull(result.getId());
	}

	@Test
	void updateBook_shouldUpdateAllEditableFields() {
		Book existing = buildBook(1L, "Old title");
		Book update = buildBook(null, "New title");
		update.setIsbn("999-NEW");
		update.setDescription("new description");
		update.setGenres(List.of("Genre A"));
		update.setPublishedDate(LocalDate.of(2020, 1, 1));

		Author newAuthor = new Author();
		newAuthor.setId(2L);
		newAuthor.setName("New Author");
		newAuthor.setDescription("Author Desc");
		update.setAuthor(newAuthor);

		when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));
		when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Book result = bookService.updateBook(1L, update);

		assertEquals("999-NEW", result.getIsbn());
		assertEquals("New title", result.getTitle());
		assertEquals("new description", result.getDescription());
		assertEquals(2L, result.getAuthor().getId());
		assertEquals(1, result.getGenres().size());
		assertEquals(LocalDate.of(2020, 1, 1), result.getPublishedDate());
	}

	@Test
	void deleteBook_shouldDelete_whenFound() {
		Book existing = buildBook(1L, "Old title");
		when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));

		bookService.deleteBook(1L);

		verify(bookRepository).delete(existing);
	}

	private Book buildBook(Long id, String title) {
		Book book = new Book();
		book.setId(id);
		book.setIsbn("123-456");
		book.setTitle(title);
		book.setDescription("desc");

		Author author = new Author();
		author.setId(1L);
		author.setName("Author Name");
		author.setDescription("Author Desc");
		book.setAuthor(author);

		book.setGenres(List.of("Tech"));
		book.setPublishedDate(LocalDate.of(2017, 9, 20));
		return book;
	}
}
