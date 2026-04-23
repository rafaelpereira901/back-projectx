package com.agoracorp.projectx.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.agoracorp.projectx.model.Author;
import com.agoracorp.projectx.service.AuthorService;

@ExtendWith(MockitoExtension.class)
class AuthorControllerTest {

	@Mock
	private AuthorService authorService;

	private AuthorController authorController;

	@BeforeEach
	void setUp() {
		authorController = new AuthorController(authorService);
	}

	@Test
	void getAuthors_shouldReturnOk() {
		when(authorService.getAllAuthors()).thenReturn(List.of(buildAuthor(1L)));

		ResponseEntity<List<Author>> response = authorController.getAuthors();

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(1, response.getBody().size());
	}

	@Test
	void getAuthorById_shouldReturnOk() {
		Author author = buildAuthor(1L);
		when(authorService.getAuthorById(1L)).thenReturn(author);

		ResponseEntity<Author> response = authorController.getAuthorById(1L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(1L, response.getBody().getId());
	}

	@Test
	void createAuthor_shouldReturnCreated() {
		Author author = buildAuthor(1L);
		when(authorService.createAuthor(author)).thenReturn(author);

		ResponseEntity<Author> response = authorController.createAuthor(author);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals("Robert", response.getBody().getName());
	}

	@Test
	void updateAuthor_shouldReturnOk() {
		Author author = buildAuthor(1L);
		when(authorService.updateAuthor(1L, author)).thenReturn(author);

		ResponseEntity<Author> response = authorController.updateAuthor(1L, author);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	void deleteAuthor_shouldReturnNoContent() {
		ResponseEntity<Void> response = authorController.deleteAuthor(1L);

		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		verify(authorService).deleteAuthor(1L);
	}

	private Author buildAuthor(Long id) {
		Author author = new Author();
		author.setId(id);
		author.setName("Robert");
		author.setDescription("Author description");
		return author;
	}
}
