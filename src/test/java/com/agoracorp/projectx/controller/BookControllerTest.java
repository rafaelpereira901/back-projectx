package com.agoracorp.projectx.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.agoracorp.projectx.model.Author;
import com.agoracorp.projectx.model.Book;
import com.agoracorp.projectx.service.BookService;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

	@Mock
	private BookService bookService;

	private BookController bookController;

	@BeforeEach
	void setUp() {
		bookController = new BookController(bookService);
	}

	@Test
	void getBooks_shouldReturnOk() {
		when(bookService.getAllBooks()).thenReturn(List.of(buildBook(1L)));

		ResponseEntity<List<Book>> response = bookController.getBooks();

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(1, response.getBody().size());
	}

	@Test
	void getBookById_shouldReturnOk() {
		Book book = buildBook(1L);
		when(bookService.getBookById(1L)).thenReturn(book);

		ResponseEntity<Book> response = bookController.getBookById(1L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(1L, response.getBody().getId());
	}

	@Test
	void createBook_shouldReturnCreated() {
		Book book = buildBook(1L);
		when(bookService.createBook(book)).thenReturn(book);

		ResponseEntity<Book> response = bookController.createBook(book);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals("Clean Architecture", response.getBody().getTitle());
	}

	@Test
	void updateBook_shouldReturnOk() {
		Book book = buildBook(1L);
		when(bookService.updateBook(1L, book)).thenReturn(book);

		ResponseEntity<Book> response = bookController.updateBook(1L, book);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	void deleteBook_shouldReturnNoContent() {
		ResponseEntity<Void> response = bookController.deleteBook(1L);

		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		verify(bookService).deleteBook(1L);
	}

	private Book buildBook(Long id) {
		Book book = new Book();
		book.setId(id);
		book.setIsbn("978-0-123456-47-2");
		book.setTitle("Clean Architecture");
		book.setDescription("A guide to software architecture and design.");

		Author author = new Author();
		author.setId(1L);
		author.setName("Robert");
		author.setDescription("Author description");
		book.setAuthor(author);

		book.setGenres(List.of("Software Engineering"));
		book.setPublishedDate(LocalDate.of(2017, 9, 20));
		return book;
	}
}
