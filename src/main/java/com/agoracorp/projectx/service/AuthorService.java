package com.agoracorp.projectx.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.agoracorp.projectx.model.Author;
import com.agoracorp.projectx.repository.AuthorRepository;

@Service
public class AuthorService {

	private final AuthorRepository authorRepository;

	public AuthorService(AuthorRepository authorRepository) {
		this.authorRepository = authorRepository;
	}

	@Transactional(readOnly = true)
	public List<Author> getAllAuthors() {
		return authorRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Author getAuthorById(Long id) {
		return findByIdOrThrow(id);
	}

	@Transactional
	public Author createAuthor(Author author) {
		author.setId(null);

		if (author.getName() == null || author.getName().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Author name is required");
		}

		return authorRepository.save(author);
	}

	@Transactional
	public Author updateAuthor(Long id, Author updatedAuthor) {
		Author existingAuthor = findByIdOrThrow(id);
		existingAuthor.setName(updatedAuthor.getName());
		existingAuthor.setDescription(updatedAuthor.getDescription());
		return authorRepository.save(existingAuthor);
	}

	@Transactional
	public void deleteAuthor(Long id) {
		Author existingAuthor = findByIdOrThrow(id);
		authorRepository.delete(existingAuthor);
	}

	private Author findByIdOrThrow(Long id) {
		return authorRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author with id " + id + " not found"));
	}
}
