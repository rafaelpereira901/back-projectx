package com.agoracorp.projectx.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agoracorp.projectx.model.Author;
import com.agoracorp.projectx.service.AuthorService;

@RestController
@RequestMapping("/authors")
public class AuthorController {

	private final AuthorService authorService;

	public AuthorController(AuthorService authorService) {
		this.authorService = authorService;
	}

	@GetMapping
	public ResponseEntity<List<Author>> getAuthors() {
		return new ResponseEntity<>(authorService.getAllAuthors(), HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Author> getAuthorById(@PathVariable Long id) {
		return new ResponseEntity<>(authorService.getAuthorById(id), HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<Author> createAuthor(@Valid @RequestBody Author author) {
		return new ResponseEntity<>(authorService.createAuthor(author), HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Author> updateAuthor(@PathVariable Long id, @Valid @RequestBody Author author) {
		return new ResponseEntity<>(authorService.updateAuthor(id, author), HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
		authorService.deleteAuthor(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
