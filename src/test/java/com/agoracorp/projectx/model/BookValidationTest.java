package com.agoracorp.projectx.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class BookValidationTest {

	private static Validator validator;

	@BeforeAll
	static void setUpValidator() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void shouldFailValidation_whenRequiredFieldsAreMissingOrInvalid() {
		Book book = new Book();
		book.setIsbn(" ");
		book.setTitle(" ");
		book.setDescription(" ");
		book.setAuthor(null);
		book.setGenres(List.of("Tech", " "));
		book.setPublishedDate(LocalDate.now().plusDays(1));

		Set<ConstraintViolation<Book>> violations = validator.validate(book);
		Set<String> fields = violations.stream().map(v -> v.getPropertyPath().toString()).collect(Collectors.toSet());

		assertTrue(fields.contains("isbn"));
		assertTrue(fields.contains("title"));
		assertTrue(fields.contains("description"));
		assertTrue(fields.contains("author"));
		assertTrue(fields.contains("genres[1].<list element>"));
		assertTrue(fields.contains("publishedDate"));
	}

	@Test
	void shouldPassValidation_whenBookIsValid() {
		Author author = new Author();
		author.setName("Robert");
		author.setDescription("Author description");

		Book book = new Book();
		book.setIsbn("978-0-123456-47-2");
		book.setTitle("Clean Architecture");
		book.setDescription("A guide to software architecture and design.");
		book.setAuthor(author);
		book.setGenres(List.of("Software Engineering", "Architecture"));
		book.setPublishedDate(LocalDate.now());

		Set<ConstraintViolation<Book>> violations = validator.validate(book);

		assertTrue(violations.isEmpty());
	}
}
