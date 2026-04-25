package com.agoracorp.projectx.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class AuthorValidationTest {

	private static Validator validator;

	@BeforeAll
	static void setUpValidator() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void shouldFailValidation_whenNameAndDescriptionAreBlank() {
		Author author = new Author();
		author.setName(" ");
		author.setDescription(" ");

		Set<ConstraintViolation<Author>> violations = validator.validate(author);
		Set<String> fields = violations.stream().map(v -> v.getPropertyPath().toString()).collect(Collectors.toSet());

		assertEquals(2, violations.size());
		assertTrue(fields.contains("name"));
		assertTrue(fields.contains("description"));
	}

	@Test
	void shouldPassValidation_whenNameAndDescriptionAreValid() {
		Author author = new Author();
		author.setName("Robert");
		author.setDescription("Author description");

		Set<ConstraintViolation<Author>> violations = validator.validate(author);

		assertTrue(violations.isEmpty());
	}
}
